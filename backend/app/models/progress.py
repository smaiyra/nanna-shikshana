from datetime import datetime
from sqlalchemy import Boolean, DateTime, ForeignKey, Index, Integer, String, Text, UniqueConstraint
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.database import Base
from app.models.student import new_uuid, utc_now


class LessonProgress(Base):
    __tablename__ = "lesson_progress"
    __table_args__ = (
        UniqueConstraint("student_id", "lesson_key", name="uq_student_lesson"),
        Index("ix_lesson_progress_student_lesson", "student_id", "lesson_key"),
    )

    id: Mapped[str] = mapped_column(String(36), primary_key=True, default=new_uuid)
    student_id: Mapped[str] = mapped_column(
        String(36), ForeignKey("students.id", ondelete="CASCADE"), index=True, nullable=False
    )
    lesson_key: Mapped[str] = mapped_column(
        String(64), ForeignKey("lesson_modules.lesson_key", ondelete="CASCADE"), index=True, nullable=False
    )
    completed: Mapped[bool] = mapped_column(Boolean, default=False, nullable=False)
    best_quiz_score: Mapped[int] = mapped_column(Integer, default=0, nullable=False)
    total_questions: Mapped[int] = mapped_column(Integer, default=3, nullable=False)
    last_studied_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=utc_now, nullable=False)

    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=utc_now, nullable=False)
    updated_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), default=utc_now, onupdate=utc_now, nullable=False
    )

    student = relationship("Student", back_populates="lesson_progress")


class QuizAttempt(Base):
    __tablename__ = "quiz_attempts"

    id: Mapped[str] = mapped_column(String(36), primary_key=True, default=new_uuid)
    student_id: Mapped[str] = mapped_column(
        String(36), ForeignKey("students.id", ondelete="CASCADE"), index=True, nullable=False
    )
    lesson_key: Mapped[str] = mapped_column(
        String(64), ForeignKey("lesson_modules.lesson_key", ondelete="CASCADE"), index=True, nullable=False
    )
    score: Mapped[int] = mapped_column(Integer, nullable=False)
    total_questions: Mapped[int] = mapped_column(Integer, nullable=False)
    xp_awarded: Mapped[int] = mapped_column(Integer, nullable=False)
    answers_json: Mapped[str] = mapped_column(Text, default="[]", nullable=False)
    client_event_id: Mapped[str | None] = mapped_column(String(64), unique=True, index=True, nullable=True)
    attempted_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=utc_now, nullable=False)

    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=utc_now, nullable=False)
    updated_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), default=utc_now, onupdate=utc_now, nullable=False
    )

    student = relationship("Student", back_populates="quiz_attempts")


class P2PTransferLog(Base):
    __tablename__ = "p2p_transfer_logs"

    id: Mapped[str] = mapped_column(String(36), primary_key=True, default=new_uuid)
    student_id: Mapped[str] = mapped_column(
        String(36), ForeignKey("students.id", ondelete="CASCADE"), index=True, nullable=False
    )
    lesson_key: Mapped[str] = mapped_column(
        String(64), ForeignKey("lesson_modules.lesson_key", ondelete="CASCADE"), index=True, nullable=False
    )
    package_name: Mapped[str] = mapped_column(String(120), nullable=False)
    sha256: Mapped[str] = mapped_column(String(64), nullable=False)
    peer_device_name: Mapped[str] = mapped_column(String(120), nullable=False)
    xp_awarded: Mapped[int] = mapped_column(Integer, default=50, nullable=False)
    client_event_id: Mapped[str | None] = mapped_column(String(64), unique=True, index=True, nullable=True)
    transferred_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=utc_now, nullable=False)

    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=utc_now, nullable=False)
    updated_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), default=utc_now, onupdate=utc_now, nullable=False
    )

    student = relationship("Student", back_populates="p2p_transfers")
