from datetime import datetime
from sqlalchemy import DateTime, ForeignKey, Index, Integer, String, Text
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.database import Base
from app.models.student import new_uuid, utc_now


class SyncEvent(Base):
    __tablename__ = "sync_events"
    __table_args__ = (
        Index("ix_sync_events_student_seq", "student_id", "server_seq"),
    )

    id: Mapped[str] = mapped_column(String(36), primary_key=True, default=new_uuid)
    student_id: Mapped[str] = mapped_column(
        String(36), ForeignKey("students.id", ondelete="CASCADE"), index=True, nullable=False
    )
    event_id: Mapped[str] = mapped_column(String(64), index=True, nullable=False)
    idempotency_key: Mapped[str] = mapped_column(String(128), unique=True, index=True, nullable=False)
    event_type: Mapped[str] = mapped_column(String(48), index=True, nullable=False)
    payload_json: Mapped[str] = mapped_column(Text, nullable=False)
    client_timestamp: Mapped[datetime] = mapped_column(DateTime(timezone=True), nullable=False)
    server_seq: Mapped[int] = mapped_column(Integer, unique=True, index=True, nullable=False)
    resolution_status: Mapped[str] = mapped_column(String(48), default="applied", nullable=False)

    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=utc_now, nullable=False)
    updated_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), default=utc_now, onupdate=utc_now, nullable=False
    )

    student = relationship("Student", back_populates="sync_events")
