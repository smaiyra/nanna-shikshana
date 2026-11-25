from datetime import datetime, timezone

from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy import select
from sqlalchemy.orm import Session

from app.database import get_db
from app.models.student import RevokedToken, Student
from app.schemas.auth import (
    ProfileUpdateRequest,
    StudentLoginRequest,
    StudentProfileResponse,
    StudentRegisterRequest,
    TokenResponse,
)
from app.security import (
    create_access_token,
    get_current_token_payload,
    hash_secret,
    require_role,
    verify_secret,
)
from app.services.gamification_service import calculate_level_info, today_iso

router = APIRouter(prefix="/auth", tags=["auth"])


@router.post("/register", response_model=TokenResponse, status_code=status.HTTP_201_CREATED)
def register_student(req: StudentRegisterRequest, db: Session = Depends(get_db)):
    existing = db.scalar(select(Student).where(Student.student_code == req.student_code))
    if existing is not None:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail=f"Student code '{req.student_code}' is already registered.",
        )

    level_info = calculate_level_info(0)
    student = Student(
        student_code=req.student_code,
        device_id=req.device_id,
        secret_hash=hash_secret(req.passcode),
        role="student",
        name=req.name,
        village=req.village,
        school=req.school,
        preferred_lang=req.preferred_lang,
        total_xp=0,
        current_level=level_info.title_en,
        streak_count=1,
        streak_freezes_left=2,
        last_active_date=today_iso(),
    )
    db.add(student)
    db.commit()
    db.refresh(student)

    token, _jti, expires_at = create_access_token(subject=student.id, role=student.role)
    return TokenResponse(
        access_token=token,
        expires_at=expires_at,
        student=StudentProfileResponse.model_validate(student),
    )


@router.post("/login", response_model=TokenResponse)
def login_student(req: StudentLoginRequest, db: Session = Depends(get_db)):
    student = db.scalar(select(Student).where(Student.student_code == req.student_code))
    if student is None or not verify_secret(req.passcode, student.secret_hash):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid student code or passcode.",
        )

    if req.device_id and req.device_id != student.device_id:
        student.device_id = req.device_id
        db.commit()
        db.refresh(student)

    token, _jti, expires_at = create_access_token(subject=student.id, role=student.role)
    return TokenResponse(
        access_token=token,
        expires_at=expires_at,
        student=StudentProfileResponse.model_validate(student),
    )


@router.post("/logout", status_code=status.HTTP_204_NO_CONTENT)
def logout_student(
    payload: dict = Depends(get_current_token_payload),
    student: Student = Depends(require_role("student")),
    db: Session = Depends(get_db),
):
    jti = payload.get("jti")
    exp = payload.get("exp")
    if jti:
        existing = db.scalar(select(RevokedToken).where(RevokedToken.jti == jti))
        if existing is None:
            expires_at = (
                datetime.fromtimestamp(exp, tz=timezone.utc)
                if exp
                else datetime.now(timezone.utc)
            )
            db.add(RevokedToken(jti=jti, student_id=student.id, expires_at=expires_at))
            db.commit()


@router.get("/me", response_model=StudentProfileResponse)
def get_me(student: Student = Depends(require_role("student"))):
    return StudentProfileResponse.model_validate(student)


@router.patch("/profile", response_model=StudentProfileResponse)
def update_profile(
    req: ProfileUpdateRequest,
    student: Student = Depends(require_role("student")),
    db: Session = Depends(get_db),
):
    if req.name is not None:
        student.name = req.name
    if req.village is not None:
        student.village = req.village
    if req.school is not None:
        student.school = req.school
    if req.preferred_lang is not None:
        student.preferred_lang = req.preferred_lang
    db.commit()
    db.refresh(student)
    return StudentProfileResponse.model_validate(student)
