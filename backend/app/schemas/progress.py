from datetime import datetime
from pydantic import BaseModel, ConfigDict, Field


class LessonViewRequest(BaseModel):
    studied_at: datetime | None = None


class LessonProgressResponse(BaseModel):
    id: str
    lesson_key: str
    completed: bool
    best_quiz_score: int
    total_questions: int
    last_studied_at: datetime

    model_config = ConfigDict(from_attributes=True)


class QuizAttemptRequest(BaseModel):
    lesson_key: str = Field(..., min_length=2, max_length=64)
    selected_indices: list[int] = Field(..., min_length=1, max_length=20)
    client_event_id: str | None = Field(default=None, max_length=64)
    attempted_at: datetime | None = None


class QuizAttemptResponse(BaseModel):
    attempt_id: str
    lesson_key: str
    score: int
    total_questions: int
    xp_awarded: int
    total_xp: int
    current_level: str
    newly_unlocked_badges: list[str]
    best_quiz_score: int
    attempted_at: datetime


class P2PTransferRequest(BaseModel):
    lesson_key: str = Field(..., min_length=2, max_length=64)
    package_name: str = Field(..., min_length=2, max_length=120)
    sha256: str = Field(..., min_length=16, max_length=64)
    peer_device_name: str = Field(..., min_length=1, max_length=120)
    client_event_id: str | None = Field(default=None, max_length=64)
    transferred_at: datetime | None = None


class P2PTransferResponse(BaseModel):
    transfer_id: str
    lesson_key: str
    verified: bool
    xp_awarded: int
    total_xp: int
    newly_unlocked_badges: list[str]
    transferred_at: datetime
