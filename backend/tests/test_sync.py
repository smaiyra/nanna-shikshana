from datetime import datetime, timezone
import uuid

from fastapi.testclient import TestClient


def test_sync_push_idempotency_and_conflict_resolution_and_pull_cursor(
    client: TestClient,
    auth_headers: dict[str, str],
):
    e1 = str(uuid.uuid4())
    e2 = str(uuid.uuid4())
    e3 = str(uuid.uuid4())

    push1 = client.post(
        "/api/v1/sync/push",
        headers=auth_headers,
        json={
            "events": [
                {
                    "event_id": e1,
                    "idempotency_key": f"idem-{e1}",
                    "event_type": "quiz_completed",
                    "client_timestamp": "2026-11-24T10:00:00Z",
                    "payload": {
                        "lesson_key": "math_fractions_ch3",
                        "score": 3,
                        "total_questions": 3,
                        "selected_indices": [1, 1, 0],
                    },
                },
                {
                    "event_id": e2,
                    "idempotency_key": f"idem-{e2}",
                    "event_type": "p2p_transfer_completed",
                    "client_timestamp": "2026-11-24T10:05:00Z",
                    "payload": {
                        "lesson_key": "math_fractions_ch3",
                        "package_name": "KTBS_Math_Fractions_G6.pkg.zip",
                        "sha256": "abc123",
                        "peer_device_name": "Raju's Phone",
                    },
                },
            ]
        },
    )
    assert push1.status_code == 200
    d1 = push1.json()
    assert [r["status"] for r in d1["results"]] == ["applied", "applied"]
    assert d1["total_xp"] == 110  # 60 quiz + 50 P2P
    cursor_after_first = d1["latest_cursor"]

    # Re-sending exact same idempotency keys must be safely ignored without adding XP
    push_dup = client.post(
        "/api/v1/sync/push",
        headers=auth_headers,
        json={
            "events": [
                {
                    "event_id": e1,
                    "idempotency_key": f"idem-{e1}",
                    "event_type": "quiz_completed",
                    "client_timestamp": "2026-11-24T10:00:00Z",
                    "payload": {
                        "lesson_key": "math_fractions_ch3",
                        "score": 3,
                        "total_questions": 3,
                    },
                }
            ]
        },
    )
    assert push_dup.status_code == 200
    assert push_dup.json()["results"][0]["status"] == "duplicate_ignored"
    assert push_dup.json()["total_xp"] == 110

    # Lower score later on same lesson resolves conflict via monotonic MAX best_quiz_score
    push_conflict = client.post(
        "/api/v1/sync/push",
        headers=auth_headers,
        json={
            "events": [
                {
                    "event_id": e3,
                    "idempotency_key": f"idem-{e3}",
                    "event_type": "quiz_completed",
                    "client_timestamp": datetime.now(timezone.utc).isoformat(),
                    "payload": {
                        "lesson_key": "math_fractions_ch3",
                        "score": 1,
                        "total_questions": 3,
                    },
                }
            ]
        },
    )
    assert push_conflict.status_code == 200
    assert push_conflict.json()["results"][0]["status"] == "conflict_resolved"

    # Pull from cursor 0 returns all 3 events; pull from cursor_after_first returns only the 3rd
    pull_all = client.get("/api/v1/sync/pull?since=0", headers=auth_headers)
    assert pull_all.status_code == 200
    assert len(pull_all.json()["events"]) == 3
    assert pull_all.json()["server_state"]["completed_quizzes"]["math_fractions_ch3"] == 3

    pull_delta = client.get(f"/api/v1/sync/pull?since={cursor_after_first}", headers=auth_headers)
    assert pull_delta.status_code == 200
    assert len(pull_delta.json()["events"]) == 1
    assert pull_delta.json()["events"][0]["event_id"] == e3
