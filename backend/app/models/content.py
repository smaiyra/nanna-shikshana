from datetime import datetime
from sqlalchemy import DateTime, Float, ForeignKey, Integer, String, Text
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.database import Base
from app.models.student import new_uuid, utc_now


class ContentPack(Base):
    __tablename__ = "content_packs"

    id: Mapped[str] = mapped_column(String(36), primary_key=True, default=new_uuid)
    pack_code: Mapped[str] = mapped_column(String(64), unique=True, index=True, nullable=False)
    subject: Mapped[str] = mapped_column(String(32), index=True, nullable=False)
    grade: Mapped[str] = mapped_column(String(48), nullable=False)
    version: Mapped[int] = mapped_column(Integer, default=1, nullable=False)
    package_name: Mapped[str] = mapped_column(String(120), unique=True, nullable=False)
    file_size_mb: Mapped[float] = mapped_column(Float, nullable=False)
    sha256: Mapped[str] = mapped_column(String(64), nullable=False)
    archive_path: Mapped[str] = mapped_column(String(255), nullable=False)

    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=utc_now, nullable=False)
    updated_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), default=utc_now, onupdate=utc_now, nullable=False
    )

    lessons = relationship("LessonModule", back_populates="pack", cascade="all, delete-orphan")


class LessonModule(Base):
    __tablename__ = "lesson_modules"

    id: Mapped[str] = mapped_column(String(36), primary_key=True, default=new_uuid)
    pack_id: Mapped[str] = mapped_column(
        String(36), ForeignKey("content_packs.id", ondelete="CASCADE"), index=True, nullable=False
    )
    lesson_key: Mapped[str] = mapped_column(String(64), unique=True, index=True, nullable=False)
    subject: Mapped[str] = mapped_column(String(32), index=True, nullable=False)
    grade: Mapped[str] = mapped_column(String(48), nullable=False)
    sort_order: Mapped[int] = mapped_column(Integer, default=0, nullable=False)

    title_en: Mapped[str] = mapped_column(String(200), nullable=False)
    title_kn: Mapped[str] = mapped_column(String(200), nullable=False)
    chapter_en: Mapped[str] = mapped_column(String(120), nullable=False)
    chapter_kn: Mapped[str] = mapped_column(String(120), nullable=False)
    description_en: Mapped[str] = mapped_column(Text, nullable=False)
    description_kn: Mapped[str] = mapped_column(Text, nullable=False)
    diagram_type: Mapped[str] = mapped_column(String(32), nullable=False)
    audio_text_en: Mapped[str] = mapped_column(Text, nullable=False)
    audio_text_kn: Mapped[str] = mapped_column(Text, nullable=False)
    sections_json: Mapped[str] = mapped_column(Text, nullable=False)
    quiz_json: Mapped[str] = mapped_column(Text, nullable=False)

    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=utc_now, nullable=False)
    updated_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), default=utc_now, onupdate=utc_now, nullable=False
    )

    pack = relationship("ContentPack", back_populates="lessons")
