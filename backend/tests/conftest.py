from collections.abc import Generator
from pathlib import Path
import tempfile

from alembic import command
from alembic.config import Config
from fastapi.testclient import TestClient
import pytest
from sqlalchemy import create_engine, event
from sqlalchemy.orm import Session, sessionmaker

from app.config import settings
from app.database import get_db
from app.main import create_app
from app.services.seed import seed_initial_data


@pytest.fixture()
def test_env(tmp_path: Path) -> Generator[tuple[TestClient, sessionmaker], None, None]:
    db_file = tmp_path / "test_nanna.db"
    packs_dir = tmp_path / "packs"
    packs_dir.mkdir(parents=True, exist_ok=True)
    db_url = f"sqlite:///{db_file}"

    old_db_url = settings.DATABASE_URL
    old_packs_dir = settings.PACKS_STORAGE_DIR
    settings.DATABASE_URL = db_url
    settings.PACKS_STORAGE_DIR = str(packs_dir)

    alembic_cfg = Config(str(Path(__file__).resolve().parents[1] / "alembic.ini"))
    alembic_cfg.set_main_option("script_location", str(Path(__file__).resolve().parents[1] / "migrations"))
    alembic_cfg.set_main_option("sqlalchemy.url", db_url)
    command.upgrade(alembic_cfg, "head")

    eng = create_engine(db_url, connect_args={"check_same_thread": False}, future=True)

    @event.listens_for(eng, "connect")
    def _set_pragmas(dbapi_connection, _connection_record):
        cur = dbapi_connection.cursor()
        cur.execute("PRAGMA journal_mode=WAL;")
        cur.execute("PRAGMA foreign_keys=ON;")
        cur.close()

    TestingSessionLocal = sessionmaker(bind=eng, autoflush=False, autocommit=False, expire_on_commit=False)
    with TestingSessionLocal() as db:
        seed_initial_data(db, packs_dir=packs_dir)

    app = create_app()

    def override_get_db() -> Generator[Session, None, None]:
        db = TestingSessionLocal()
        try:
            yield db
        finally:
            db.close()

    app.dependency_overrides[get_db] = override_get_db

    with TestClient(app) as client:
        yield client, TestingSessionLocal

    eng.dispose()
    settings.DATABASE_URL = old_db_url
    settings.PACKS_STORAGE_DIR = old_packs_dir


@pytest.fixture()
def client(test_env):
    return test_env[0]


@pytest.fixture()
def db_session_factory(test_env):
    return test_env[1]


@pytest.fixture()
def auth_headers(client: TestClient) -> dict[str, str]:
    resp = client.post(
        "/api/v1/auth/register",
        json={
            "student_code": "STU_ANANDA_01",
            "device_id": "android-redmi-9-001",
            "passcode": "1234",
            "name": "Ananda",
            "village": "Channapatna Rural",
            "school": "Govt High School, Channapatna",
            "preferred_lang": "en",
        },
    )
    assert resp.status_code == 201
    token = resp.json()["access_token"]
    return {"Authorization": f"Bearer {token}"}
