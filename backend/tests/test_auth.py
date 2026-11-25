from fastapi.testclient import TestClient

from app.models.student import Student
from app.security import create_access_token


def test_health_sqlite_wal_and_fk(client: TestClient):
    resp = client.get("/health")
    assert resp.status_code == 200
    data = resp.json()
    assert data["database"] == "sqlite"
    assert data["journal_mode"].lower() == "wal"
    assert data["foreign_keys"] is True


def test_register_login_profile_and_logout_revocation(client: TestClient):
    reg = client.post(
        "/api/v1/auth/register",
        json={
            "student_code": "STU_RAJU_02",
            "device_id": "device-raju",
            "passcode": "pass9876",
            "name": "Raju Kumar",
            "preferred_lang": "kn",
        },
    )
    assert reg.status_code == 201
    token = reg.json()["access_token"]
    assert reg.json()["student"]["role"] == "student"

    dup = client.post(
        "/api/v1/auth/register",
        json={
            "student_code": "STU_RAJU_02",
            "device_id": "device-raju",
            "passcode": "pass9876",
        },
    )
    assert dup.status_code == 409
    assert dup.json()["error"]["code"] == "HTTP_409"

    bad_login = client.post(
        "/api/v1/auth/login",
        json={"student_code": "STU_RAJU_02", "passcode": "wrongpass"},
    )
    assert bad_login.status_code == 401

    login = client.post(
        "/api/v1/auth/login",
        json={"student_code": "STU_RAJU_02", "passcode": "pass9876"},
    )
    assert login.status_code == 200
    headers = {"Authorization": f"Bearer {login.json()['access_token']}"}

    me = client.get("/api/v1/auth/me", headers=headers)
    assert me.status_code == 200
    assert me.json()["name"] == "Raju Kumar"

    upd = client.patch(
        "/api/v1/auth/profile",
        headers=headers,
        json={"village": "Ramanagara", "preferred_lang": "en"},
    )
    assert upd.status_code == 200
    assert upd.json()["village"] == "Ramanagara"
    assert upd.json()["preferred_lang"] == "en"

    logout = client.post("/api/v1/auth/logout", headers=headers)
    assert logout.status_code == 204

    after_logout = client.get("/api/v1/auth/me", headers=headers)
    assert after_logout.status_code == 401


def test_rbac_blocks_non_student_role_and_unauthenticated(client: TestClient, auth_headers: dict[str, str], db_session_factory):
    unauth = client.get("/api/v1/content/packs")
    assert unauth.status_code == 401

    me = client.get("/api/v1/auth/me", headers=auth_headers)
    student_id = me.json()["id"]

    # Token forged with an unsupported role must be rejected with 403
    wrong_role_token, _jti, _exp = create_access_token(subject=student_id, role="admin")
    forbidden = client.get(
        "/api/v1/auth/me",
        headers={"Authorization": f"Bearer {wrong_role_token}"},
    )
    assert forbidden.status_code == 403
