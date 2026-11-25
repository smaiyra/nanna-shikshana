from datetime import datetime, timezone
import uuid

from sqlalchemy import DateTime, ForeignKey, Integer, String
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.database import Base


def utc_now() -> datetime:
    return datetime.now(timezone.utc)


def new_uuid() -> str:
    return str(uuid.uuid4())


class Student(Base):
    __tablename__ = "students"

    id: Mapped[str] = mapped_column(String(36), primary_key=True, default=new_uuid)
    student_code: Mapped[str] = mapped_column(String(64), unique=True, index=True, nullable=False)
    device_id: Mapped[str] = mapped_column(String(128), index=True, nullable=False)
    secret_hash: Mapped[str] = mapped_column(String(255), nullable=False)
    role: Mapped[str] = mapped_column(String(24), default="student", nullable=False)

    name: Mapped[str] = mapped_column(String(120), nullable=False)
    village: Mapped[str] = mapped_column(String(120), default="Channapatna Rural", nullable=False)
    school: Mapped[str] = mapped_column(String(180), default="Govt High School, Channapatna", nullable=False)
    preferred_lang: Mapped[str] = mapped_column(String(8), default="en", nullable=False)

    total_xp: Mapped[int] = mapped_column(Integer, default=450, nullable=False)
    current_level: Mapped[str] = mapped_column(String(80), default="Level 2: District Champion", nullable=False)
    streak_count: Mapped[int] = mapped_column(Integer, default=5, nullable=False)
    streak_freezes_left: Mapped[int] = mapped_column(Integer, default=2, nullable=False)
    last_active_date: Mapped[str] = mapped_column(String(10), nullable=False)

    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=utc_now, nullable=False)
    updated_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), default=utc_now, onupdate=utc_now, nullable=False
    )

    lesson_progress = relationship("LessonProgress", back_populates="student", cascade="all, delete-orphan")
    quiz_attempts = relationship("QuizAttempt", back_populates="student", cascade="all, delete-orphan")
    p2p_transfers = relationship("P2PTransferLog", back_populates="student", cascade="all, delete-orphan")
    badges = relationship("StudentBadge", back_populates="student", cascade="all, delete-orphan")
    quest_progress = relationship("QuestProgress", back_populates="student", cascade="all, delete-orphan")
    sync_events = relationship("SyncEvent", back_populates="student", cascade="all, delete-orphan")


class RevokedToken(Base):
    __tablename__ = "revoked_tokens"

    id: Mapped[str] = mapped_column(String(36), primary_key=True, default=new_uuid)
    jti: Mapped[str] = mapped_column(String(64), unique=True, index=True, nullable=False)
    student_id: Mapped[str] = mapped_column(
        String(36), ForeignKey("students.id", ondelete="CASCADE"), index=True, nullable=False
    )
    expires_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), nullable=False)
    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=utc_now, nullable=False)
    updated_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), default=utc_now, onupdate=utc_now, nullable=False
    )
