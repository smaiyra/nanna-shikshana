from datetime import datetime, timedelta, timezone
from typing import Callable
import uuid

from argon2 import PasswordHasher
from argon2.exceptions import VerifyMismatchError
from fastapi import Depends, HTTPException, status
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer
import jwt
from sqlalchemy import select
from sqlalchemy.orm import Session

from app.config import settings
from app.database import get_db

_password_hasher = PasswordHasher()
_bearer_scheme = HTTPBearer(auto_error=False)


def hash_secret(raw_secret: str) -> str:
    return _password_hasher.hash(raw_secret)


def verify_secret(raw_secret: str, secret_hash: str) -> bool:
    try:
        return _password_hasher.verify(secret_hash, raw_secret)
    except VerifyMismatchError:
        return False


def create_access_token(
    subject: str,
    role: str = "student",
    expires_minutes: int | None = None,
) -> tuple[str, str, datetime]:
    expire_delta = timedelta(minutes=expires_minutes or settings.ACCESS_TOKEN_EXPIRE_MINUTES)
    now = datetime.now(timezone.utc)
    expires_at = now + expire_delta
    jti = str(uuid.uuid4())
    payload = {
        "sub": subject,
        "role": role,
        "jti": jti,
        "iat": int(now.timestamp()),
        "exp": int(expires_at.timestamp()),
    }
    token = jwt.encode(payload, settings.JWT_SECRET_KEY, algorithm=settings.JWT_ALGORITHM)
    return token, jti, expires_at


def decode_access_token(token: str) -> dict:
    try:
        return jwt.decode(
            token,
            settings.JWT_SECRET_KEY,
            algorithms=[settings.JWT_ALGORITHM],
        )
    except jwt.ExpiredSignatureError as exc:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Authentication token has expired.",
        ) from exc
    except jwt.InvalidTokenError as exc:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid authentication token.",
        ) from exc


def get_current_token_payload(
    credentials: HTTPAuthorizationCredentials | None = Depends(_bearer_scheme),
    db: Session = Depends(get_db),
) -> dict:
    from app.models.student import RevokedToken

    if credentials is None or credentials.scheme.lower() != "bearer":
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Missing or invalid Authorization Bearer token.",
        )
    payload = decode_access_token(credentials.credentials)
    jti = payload.get("jti")
    if jti:
        revoked = db.scalar(select(RevokedToken).where(RevokedToken.jti == jti))
        if revoked is not None:
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Token has been logged out.",
            )
    return payload


def get_current_student(
    payload: dict = Depends(get_current_token_payload),
    db: Session = Depends(get_db),
):
    from app.models.student import Student

    student_id = payload.get("sub")
    if not student_id:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Token missing subject claim.",
        )
    student = db.scalar(select(Student).where(Student.id == student_id))
    if student is None:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Student account not found.",
        )
    return student


def require_role(allowed_role: str = "student") -> Callable:
    def _dependency(
        payload: dict = Depends(get_current_token_payload),
        student=Depends(get_current_student),
    ):
        token_role = payload.get("role")
        if student.role != allowed_role or token_role != allowed_role:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Insufficient permissions for this resource.",
            )
        return student

    return _dependency
