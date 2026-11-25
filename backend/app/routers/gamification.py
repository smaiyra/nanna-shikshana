from datetime import datetime, timezone
import math

from fastapi import APIRouter, Depends, HTTPException, Query, status
from sqlalchemy import select
from sqlalchemy.orm import Session

from app.database import get_db
from app.models.gamification import Badge, DailyQuest, LeaderboardPeer, QuestProgress, StudentBadge
from app.models.progress import LessonProgress
from app.models.student import Student
from app.schemas.common import PaginatedResponse, PaginationMeta
from app.schemas.gamification import (
    BadgeResponse,
    ClaimQuestResponse,
    GamificationProfileResponse,
    LeaderboardEntryResponse,
    QuestStatusResponse,
)
from app.security import require_role
from app.services.gamification_service import (
    award_xp,
    calculate_level_info,
    get_or_create_daily_quests,
    today_iso,
)

router = APIRouter(prefix="/gamification", tags=["gamification"])


@router.get("/profile", response_model=GamificationProfileResponse)
def get_gamification_profile(
    student: Student = Depends(require_role("student")),
    db: Session = Depends(get_db),
):
    badges = [
        sb.badge_key
        for sb in db.scalars(select(StudentBadge).where(StudentBadge.student_id == student.id))
    ]
    progress_rows = list(
        db.scalars(select(LessonProgress).where(LessonProgress.student_id == student.id))
    )
    quests = get_or_create_daily_quests(db, student, today_iso())
    db.commit()

    return GamificationProfileResponse(
        student_id=student.id,
        student_code=student.student_code,
        name=student.name,
        village=student.village,
        school=student.school,
        total_xp=student.total_xp,
        current_level=student.current_level,
        level_info=calculate_level_info(student.total_xp),
        streak_count=student.streak_count,
        streak_freezes_left=student.streak_freezes_left,
        last_active_date=student.last_active_date,
        unlocked_badges=badges,
        completed_lessons=[p.lesson_key for p in progress_rows if p.completed],
        completed_quizzes={p.lesson_key: p.best_quiz_score for p in progress_rows if p.completed},
        quests=quests,
    )


@router.get("/quests", response_model=list[QuestStatusResponse])
def get_daily_quests(
    student: Student = Depends(require_role("student")),
    db: Session = Depends(get_db),
):
    quests = get_or_create_daily_quests(db, student, today_iso())
    db.commit()
    return quests


@router.post("/quests/{quest_key}/claim", response_model=ClaimQuestResponse)
def claim_daily_quest(
    quest_key: str,
    student: Student = Depends(require_role("student")),
    db: Session = Depends(get_db),
):
    quest = db.scalar(select(DailyQuest).where(DailyQuest.quest_key == quest_key))
    if quest is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Quest not found.")

    qdate = today_iso()
    get_or_create_daily_quests(db, student, qdate)
    qp = db.scalar(
        select(QuestProgress).where(
            QuestProgress.student_id == student.id,
            QuestProgress.quest_key == quest_key,
            QuestProgress.quest_date == qdate,
        )
    )
    if qp is None or qp.progress < qp.max_progress:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Quest requirements are not completed yet.",
        )

    if qp.claimed:
        return ClaimQuestResponse(
            quest_key=quest_key,
            xp_awarded=0,
            total_xp=student.total_xp,
            current_level=student.current_level,
            already_claimed=True,
        )

    qp.claimed = True
    qp.claimed_at = datetime.now(timezone.utc)
    award_xp(student, quest.xp_reward)
    db.commit()
    db.refresh(student)

    return ClaimQuestResponse(
        quest_key=quest_key,
        xp_awarded=quest.xp_reward,
        total_xp=student.total_xp,
        current_level=student.current_level,
        already_claimed=False,
    )


@router.get("/badges", response_model=list[BadgeResponse])
def list_badges(
    student: Student = Depends(require_role("student")),
    db: Session = Depends(get_db),
):
    all_badges = list(db.scalars(select(Badge).order_by(Badge.sort_order.asc())))
    unlocked_map = {
        sb.badge_key: sb.unlocked_at
        for sb in db.scalars(select(StudentBadge).where(StudentBadge.student_id == student.id))
    }
    return [
        BadgeResponse(
            badge_key=b.badge_key,
            name_en=b.name_en,
            name_kn=b.name_kn,
            description_en=b.description_en,
            description_kn=b.description_kn,
            icon=b.icon,
            rarity=b.rarity,
            unlocked=b.badge_key in unlocked_map,
            unlocked_at=unlocked_map.get(b.badge_key),
        )
        for b in all_badges
    ]


@router.get("/leaderboard", response_model=PaginatedResponse[LeaderboardEntryResponse])
def get_classroom_leaderboard(
    page: int = Query(default=1, ge=1),
    page_size: int = Query(default=20, ge=1, le=100),
    student: Student = Depends(require_role("student")),
    db: Session = Depends(get_db),
):
    peers = list(db.scalars(select(LeaderboardPeer)))
    combined = [
        {
            "id": p.peer_key,
            "name": p.name,
            "village": p.village,
            "school": p.school,
            "total_xp": p.total_xp,
            "streak": p.streak,
            "is_current_user": False,
        }
        for p in peers
    ]
    combined.append(
        {
            "id": student.id,
            "name": f"{student.name} (You)",
            "village": student.village,
            "school": student.school,
            "total_xp": student.total_xp,
            "streak": student.streak_count,
            "is_current_user": True,
        }
    )
    combined.sort(key=lambda item: (-item["total_xp"], -item["streak"], item["name"]))

    ranked = [
        LeaderboardEntryResponse(rank=idx + 1, **row)
        for idx, row in enumerate(combined)
    ]

    total_items = len(ranked)
    total_pages = max(1, math.ceil(total_items / page_size))
    offset = (page - 1) * page_size
    return PaginatedResponse(
        items=ranked[offset : offset + page_size],
        pagination=PaginationMeta(
            page=page,
            page_size=page_size,
            total_items=total_items,
            total_pages=total_pages,
        ),
    )
