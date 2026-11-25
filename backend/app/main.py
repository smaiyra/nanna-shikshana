from contextlib import asynccontextmanager

from fastapi import APIRouter, FastAPI, HTTPException, Request, status
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse
from sqlalchemy import text

from app.config import settings
from app.database import SessionLocal, engine
from app.routers import auth, content, gamification, progress, sync
from app.schemas.common import ErrorDetail, ErrorResponse
from app.services.seed import seed_initial_data


@asynccontextmanager
async def lifespan(_app: FastAPI):
    with SessionLocal() as db:
        try:
            seed_initial_data(db)
        except Exception:
            db.rollback()
    yield


def create_app() -> FastAPI:
    app = FastAPI(
        title=settings.APP_NAME,
        version="1.0.0",
        description="Offline-first sync & content pack backend for Nanna Shikshana (ನಮ್ಮ ಶಿಕ್ಷಣ)",
        lifespan=lifespan,
    )

    @app.exception_handler(HTTPException)
    async def http_exception_handler(_request: Request, exc: HTTPException):
        body = ErrorResponse(
            error=ErrorDetail(
                code=f"HTTP_{exc.status_code}",
                message=str(exc.detail),
            )
        )
        return JSONResponse(status_code=exc.status_code, content=body.model_dump())

    @app.exception_handler(RequestValidationError)
    async def validation_exception_handler(_request: Request, exc: RequestValidationError):
        body = ErrorResponse(
            error=ErrorDetail(
                code="VALIDATION_ERROR",
                message="Request validation failed.",
                details=exc.errors(),
            )
        )
        return JSONResponse(
            status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
            content=body.model_dump(),
        )

    @app.get("/health", tags=["health"])
    def health_check():
        with engine.connect() as conn:
            journal_mode = conn.execute(text("PRAGMA journal_mode;")).scalar()
            fk_enabled = conn.execute(text("PRAGMA foreign_keys;")).scalar()
        return {
            "status": "ok",
            "database": "sqlite",
            "journal_mode": journal_mode,
            "foreign_keys": bool(fk_enabled),
        }

    api_v1 = APIRouter(prefix="/api/v1")
    api_v1.include_router(auth.router)
    api_v1.include_router(content.router)
    api_v1.include_router(progress.router)
    api_v1.include_router(gamification.router)
    api_v1.include_router(sync.router)
    app.include_router(api_v1)

    return app


app = create_app()
