from fastapi import APIRouter, Depends, Query
from sqlalchemy.orm import Session

from app.database import get_db
from app.models.student import Student
from app.schemas.sync import SyncPullResponse, SyncPushRequest, SyncPushResponse
from app.security import require_role
from app.services.sync_service import process_sync_batch, pull_sync_changes

router = APIRouter(prefix="/sync", tags=["sync"])


@router.post("/push", response_model=SyncPushResponse)
def push_offline_events(
    req: SyncPushRequest,
    student: Student = Depends(require_role("student")),
    db: Session = Depends(get_db),
):
    results, latest_cursor = process_sync_batch(db, student, req.events)
    return SyncPushResponse(
        results=results,
        latest_cursor=latest_cursor,
        total_xp=student.total_xp,
        streak_count=student.streak_count,
    )


@router.get("/pull", response_model=SyncPullResponse)
def pull_offline_changes(
    since: int = Query(default=0, ge=0),
    limit: int = Query(default=50, ge=1, le=200),
    student: Student = Depends(require_role("student")),
    db: Session = Depends(get_db),
):
    events, next_cursor, has_more, server_state = pull_sync_changes(
        db=db,
        student=student,
        since_cursor=since,
        limit=limit,
    )
    return SyncPullResponse(
        since_cursor=since,
        next_cursor=next_cursor,
        has_more=has_more,
        events=events,
        server_state=server_state,
    )
