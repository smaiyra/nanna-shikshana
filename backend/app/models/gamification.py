from datetime import datetime
from sqlalchemy import Boolean, DateTime, ForeignKey, Integer, String, Text, UniqueConstraint
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.database import Base
from app.models.student import new_uuid, utc_now


class Badge(Base):
    __tablename__ = "badges"

    id: Mapped[str] = mapped_column(String(36), primary_key=True, default=new_uuid)
    badge_key: Mapped[str] = mapped_column(String(64), unique=True, index=True, nullable=False)
    name_en: Mapped[str] = mapped_column(String(120), nullable=False)
    name_kn: Mapped[str] = mapped_column(String(120), nullable=False)
    description_en: Mapped[str] = mapped_column(Text, nullable=False)
    description_kn: Mapped[str] = mapped_column(Text, nullable=False)
    icon: Mapped[str] = mapped_column(String(32), nullable=False)
    rarity: Mapped[str] = mapped_column(String(24), nullable=False)
    sort_order: Mapped[int] = mapped_column(Integer, default=0, nullable=False)

    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=utc_now, nullable=False)
    updated_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), default=utc_now, onupdate=utc_now, nullable=False
    )


class StudentBadge(Base):
    __tablename__ = "student_badges"
    __table_args__ = (UniqueConstraint("student_id", "badge_key", name="uq_student_badge"),)

    id: Mapped[str] = mapped_column(String(36), primary_key=True, default=new_uuid)
    student_id: Mapped[str] = mapped_column(
        String(36), ForeignKey("students.id", ondelete="CASCADE"), index=True, nullable=False
    )
    badge_key: Mapped[str] = mapped_column(
        String(64), ForeignKey("badges.badge_key", ondelete="CASCADE"), index=True, nullable=False
    )
    unlocked_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=utc_now, nullable=False)

    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=utc_now, nullable=False)
    updated_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), default=utc_now, onupdate=utc_now, nullable=False
    )

    student = relationship("Student", back_populates="badges")


class DailyQuest(Base):
    __tablename__ = "daily_quests"

    id: Mapped[str] = mapped_column(String(36), primary_key=True, default=new_uuid)
    quest_key: Mapped[str] = mapped_column(String(64), unique=True, index=True, nullable=False)
    title_en: Mapped[str] = mapped_column(String(180), nullable=False)
    title_kn: Mapped[str] = mapped_column(String(180), nullable=False)
    sub_en: Mapped[str] = mapped_column(String(240), nullable=False)
    sub_kn: Mapped[str] = mapped_column(String(240), nullable=False)
    xp_reward: Mapped[int] = mapped_column(Integer, nullable=False)
    target_type: Mapped[str] = mapped_column(String(48), nullable=False)
    max_progress: Mapped[int] = mapped_column(Integer, default=1, nullable=False)
    sort_order: Mapped[int] = mapped_column(Integer, default=0, nullable=False)

    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=utc_now, nullable=False)
    updated_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), default=utc_now, onupdate=utc_now, nullable=False
    )


class QuestProgress(Base):
    __tablename__ = "quest_progress"
    __table_args__ = (UniqueConstraint("student_id", "quest_key", "quest_date", name="uq_student_quest_day"),)

    id: Mapped[str] = mapped_column(String(36), primary_key=True, default=new_uuid)
    student_id: Mapped[str] = mapped_column(
        String(36), ForeignKey("students.id", ondelete="CASCADE"), index=True, nullable=False
    )
    quest_key: Mapped[str] = mapped_column(
        String(64), ForeignKey("daily_quests.quest_key", ondelete="CASCADE"), index=True, nullable=False
    )
    quest_date: Mapped[str] = mapped_column(String(10), index=True, nullable=False)
    progress: Mapped[int] = mapped_column(Integer, default=0, nullable=False)
    max_progress: Mapped[int] = mapped_column(Integer, default=1, nullable=False)
    claimed: Mapped[bool] = mapped_column(Boolean, default=False, nullable=False)
    claimed_at: Mapped[datetime | None] = mapped_column(DateTime(timezone=True), nullable=True)

    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=utc_now, nullable=False)
    updated_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), default=utc_now, onupdate=utc_now, nullable=False
    )

    student = relationship("Student", back_populates="quest_progress")


class LeaderboardPeer(Base):
    __tablename__ = "leaderboard_peers"

    id: Mapped[str] = mapped_column(String(36), primary_key=True, default=new_uuid)
    peer_key: Mapped[str] = mapped_column(String(64), unique=True, index=True, nullable=False)
    name: Mapped[str] = mapped_column(String(120), nullable=False)
    village: Mapped[str] = mapped_column(String(120), nullable=False)
    school: Mapped[str] = mapped_column(String(180), nullable=False)
    total_xp: Mapped[int] = mapped_column(Integer, nullable=False)
    streak: Mapped[int] = mapped_column(Integer, nullable=False)

    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=utc_now, nullable=False)
    updated_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), default=utc_now, onupdate=utc_now, nullable=False
    )
