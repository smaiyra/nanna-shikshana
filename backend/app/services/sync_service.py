from datetime import datetime, timezone
import json

from sqlalchemy import func, select
from sqlalchemy.orm import Session

from app.models.content import LessonModule
from app.models.gamification import DailyQuest, QuestProgress, StudentBadge
from app.models.progress import LessonProgress, P2PTransferLog, QuizAttempt
from app.models.student import Student
from app.models.sync import SyncEvent
from app.schemas.sync import SyncEventIn, SyncEventResult, SyncPullItem
from app.services.gamification_service import (
    advance_quest_progress,
    award_xp,
    count_student_p2p_transfers,
    evaluate_streak,
    get_or_create_daily_quests,
    today_iso,
    unlock_badge,
)


def _next_server_seq(db: Session) -> int:
    current_max = db.scalar(select(func.max(SyncEvent.server_seq)))
    return (current_max or 0) + 1


def _ensure_utc(dt: datetime) -> datetime:
    if dt.tzinfo is None:
        return dt.replace(tzinfo=timezone.utc)
    return dt.astimezone(timezone.utc)


def process_sync_batch(
    db: Session,
    student: Student,
    events: list[SyncEventIn],
) -> tuple[list[SyncEventResult], int]:
    results: list[SyncEventResult] = []
    seq_cursor = _next_server_seq(db) - 1

    for ev in events:
        existing = db.scalar(
            select(SyncEvent).where(SyncEvent.idempotency_key == ev.idempotency_key)
        )
        if existing is not None:
            results.append(
                SyncEventResult(
                    event_id=ev.event_id,
                    idempotency_key=ev.idempotency_key,
                    status="duplicate_ignored",
                    server_seq=existing.server_seq,
                    resolution_note="Idempotency key already processed; skipped safely.",
                )
            )
            seq_cursor = max(seq_cursor, existing.server_seq)
            continue

        resolution_status = "applied"
        resolution_note: str | None = None
        client_ts = _ensure_utc(ev.client_timestamp)

        if ev.event_type == "quiz_completed":
            lesson_key = str(ev.payload.get("lesson_key", ""))
            lesson = db.scalar(select(LessonModule).where(LessonModule.lesson_key == lesson_key))
            if lesson is None:
                results.append(
                    SyncEventResult(
                        event_id=ev.event_id,
                        idempotency_key=ev.idempotency_key,
                        status="rejected",
                        resolution_note=f"Unknown lesson_key '{lesson_key}'.",
                    )
                )
                continue

            score = max(0, int(ev.payload.get("score", 0)))
            total_questions = max(1, int(ev.payload.get("total_questions", 3)))
            score = min(score, total_questions)
            xp_awarded = score * 20
            answers = ev.payload.get("selected_indices", [])

            db.add(
                QuizAttempt(
                    student_id=student.id,
                    lesson_key=lesson_key,
                    score=score,
                    total_questions=total_questions,
                    xp_awarded=xp_awarded,
                    answers_json=json.dumps(answers),
                    client_event_id=ev.event_id,
                    attempted_at=client_ts,
                )
            )
            award_xp(student, xp_awarded)

            lp = db.scalar(
                select(LessonProgress).where(
                    LessonProgress.student_id == student.id,
                    LessonProgress.lesson_key == lesson_key,
                )
            )
            if lp is None:
                lp = LessonProgress(
                    student_id=student.id,
                    lesson_key=lesson_key,
                    completed=True,
                    best_quiz_score=score,
                    total_questions=total_questions,
                    last_studied_at=client_ts,
                )
                db.add(lp)
            else:
                if score < lp.best_quiz_score:
                    resolution_status = "conflict_resolved"
                    resolution_note = "Preserved higher server best_quiz_score via monotonic MAX rule."
                lp.best_quiz_score = max(lp.best_quiz_score, score)
                lp.completed = True
                if client_ts > _ensure_utc(lp.last_studied_at):
                    lp.last_studied_at = client_ts

            unlock_badge(db, student, "First Lesson")
            if score == total_questions and lesson_key == "math_fractions_ch3":
                unlock_badge(db, student, "Math Master")
            if lesson.subject == "science":
                advance_quest_progress(db, student, "complete_science", client_ts.date().isoformat())
            if score == total_questions:
                advance_quest_progress(db, student, "quiz_100", client_ts.date().isoformat())

        elif ev.event_type == "quest_claimed":
            quest_key = str(ev.payload.get("quest_key", ""))
            quest_date = str(ev.payload.get("quest_date") or client_ts.date().isoformat())
            quest = db.scalar(select(DailyQuest).where(DailyQuest.quest_key == quest_key))
            if quest is None:
                results.append(
                    SyncEventResult(
                        event_id=ev.event_id,
                        idempotency_key=ev.idempotency_key,
                        status="rejected",
                        resolution_note=f"Unknown quest_key '{quest_key}'.",
                    )
                )
                continue

            get_or_create_daily_quests(db, student, quest_date)
            qp = db.scalar(
                select(QuestProgress).where(
                    QuestProgress.student_id == student.id,
                    QuestProgress.quest_key == quest_key,
                    QuestProgress.quest_date == quest_date,
                )
            )
            if qp is not None and qp.claimed:
                resolution_status = "conflict_resolved"
                resolution_note = "Quest already claimed for date; prevented duplicate XP award."
            elif qp is not None:
                qp.progress = max(qp.progress, qp.max_progress)
                qp.claimed = True
                qp.claimed_at = client_ts
                award_xp(student, quest.xp_reward)

        elif ev.event_type == "p2p_transfer_completed":
            lesson_key = str(ev.payload.get("lesson_key", ""))
            lesson = db.scalar(select(LessonModule).where(LessonModule.lesson_key == lesson_key))
            if lesson is None:
                results.append(
                    SyncEventResult(
                        event_id=ev.event_id,
                        idempotency_key=ev.idempotency_key,
                        status="rejected",
                        resolution_note=f"Unknown lesson_key '{lesson_key}'.",
                    )
                )
                continue

            db.add(
                P2PTransferLog(
                    student_id=student.id,
                    lesson_key=lesson_key,
                    package_name=str(ev.payload.get("package_name", "")),
                    sha256=str(ev.payload.get("sha256", "")),
                    peer_device_name=str(ev.payload.get("peer_device_name", "Peer Device")),
                    xp_awarded=50,
                    client_event_id=ev.event_id,
                    transferred_at=client_ts,
                )
            )
            db.flush()
            award_xp(student, 50)
            unlock_badge(db, student, "P2P Sharer")
            if count_student_p2p_transfers(db, student.id) >= 3:
                unlock_badge(db, student, "P2P Pioneer")
            advance_quest_progress(db, student, "p2p_share", client_ts.date().isoformat())

        elif ev.event_type == "streak_updated":
            target_date = str(ev.payload.get("active_date") or client_ts.date().isoformat())
            evaluate_streak(db, student, target_date)

        elif ev.event_type == "lesson_viewed":
            lesson_key = str(ev.payload.get("lesson_key", ""))
            lesson = db.scalar(select(LessonModule).where(LessonModule.lesson_key == lesson_key))
            if lesson is not None:
                lp = db.scalar(
                    select(LessonProgress).where(
                        LessonProgress.student_id == student.id,
                        LessonProgress.lesson_key == lesson_key,
                    )
                )
                if lp is None:
                    db.add(
                        LessonProgress(
                            student_id=student.id,
                            lesson_key=lesson_key,
                            completed=False,
                            best_quiz_score=0,
                            total_questions=3,
                            last_studied_at=client_ts,
                        )
                    )
                elif client_ts > _ensure_utc(lp.last_studied_at):
                    lp.last_studied_at = client_ts

        elif ev.event_type == "profile_updated":
            if client_ts >= _ensure_utc(student.updated_at):
                if "name" in ev.payload and ev.payload["name"]:
                    student.name = str(ev.payload["name"])[:120]
                if "village" in ev.payload and ev.payload["village"]:
                    student.village = str(ev.payload["village"])[:120]
                if "school" in ev.payload and ev.payload["school"]:
                    student.school = str(ev.payload["school"])[:180]
                if ev.payload.get("preferred_lang") in ("en", "kn"):
                    student.preferred_lang = ev.payload["preferred_lang"]
            else:
                resolution_status = "conflict_resolved"
                resolution_note = "Server profile timestamp is newer; kept server profile fields (LWW)."

        seq_cursor += 1
        sync_row = SyncEvent(
            student_id=student.id,
            event_id=ev.event_id,
            idempotency_key=ev.idempotency_key,
            event_type=ev.event_type,
            payload_json=json.dumps(ev.payload, ensure_ascii=False),
            client_timestamp=client_ts,
            server_seq=seq_cursor,
            resolution_status=resolution_status,
        )
        db.add(sync_row)
        db.flush()

        results.append(
            SyncEventResult(
                event_id=ev.event_id,
                idempotency_key=ev.idempotency_key,
                status=resolution_status,  # type: ignore[arg-type]
                server_seq=seq_cursor,
                resolution_note=resolution_note,
            )
        )

    db.commit()
    db.refresh(student)
    return results, seq_cursor


