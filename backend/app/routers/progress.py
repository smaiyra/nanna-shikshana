from datetime import datetime, timezone
import json
import math

from fastapi import APIRouter, Depends, HTTPException, Query, status
from sqlalchemy import func, select
from sqlalchemy.orm import Session

from app.database import get_db
from app.models.content import LessonModule
from app.models.progress import LessonProgress, P2PTransferLog, QuizAttempt
from app.models.student import Student
from app.schemas.common import PaginatedResponse, PaginationMeta
from app.schemas.progress import (
    LessonProgressResponse,
    LessonViewRequest,
    P2PTransferRequest,
    P2PTransferResponse,
    QuizAttemptRequest,
    QuizAttemptResponse,
)
from app.security import require_role
from app.services.gamification_service import (
    advance_quest_progress,
    award_xp,
    count_student_p2p_transfers,
    today_iso,
    unlock_badge,
)

router = APIRouter(prefix="/progress", tags=["progress"])


@router.get("/lessons", response_model=PaginatedResponse[LessonProgressResponse])
def list_lesson_progress(
    page: int = Query(default=1, ge=1),
    page_size: int = Query(default=20, ge=1, le=100),
    student: Student = Depends(require_role("student")),
    db: Session = Depends(get_db),
):
    total_items = db.scalar(
        select(func.count(LessonProgress.id)).where(LessonProgress.student_id == student.id)
    ) or 0
    total_pages = max(1, math.ceil(total_items / page_size))
    offset = (page - 1) * page_size

    rows = list(
        db.scalars(
            select(LessonProgress)
            .where(LessonProgress.student_id == student.id)
            .order_by(LessonProgress.last_studied_at.desc())
            .offset(offset)
            .limit(page_size)
        )
    )
    return PaginatedResponse(
        items=[LessonProgressResponse.model_validate(r) for r in rows],
        pagination=PaginationMeta(
            page=page,
            page_size=page_size,
            total_items=total_items,
            total_pages=total_pages,
        ),
    )


@router.post("/lessons/{lesson_key}/view", response_model=LessonProgressResponse)
def record_lesson_view(
    lesson_key: str,
    req: LessonViewRequest,
    student: Student = Depends(require_role("student")),
    db: Session = Depends(get_db),
):
    lesson = db.scalar(select(LessonModule).where(LessonModule.lesson_key == lesson_key))
    if lesson is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Lesson module not found.")

    studied_at = req.studied_at or datetime.now(timezone.utc)
    lp = db.scalar(
        select(LessonProgress).where(
            LessonProgress.student_id == student.id,
            LessonProgress.lesson_key == lesson_key,
        )
    )
    if lp is None:
        quiz_list = json.loads(lesson.quiz_json)
        lp = LessonProgress(
            student_id=student.id,
            lesson_key=lesson_key,
            completed=False,
            best_quiz_score=0,
            total_questions=len(quiz_list),
            last_studied_at=studied_at,
        )
        db.add(lp)
    else:
        lp.last_studied_at = studied_at

    db.commit()
    db.refresh(lp)
    return LessonProgressResponse.model_validate(lp)


