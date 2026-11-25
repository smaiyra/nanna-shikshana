"""initial schema

Revision ID: 0001_initial
Revises:
Create Date: 2026-11-24 21:20:00.000000

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


revision: str = "0001_initial"
down_revision: Union[str, None] = None
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    op.create_table(
        "students",
        sa.Column("id", sa.String(length=36), nullable=False),
        sa.Column("student_code", sa.String(length=64), nullable=False),
        sa.Column("device_id", sa.String(length=128), nullable=False),
        sa.Column("secret_hash", sa.String(length=255), nullable=False),
        sa.Column("role", sa.String(length=24), nullable=False),
        sa.Column("name", sa.String(length=120), nullable=False),
        sa.Column("village", sa.String(length=120), nullable=False),
        sa.Column("school", sa.String(length=180), nullable=False),
        sa.Column("preferred_lang", sa.String(length=8), nullable=False),
        sa.Column("total_xp", sa.Integer(), nullable=False),
        sa.Column("current_level", sa.String(length=80), nullable=False),
        sa.Column("streak_count", sa.Integer(), nullable=False),
        sa.Column("streak_freezes_left", sa.Integer(), nullable=False),
        sa.Column("last_active_date", sa.String(length=10), nullable=False),
        sa.Column("created_at", sa.DateTime(timezone=True), nullable=False),
        sa.Column("updated_at", sa.DateTime(timezone=True), nullable=False),
        sa.PrimaryKeyConstraint("id"),
    )
    op.create_index("ix_students_device_id", "students", ["device_id"], unique=False)
    op.create_index("ix_students_student_code", "students", ["student_code"], unique=True)

    op.create_table(
        "revoked_tokens",
        sa.Column("id", sa.String(length=36), nullable=False),
        sa.Column("jti", sa.String(length=64), nullable=False),
        sa.Column("student_id", sa.String(length=36), nullable=False),
        sa.Column("expires_at", sa.DateTime(timezone=True), nullable=False),
        sa.Column("created_at", sa.DateTime(timezone=True), nullable=False),
        sa.Column("updated_at", sa.DateTime(timezone=True), nullable=False),
        sa.ForeignKeyConstraint(["student_id"], ["students.id"], ondelete="CASCADE"),
        sa.PrimaryKeyConstraint("id"),
    )
    op.create_index("ix_revoked_tokens_jti", "revoked_tokens", ["jti"], unique=True)
    op.create_index("ix_revoked_tokens_student_id", "revoked_tokens", ["student_id"], unique=False)

    op.create_table(
        "content_packs",
        sa.Column("id", sa.String(length=36), nullable=False),
        sa.Column("pack_code", sa.String(length=64), nullable=False),
        sa.Column("subject", sa.String(length=32), nullable=False),
        sa.Column("grade", sa.String(length=48), nullable=False),
        sa.Column("version", sa.Integer(), nullable=False),
        sa.Column("package_name", sa.String(length=120), nullable=False),
        sa.Column("file_size_mb", sa.Float(), nullable=False),
        sa.Column("sha256", sa.String(length=64), nullable=False),
        sa.Column("archive_path", sa.String(length=255), nullable=False),
        sa.Column("created_at", sa.DateTime(timezone=True), nullable=False),
        sa.Column("updated_at", sa.DateTime(timezone=True), nullable=False),
        sa.PrimaryKeyConstraint("id"),
        sa.UniqueConstraint("package_name"),
    )
    op.create_index("ix_content_packs_pack_code", "content_packs", ["pack_code"], unique=True)
    op.create_index("ix_content_packs_subject", "content_packs", ["subject"], unique=False)

    op.create_table(
        "lesson_modules",
        sa.Column("id", sa.String(length=36), nullable=False),
        sa.Column("pack_id", sa.String(length=36), nullable=False),
        sa.Column("lesson_key", sa.String(length=64), nullable=False),
        sa.Column("subject", sa.String(length=32), nullable=False),
        sa.Column("grade", sa.String(length=48), nullable=False),
        sa.Column("sort_order", sa.Integer(), nullable=False),
        sa.Column("title_en", sa.String(length=200), nullable=False),
        sa.Column("title_kn", sa.String(length=200), nullable=False),
        sa.Column("chapter_en", sa.String(length=120), nullable=False),
        sa.Column("chapter_kn", sa.String(length=120), nullable=False),
        sa.Column("description_en", sa.Text(), nullable=False),
        sa.Column("description_kn", sa.Text(), nullable=False),
        sa.Column("diagram_type", sa.String(length=32), nullable=False),
        sa.Column("audio_text_en", sa.Text(), nullable=False),
        sa.Column("audio_text_kn", sa.Text(), nullable=False),
        sa.Column("sections_json", sa.Text(), nullable=False),
        sa.Column("quiz_json", sa.Text(), nullable=False),
        sa.Column("created_at", sa.DateTime(timezone=True), nullable=False),
        sa.Column("updated_at", sa.DateTime(timezone=True), nullable=False),
        sa.ForeignKeyConstraint(["pack_id"], ["content_packs.id"], ondelete="CASCADE"),
        sa.PrimaryKeyConstraint("id"),
    )
    op.create_index("ix_lesson_modules_lesson_key", "lesson_modules", ["lesson_key"], unique=True)
    op.create_index("ix_lesson_modules_pack_id", "lesson_modules", ["pack_id"], unique=False)
    op.create_index("ix_lesson_modules_subject", "lesson_modules", ["subject"], unique=False)

    op.create_table(
        "badges",
        sa.Column("id", sa.String(length=36), nullable=False),
        sa.Column("badge_key", sa.String(length=64), nullable=False),
        sa.Column("name_en", sa.String(length=120), nullable=False),
        sa.Column("name_kn", sa.String(length=120), nullable=False),
        sa.Column("description_en", sa.Text(), nullable=False),
        sa.Column("description_kn", sa.Text(), nullable=False),
        sa.Column("icon", sa.String(length=32), nullable=False),
        sa.Column("rarity", sa.String(length=24), nullable=False),
        sa.Column("sort_order", sa.Integer(), nullable=False),
        sa.Column("created_at", sa.DateTime(timezone=True), nullable=False),
        sa.Column("updated_at", sa.DateTime(timezone=True), nullable=False),
        sa.PrimaryKeyConstraint("id"),
    )
    op.create_index("ix_badges_badge_key", "badges", ["badge_key"], unique=True)

    op.create_table(
        "daily_quests",
        sa.Column("id", sa.String(length=36), nullable=False),
        sa.Column("quest_key", sa.String(length=64), nullable=False),
        sa.Column("title_en", sa.String(length=180), nullable=False),
        sa.Column("title_kn", sa.String(length=180), nullable=False),
        sa.Column("sub_en", sa.String(length=240), nullable=False),
        sa.Column("sub_kn", sa.String(length=240), nullable=False),
        sa.Column("xp_reward", sa.Integer(), nullable=False),
        sa.Column("target_type", sa.String(length=48), nullable=False),
        sa.Column("max_progress", sa.Integer(), nullable=False),
        sa.Column("sort_order", sa.Integer(), nullable=False),
        sa.Column("created_at", sa.DateTime(timezone=True), nullable=False),
        sa.Column("updated_at", sa.DateTime(timezone=True), nullable=False),
        sa.PrimaryKeyConstraint("id"),
    )
    op.create_index("ix_daily_quests_quest_key", "daily_quests", ["quest_key"], unique=True)

    op.create_table(
        "leaderboard_peers",
        sa.Column("id", sa.String(length=36), nullable=False),
        sa.Column("peer_key", sa.String(length=64), nullable=False),
        sa.Column("name", sa.String(length=120), nullable=False),
        sa.Column("village", sa.String(length=120), nullable=False),
        sa.Column("school", sa.String(length=180), nullable=False),
        sa.Column("total_xp", sa.Integer(), nullable=False),
        sa.Column("streak", sa.Integer(), nullable=False),
        sa.Column("created_at", sa.DateTime(timezone=True), nullable=False),
        sa.Column("updated_at", sa.DateTime(timezone=True), nullable=False),
        sa.PrimaryKeyConstraint("id"),
    )
    op.create_index("ix_leaderboard_peers_peer_key", "leaderboard_peers", ["peer_key"], unique=True)

    op.create_table(
        "lesson_progress",
        sa.Column("id", sa.String(length=36), nullable=False),
        sa.Column("student_id", sa.String(length=36), nullable=False),
        sa.Column("lesson_key", sa.String(length=64), nullable=False),
        sa.Column("completed", sa.Boolean(), nullable=False),
        sa.Column("best_quiz_score", sa.Integer(), nullable=False),
        sa.Column("total_questions", sa.Integer(), nullable=False),
        sa.Column("last_studied_at", sa.DateTime(timezone=True), nullable=False),
        sa.Column("created_at", sa.DateTime(timezone=True), nullable=False),
        sa.Column("updated_at", sa.DateTime(timezone=True), nullable=False),
        sa.ForeignKeyConstraint(["lesson_key"], ["lesson_modules.lesson_key"], ondelete="CASCADE"),
        sa.ForeignKeyConstraint(["student_id"], ["students.id"], ondelete="CASCADE"),
        sa.PrimaryKeyConstraint("id"),
        sa.UniqueConstraint("student_id", "lesson_key", name="uq_student_lesson"),
    )
    op.create_index("ix_lesson_progress_lesson_key", "lesson_progress", ["lesson_key"], unique=False)
    op.create_index("ix_lesson_progress_student_id", "lesson_progress", ["student_id"], unique=False)
    op.create_index("ix_lesson_progress_student_lesson", "lesson_progress", ["student_id", "lesson_key"], unique=False)

    op.create_table(
        "quiz_attempts",
        sa.Column("id", sa.String(length=36), nullable=False),
        sa.Column("student_id", sa.String(length=36), nullable=False),
        sa.Column("lesson_key", sa.String(length=64), nullable=False),
        sa.Column("score", sa.Integer(), nullable=False),
        sa.Column("total_questions", sa.Integer(), nullable=False),
        sa.Column("xp_awarded", sa.Integer(), nullable=False),
        sa.Column("answers_json", sa.Text(), nullable=False),
        sa.Column("client_event_id", sa.String(length=64), nullable=True),
        sa.Column("attempted_at", sa.DateTime(timezone=True), nullable=False),
        sa.Column("created_at", sa.DateTime(timezone=True), nullable=False),
        sa.Column("updated_at", sa.DateTime(timezone=True), nullable=False),
        sa.ForeignKeyConstraint(["lesson_key"], ["lesson_modules.lesson_key"], ondelete="CASCADE"),
        sa.ForeignKeyConstraint(["student_id"], ["students.id"], ondelete="CASCADE"),
        sa.PrimaryKeyConstraint("id"),
    )
    op.create_index("ix_quiz_attempts_client_event_id", "quiz_attempts", ["client_event_id"], unique=True)
    op.create_index("ix_quiz_attempts_lesson_key", "quiz_attempts", ["lesson_key"], unique=False)
    op.create_index("ix_quiz_attempts_student_id", "quiz_attempts", ["student_id"], unique=False)

    op.create_table(
        "p2p_transfer_logs",
        sa.Column("id", sa.String(length=36), nullable=False),
        sa.Column("student_id", sa.String(length=36), nullable=False),
        sa.Column("lesson_key", sa.String(length=64), nullable=False),
        sa.Column("package_name", sa.String(length=120), nullable=False),
        sa.Column("sha256", sa.String(length=64), nullable=False),
        sa.Column("peer_device_name", sa.String(length=120), nullable=False),
        sa.Column("xp_awarded", sa.Integer(), nullable=False),
        sa.Column("client_event_id", sa.String(length=64), nullable=True),
        sa.Column("transferred_at", sa.DateTime(timezone=True), nullable=False),
        sa.Column("created_at", sa.DateTime(timezone=True), nullable=False),
        sa.Column("updated_at", sa.DateTime(timezone=True), nullable=False),
        sa.ForeignKeyConstraint(["lesson_key"], ["lesson_modules.lesson_key"], ondelete="CASCADE"),
        sa.ForeignKeyConstraint(["student_id"], ["students.id"], ondelete="CASCADE"),
        sa.PrimaryKeyConstraint("id"),
    )
    op.create_index("ix_p2p_transfer_logs_client_event_id", "p2p_transfer_logs", ["client_event_id"], unique=True)
    op.create_index("ix_p2p_transfer_logs_lesson_key", "p2p_transfer_logs", ["lesson_key"], unique=False)
    op.create_index("ix_p2p_transfer_logs_student_id", "p2p_transfer_logs", ["student_id"], unique=False)

    op.create_table(
        "student_badges",
        sa.Column("id", sa.String(length=36), nullable=False),
        sa.Column("student_id", sa.String(length=36), nullable=False),
        sa.Column("badge_key", sa.String(length=64), nullable=False),
        sa.Column("unlocked_at", sa.DateTime(timezone=True), nullable=False),
        sa.Column("created_at", sa.DateTime(timezone=True), nullable=False),
        sa.Column("updated_at", sa.DateTime(timezone=True), nullable=False),
        sa.ForeignKeyConstraint(["badge_key"], ["badges.badge_key"], ondelete="CASCADE"),
        sa.ForeignKeyConstraint(["student_id"], ["students.id"], ondelete="CASCADE"),
        sa.PrimaryKeyConstraint("id"),
        sa.UniqueConstraint("student_id", "badge_key", name="uq_student_badge"),
    )
    op.create_index("ix_student_badges_badge_key", "student_badges", ["badge_key"], unique=False)
    op.create_index("ix_student_badges_student_id", "student_badges", ["student_id"], unique=False)

    op.create_table(
        "quest_progress",
        sa.Column("id", sa.String(length=36), nullable=False),
        sa.Column("student_id", sa.String(length=36), nullable=False),
        sa.Column("quest_key", sa.String(length=64), nullable=False),
        sa.Column("quest_date", sa.String(length=10), nullable=False),
        sa.Column("progress", sa.Integer(), nullable=False),
        sa.Column("max_progress", sa.Integer(), nullable=False),
        sa.Column("claimed", sa.Boolean(), nullable=False),
        sa.Column("claimed_at", sa.DateTime(timezone=True), nullable=True),
        sa.Column("created_at", sa.DateTime(timezone=True), nullable=False),
        sa.Column("updated_at", sa.DateTime(timezone=True), nullable=False),
        sa.ForeignKeyConstraint(["quest_key"], ["daily_quests.quest_key"], ondelete="CASCADE"),
        sa.ForeignKeyConstraint(["student_id"], ["students.id"], ondelete="CASCADE"),
        sa.PrimaryKeyConstraint("id"),
        sa.UniqueConstraint("student_id", "quest_key", "quest_date", name="uq_student_quest_day"),
    )
    op.create_index("ix_quest_progress_quest_date", "quest_progress", ["quest_date"], unique=False)
    op.create_index("ix_quest_progress_quest_key", "quest_progress", ["quest_key"], unique=False)
    op.create_index("ix_quest_progress_student_id", "quest_progress", ["student_id"], unique=False)

    op.create_table(
        "sync_events",
        sa.Column("id", sa.String(length=36), nullable=False),
        sa.Column("student_id", sa.String(length=36), nullable=False),
        sa.Column("event_id", sa.String(length=64), nullable=False),
        sa.Column("idempotency_key", sa.String(length=128), nullable=False),
        sa.Column("event_type", sa.String(length=48), nullable=False),
        sa.Column("payload_json", sa.Text(), nullable=False),
        sa.Column("client_timestamp", sa.DateTime(timezone=True), nullable=False),
        sa.Column("server_seq", sa.Integer(), nullable=False),
        sa.Column("resolution_status", sa.String(length=48), nullable=False),
        sa.Column("created_at", sa.DateTime(timezone=True), nullable=False),
        sa.Column("updated_at", sa.DateTime(timezone=True), nullable=False),
        sa.ForeignKeyConstraint(["student_id"], ["students.id"], ondelete="CASCADE"),
        sa.PrimaryKeyConstraint("id"),
    )
    op.create_index("ix_sync_events_event_id", "sync_events", ["event_id"], unique=False)
    op.create_index("ix_sync_events_event_type", "sync_events", ["event_type"], unique=False)
    op.create_index("ix_sync_events_idempotency_key", "sync_events", ["idempotency_key"], unique=True)
    op.create_index("ix_sync_events_server_seq", "sync_events", ["server_seq"], unique=True)
    op.create_index("ix_sync_events_student_id", "sync_events", ["student_id"], unique=False)
    op.create_index("ix_sync_events_student_seq", "sync_events", ["student_id", "server_seq"], unique=False)


def downgrade() -> None:
    op.drop_table("sync_events")
    op.drop_table("quest_progress")
    op.drop_table("student_badges")
    op.drop_table("p2p_transfer_logs")
    op.drop_table("quiz_attempts")
    op.drop_table("lesson_progress")
    op.drop_table("leaderboard_peers")
    op.drop_table("daily_quests")
    op.drop_table("badges")
    op.drop_table("lesson_modules")
    op.drop_table("content_packs")
    op.drop_table("revoked_tokens")
    op.drop_table("students")