def pull_sync_changes(
    db: Session,
    student: Student,
    since_cursor: int,
    limit: int = 50,
) -> tuple[list[SyncPullItem], int, bool, dict]:
    rows = list(
        db.scalars(
            select(SyncEvent)
            .where(SyncEvent.student_id == student.id, SyncEvent.server_seq > since_cursor)
            .order_by(SyncEvent.server_seq.asc())
            .limit(limit + 1)
        )
    )
    has_more = len(rows) > limit
    page_rows = rows[:limit]
    next_cursor = page_rows[-1].server_seq if page_rows else since_cursor

    items = [
        SyncPullItem(
            server_seq=row.server_seq,
            event_id=row.event_id,
            event_type=row.event_type,
            payload=json.loads(row.payload_json),
            resolution_status=row.resolution_status,
            created_at=row.created_at,
        )
        for row in page_rows
    ]

    badges = [
        sb.badge_key
        for sb in db.scalars(select(StudentBadge).where(StudentBadge.student_id == student.id))
    ]
    progress_rows = list(
        db.scalars(select(LessonProgress).where(LessonProgress.student_id == student.id))
    )
    quests = get_or_create_daily_quests(db, student, today_iso())

    server_state = {
        "total_xp": student.total_xp,
        "current_level": student.current_level,
        "streak_count": student.streak_count,
        "streak_freezes_left": student.streak_freezes_left,
        "last_active_date": student.last_active_date,
        "preferred_lang": student.preferred_lang,
        "unlocked_badges": badges,
        "completed_lessons": [p.lesson_key for p in progress_rows if p.completed],
        "completed_quizzes": {p.lesson_key: p.best_quiz_score for p in progress_rows if p.completed},
        "quests": [q.model_dump() for q in quests],
    }
    return items, next_cursor, has_more, server_state
