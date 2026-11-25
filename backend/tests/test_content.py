import hashlib
import io
import zipfile

from fastapi.testclient import TestClient


def test_list_and_filter_content_packs_paginated(client: TestClient, auth_headers: dict[str, str]):
    resp = client.get("/api/v1/content/packs?page=1&page_size=2", headers=auth_headers)
    assert resp.status_code == 200
    data = resp.json()
    assert len(data["items"]) == 2
    assert data["pagination"]["total_items"] == 4
    assert data["pagination"]["total_pages"] == 2

    math_resp = client.get("/api/v1/content/packs?subject=math", headers=auth_headers)
    assert math_resp.status_code == 200
    assert len(math_resp.json()["items"]) == 1
    assert math_resp.json()["items"][0]["lesson"]["lesson_key"] == "math_fractions_ch3"


def test_version_check_and_download_zip_with_sha256(client: TestClient, auth_headers: dict[str, str]):
    manifest = client.get("/api/v1/content/packs/manifest", headers=auth_headers)
    assert manifest.status_code == 200
    entries = manifest.json()
    assert len(entries) == 4

    check = client.post(
        "/api/v1/content/packs/version-check",
        headers=auth_headers,
        json={"installed_versions": {"pack_math_fractions_ch3": 1}},
    )
    assert check.status_code == 200
    check_data = check.json()
    assert "pack_math_fractions_ch3" in check_data["up_to_date_codes"]
    assert len(check_data["updates_available"]) == 3

    detail = client.get("/api/v1/content/packs/pack_math_fractions_ch3", headers=auth_headers)
    assert detail.status_code == 200
    assert len(detail.json()["lesson_detail"]["quiz"]) == 3

    dl = client.get("/api/v1/content/packs/pack_math_fractions_ch3/download", headers=auth_headers)
    assert dl.status_code == 200
    expected_sha = dl.headers["X-Pack-SHA256"]
    actual_sha = hashlib.sha256(dl.content).hexdigest()
    assert actual_sha == expected_sha

    with zipfile.ZipFile(io.BytesIO(dl.content), "r") as zf:
        assert "manifest.json" in zf.namelist()
        assert "lesson.json" in zf.namelist()
