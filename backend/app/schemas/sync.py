from datetime import datetime
from typing import Literal
from pydantic import BaseModel, Field


SyncEventType = Literal[
    "quiz_completed",
    "quest_claimed",
    "p2p_transfer_completed",
    "streak_updated",
    "profile_updated",
    "lesson_viewed",
]


class SyncEventIn(BaseModel):
    event_id: str = Field(..., min_length=8, max_length=64)
    idempotency_key: str = Field(..., min_length=8, max_length=128)
    event_type: SyncEventType
    client_timestamp: datetime
    payload: dict = Field(default_factory=dict)


class SyncPushRequest(BaseModel):
    events: list[SyncEventIn] = Field(..., min_length=1, max_length=100)


class SyncEventResult(BaseModel):
    event_id: str
    idempotency_key: str
    status: Literal["applied", "duplicate_ignored", "conflict_resolved", "rejected"]
    server_seq: int | None = None
    resolution_note: str | None = None


class SyncPushResponse(BaseModel):
    results: list[SyncEventResult]
    latest_cursor: int
    total_xp: int
    streak_count: int


class SyncPullItem(BaseModel):
    server_seq: int
    event_id: str
    event_type: str
    payload: dict
    resolution_status: str
    created_at: datetime


class SyncPullResponse(BaseModel):
    since_cursor: int
    next_cursor: int
    has_more: bool
    events: list[SyncPullItem]
    server_state: dict
