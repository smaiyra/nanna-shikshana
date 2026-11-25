from fastapi.testclient import TestClient

from app.models.student import Student
from app.services.gamification_service import evaluate_streak


def test_quiz_scoring_badges_and_quest_claiming(client: TestClient, auth_headers: dict[str, str]):
    # 100% on Fractions quiz (correct_index: 1, 1, 0)
    quiz = client.post(
        "/api/v1/progress/quizzes/attempt",
        headers=auth_headers,
        json={
            "lesson_key": "math_fractions_ch3",
            "selected_indices": [1, 1, 0],
            "client_event_id": "evt-quiz-1",
        },
    )
    assert quiz.status_code == 200
    qdata = quiz.json()
    assert qdata["score"] == 3
    assert qdata["xp_awarded"] == 60
    assert "First Lesson" in qdata["newly_unlocked_badges"]
    assert "Math Master" in qdata["newly_unlocked_badges"]

    # Complete Science quiz (correct_index: 0, 1, 1)
    sci = client.post(
        "/api/v1/progress/quizzes/attempt",
        headers=auth_headers,
        json={
            "lesson_key": "sci_photosynthesis_ch5",
            "selected_indices": [0, 1, 1],
            "client_event_id": "evt-quiz-2",
        },
    )
    assert sci.status_code == 200
    assert sci.json()["total_xp"] == 120

    # Quests quest_science and quest_quiz_100 should now be claimable
    claim_sci = client.post("/api/v1/gamification/quests/quest_science/claim", headers=auth_headers)
    assert claim_sci.status_code == 200
    assert claim_sci.json()["xp_awarded"] == 50
    assert claim_sci.json()["already_claimed"] is False

    # Re-claiming same quest must not award duplicate XP
    reclaim = client.post("/api/v1/gamification/quests/quest_science/claim", headers=auth_headers)
    assert reclaim.status_code == 200
    assert reclaim.json()["already_claimed"] is True
    assert reclaim.json()["xp_awarded"] == 0


def test_p2p_transfer_badges_and_leaderboard_ranking(client: TestClient, auth_headers: dict[str, str]):
    for idx in range(3):
        resp = client.post(
            "/api/v1/progress/p2p/transfers",
            headers=auth_headers,
            json={
                "lesson_key": "math_fractions_ch3",
                "package_name": "KTBS_Math_Fractions_G6.pkg.zip",
                "sha256": "9f82a17cb583e4091a104f29d9365e8841a02b1c8537b0df73e21190ca38dbb7",
                "peer_device_name": f"Peer Phone {idx}",
                "client_event_id": f"p2p-evt-{idx}",
            },
        )
        assert resp.status_code == 200

    badges = client.get("/api/v1/gamification/badges", headers=auth_headers).json()
    unlocked = {b["badge_key"] for b in badges if b["unlocked"]}
    assert "P2P Sharer" in unlocked
    assert "P2P Pioneer" in unlocked

    lb = client.get("/api/v1/gamification/leaderboard", headers=auth_headers).json()
    assert lb["pagination"]["total_items"] == 6
    ranks = [row["rank"] for row in lb["items"]]
    assert ranks == [1, 2, 3, 4, 5, 6]
    assert any(row["is_current_user"] for row in lb["items"])


def test_streak_consecutive_freeze_and_reset(client: TestClient, auth_headers: dict[str, str], db_session_factory):
    me = client.get("/api/v1/auth/me", headers=auth_headers).json()
    student_id = me["id"]

    with db_session_factory() as db:
        student = db.get(Student, student_id)
        assert student is not None
        student.streak_count = 4
        student.streak_freezes_left = 1
        student.last_active_date = "2026-11-20"
        db.commit()

        # +1 consecutive day -> streak becomes 5
        res1 = evaluate_streak(db, student, "2026-11-21")
        assert res1["status"] == "incremented"
        assert student.streak_count == 5

        # Missed 1 day (diff = 2) -> consumes 1 freeze, streak stays 5
        res2 = evaluate_streak(db, student, "2026-11-23")
        assert res2["status"] == "frozen"
        assert student.streak_count == 5
        assert student.streak_freezes_left == 0

        # Missed 1 day again with 0 freezes -> resets to 1
        res3 = evaluate_streak(db, student, "2026-11-25")
        assert res3["status"] == "reset"
        assert student.streak_count == 1
