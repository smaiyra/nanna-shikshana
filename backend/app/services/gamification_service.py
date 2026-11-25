from datetime import date, datetime, timedelta, timezone

from sqlalchemy import select
from sqlalchemy.orm import Session

from app.models.gamification import Badge, DailyQuest, QuestProgress, StudentBadge
from app.models.progress import LessonProgress, P2PTransferLog
from app.models.student import Student
from app.schemas.gamification import LevelInfoResponse, QuestStatusResponse


def today_iso() -> str:
    return datetime.now(timezone.utc).date().isoformat()


def calculate_level_info(total_xp: int) -> LevelInfoResponse:
    if total_xp <= 200:
        pct = min(100.0, max(0.0, (total_xp / 200.0) * 100.0))
        return LevelInfoResponse(
            level_number=1,
            title_en="Level 1: Village Scholar",
            title_kn="ಹಂತ 1: ಗ್ರಾಮ ವಿದ್ವಾಂಸ",
            current_xp=total_xp,
            next_level_xp=201,
            needed_xp=201 - total_xp,
            progress_pct=round(pct, 2),
            color_hex="#0EA5E9",
        )
    if total_xp <= 600:
        pct = min(100.0, max(0.0, ((total_xp - 201) / 399.0) * 100.0))
        return LevelInfoResponse(
            level_number=2,
            title_en="Level 2: District Champion",
            title_kn="ಹಂತ 2: ಜಿಲ್ಲಾ ಚಾಂಪಿಯನ್",
            current_xp=total_xp,
            next_level_xp=601,
            needed_xp=601 - total_xp,
            progress_pct=round(pct, 2),
            color_hex="#8B5CF6",
        )
    pct = min(100.0, 50.0 + ((total_xp - 601) % 500) / 10.0)
    return LevelInfoResponse(
        level_number=3,
        title_en="Level 3: State Leader",
        title_kn="ಹಂತ 3: ರಾಜ್ಯ ನಾಯಕ",
        current_xp=total_xp,
        next_level_xp=1200,
        needed_xp=max(0, 1200 - total_xp),
        progress_pct=round(min(100.0, pct), 2),
        color_hex="#10B981",
    )


def award_xp(student: Student, xp_delta: int) -> None:
    if xp_delta <= 0:
        return
    student.total_xp += xp_delta
    student.current_level = calculate_level_info(student.total_xp).title_en


def unlock_badge(db: Session, student: Student, badge_key: str) -> bool:
    badge = db.scalar(select(Badge).where(Badge.badge_key == badge_key))
    if badge is None:
        return False
    existing = db.scalar(
        select(StudentBadge).where(
            StudentBadge.student_id == student.id,
            StudentBadge.badge_key == badge_key,
        )
    )
    if existing is not None:
        return False
    db.add(StudentBadge(student_id=student.id, badge_key=badge_key))
    db.flush()
    return True


def evaluate_streak(db: Session, student: Student, target_date_str: str | None = None) -> dict:
    current_date_str = target_date_str or today_iso()
    current_date = date.fromisoformat(current_date_str)
    last_date = date.fromisoformat(student.last_active_date)
    diff_days = (current_date - last_date).days

    unlocked: list[str] = []
    if diff_days <= 0:
        return {"status": "maintained", "streak_count": student.streak_count, "unlocked_badges": unlocked}

    if diff_days == 1:
        student.streak_count += 1
        student.last_active_date = current_date_str
        if student.streak_count >= 3 and unlock_badge(db, student, "3-Day Streak"):
            unlocked.append("3-Day Streak")
        if student.streak_count >= 7 and unlock_badge(db, student, "7-Day Flame"):
            unlocked.append("7-Day Flame")
        return {"status": "incremented", "streak_count": student.streak_count, "unlocked_badges": unlocked}

    if diff_days == 2:
        if student.streak_freezes_left > 0:
            student.streak_freezes_left -= 1
            student.last_active_date = current_date_str
            return {
                "status": "frozen",
                "streak_count": student.streak_count,
                "streak_freezes_left": student.streak_freezes_left,
                "unlocked_badges": unlocked,
            }
        student.streak_count = 1
        student.last_active_date = current_date_str
        return {"status": "reset", "streak_count": student.streak_count, "unlocked_badges": unlocked}

    student.streak_count = 1
    student.last_active_date = current_date_str
    return {"status": "reset", "streak_count": student.streak_count, "unlocked_badges": unlocked}


def get_or_create_daily_quests(db: Session, student: Student, quest_date: str | None = None) -> list[QuestStatusResponse]:
    qdate = quest_date or today_iso()
    quests = list(db.scalars(select(DailyQuest).order_by(DailyQuest.sort_order)))
    existing = {
        qp.quest_key: qp
        for qp in db.scalars(
            select(QuestProgress).where(
                QuestProgress.student_id == student.id,
                QuestProgress.quest_date == qdate,
            )
        )
    }

    results: list[QuestStatusResponse] = []
    created_any = False
    for q in quests:
        qp = existing.get(q.quest_key)
        if qp is None:
            qp = QuestProgress(
                student_id=student.id,
                quest_key=q.quest_key,
                quest_date=qdate,
                progress=0,
                max_progress=q.max_progress,
                claimed=False,
            )
            db.add(qp)
            created_any = True
        results.append(
            QuestStatusResponse(
                quest_key=q.quest_key,
                title_en=q.title_en,
                title_kn=q.title_kn,
                sub_en=q.sub_en,
                sub_kn=q.sub_kn,
                xp_reward=q.xp_reward,
                target_type=q.target_type,
                progress=qp.progress,
                max_progress=qp.max_progress,
                claimed=qp.claimed,
            )
        )
    if created_any:
        db.flush()
    return results


def advance_quest_progress(
    db: Session,
    student: Student,
    target_type: str,
    quest_date: str | None = None,
) -> None:
    qdate = quest_date or today_iso()
    get_or_create_daily_quests(db, student, qdate)
    quests = list(db.scalars(select(DailyQuest).where(DailyQuest.target_type == target_type)))
    for q in quests:
        qp = db.scalar(
            select(QuestProgress).where(
                QuestProgress.student_id == student.id,
                QuestProgress.quest_key == q.quest_key,
                QuestProgress.quest_date == qdate,
            )
        )
        if qp is not None and qp.progress < qp.max_progress:
            qp.progress = qp.max_progress


def count_student_p2p_transfers(db: Session, student_id: str) -> int:
    return len(list(db.scalars(select(P2PTransferLog.id).where(P2PTransferLog.student_id == student_id))))


def count_completed_lessons(db: Session, student_id: str) -> int:
    return len(
        list(
            db.scalars(
                select(LessonProgress.id).where(
                    LessonProgress.student_id == student_id,
                    LessonProgress.completed.is_(True),
                )
            )
        )
    )


def yesterday_iso() -> str:
    return (datetime.now(timezone.utc).date() - timedelta(days=1)).isoformat()