@router.post("/quizzes/attempt", response_model=QuizAttemptResponse)
def submit_quiz_attempt(
    req: QuizAttemptRequest,
    student: Student = Depends(require_role("student")),
    db: Session = Depends(get_db),
):
    lesson = db.scalar(select(LessonModule).where(LessonModule.lesson_key == req.lesson_key))
    if lesson is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Lesson module not found.")

    if req.client_event_id:
        existing_attempt = db.scalar(
            select(QuizAttempt).where(QuizAttempt.client_event_id == req.client_event_id)
        )
        if existing_attempt is not None:
            lp = db.scalar(
                select(LessonProgress).where(
                    LessonProgress.student_id == student.id,
                    LessonProgress.lesson_key == req.lesson_key,
                )
            )
            return QuizAttemptResponse(
                attempt_id=existing_attempt.id,
                lesson_key=existing_attempt.lesson_key,
                score=existing_attempt.score,
                total_questions=existing_attempt.total_questions,
                xp_awarded=0,
                total_xp=student.total_xp,
                current_level=student.current_level,
                newly_unlocked_badges=[],
                best_quiz_score=lp.best_quiz_score if lp else existing_attempt.score,
                attempted_at=existing_attempt.attempted_at,
            )

    quiz_questions = json.loads(lesson.quiz_json)
    total_questions = len(quiz_questions)
    if len(req.selected_indices) != total_questions:
        raise HTTPException(
            status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
            detail=f"Expected {total_questions} answers for lesson '{req.lesson_key}'.",
        )

    score = sum(
        1
        for idx, q in enumerate(quiz_questions)
        if req.selected_indices[idx] == q["correct_index"]
    )
    xp_awarded = score * 20
    attempted_at = req.attempted_at or datetime.now(timezone.utc)

    attempt = QuizAttempt(
        student_id=student.id,
        lesson_key=req.lesson_key,
        score=score,
        total_questions=total_questions,
        xp_awarded=xp_awarded,
        answers_json=json.dumps(req.selected_indices),
        client_event_id=req.client_event_id,
        attempted_at=attempted_at,
    )
    db.add(attempt)

    award_xp(student, xp_awarded)

    lp = db.scalar(
        select(LessonProgress).where(
            LessonProgress.student_id == student.id,
            LessonProgress.lesson_key == req.lesson_key,
        )
    )
    if lp is None:
        lp = LessonProgress(
            student_id=student.id,
            lesson_key=req.lesson_key,
            completed=True,
            best_quiz_score=score,
            total_questions=total_questions,
            last_studied_at=attempted_at,
        )
        db.add(lp)
    else:
        lp.completed = True
        lp.best_quiz_score = max(lp.best_quiz_score, score)
        lp.last_studied_at = attempted_at

    newly_unlocked: list[str] = []
    if unlock_badge(db, student, "First Lesson"):
        newly_unlocked.append("First Lesson")
    if score == total_questions and req.lesson_key == "math_fractions_ch3":
        if unlock_badge(db, student, "Math Master"):
            newly_unlocked.append("Math Master")

    qdate = attempted_at.date().isoformat() if req.attempted_at else today_iso()
    if lesson.subject == "science":
        advance_quest_progress(db, student, "complete_science", qdate)
    if score == total_questions:
        advance_quest_progress(db, student, "quiz_100", qdate)

    db.commit()
    db.refresh(attempt)
    db.refresh(student)
    db.refresh(lp)

    return QuizAttemptResponse(
        attempt_id=attempt.id,
        lesson_key=req.lesson_key,
        score=score,
        total_questions=total_questions,
        xp_awarded=xp_awarded,
        total_xp=student.total_xp,
        current_level=student.current_level,
        newly_unlocked_badges=newly_unlocked,
        best_quiz_score=lp.best_quiz_score,
        attempted_at=attempt.attempted_at,
    )


@router.post("/p2p/transfers", response_model=P2PTransferResponse)
def log_p2p_transfer(
    req: P2PTransferRequest,
    student: Student = Depends(require_role("student")),
    db: Session = Depends(get_db),
):
    lesson = db.scalar(select(LessonModule).where(LessonModule.lesson_key == req.lesson_key))
    if lesson is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Lesson module not found.")

    if req.client_event_id:
        existing = db.scalar(
            select(P2PTransferLog).where(P2PTransferLog.client_event_id == req.client_event_id)
        )
        if existing is not None:
            return P2PTransferResponse(
                transfer_id=existing.id,
                lesson_key=existing.lesson_key,
                verified=True,
                xp_awarded=0,
                total_xp=student.total_xp,
                newly_unlocked_badges=[],
                transferred_at=existing.transferred_at,
            )

    transferred_at = req.transferred_at or datetime.now(timezone.utc)
    log = P2PTransferLog(
        student_id=student.id,
        lesson_key=req.lesson_key,
        package_name=req.package_name,
        sha256=req.sha256,
        peer_device_name=req.peer_device_name,
        xp_awarded=50,
        client_event_id=req.client_event_id,
        transferred_at=transferred_at,
    )
    db.add(log)
    db.flush()

    award_xp(student, 50)
    newly_unlocked: list[str] = []
    if unlock_badge(db, student, "P2P Sharer"):
        newly_unlocked.append("P2P Sharer")
    if count_student_p2p_transfers(db, student.id) >= 3:
        if unlock_badge(db, student, "P2P Pioneer"):
            newly_unlocked.append("P2P Pioneer")

    qdate = transferred_at.date().isoformat() if req.transferred_at else today_iso()
    advance_quest_progress(db, student, "p2p_share", qdate)

    db.commit()
    db.refresh(log)
    db.refresh(student)

    return P2PTransferResponse(
        transfer_id=log.id,
        lesson_key=log.lesson_key,
        verified=True,
        xp_awarded=50,
        total_xp=student.total_xp,
        newly_unlocked_badges=newly_unlocked,
        transferred_at=log.transferred_at,
    )
