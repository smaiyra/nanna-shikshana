from datetime import datetime
from pydantic import BaseModel, ConfigDict, Field


class StudentRegisterRequest(BaseModel):
    student_code: str = Field(..., min_length=3, max_length=64, pattern=r"^[A-Za-z0-9_-]+$")
    device_id: str = Field(..., min_length=3, max_length=128)
    passcode: str = Field(..., min_length=4, max_length=128)
    name: str = Field(default="Ananda", min_length=1, max_length=120)
    village: str = Field(default="Channapatna Rural", min_length=1, max_length=120)
    school: str = Field(default="Govt High School, Channapatna", min_length=1, max_length=180)
    preferred_lang: str = Field(default="en", pattern=r"^(en|kn)$")


class StudentLoginRequest(BaseModel):
    student_code: str = Field(..., min_length=3, max_length=64)
    passcode: str = Field(..., min_length=4, max_length=128)
    device_id: str | None = Field(default=None, max_length=128)


class ProfileUpdateRequest(BaseModel):
    name: str | None = Field(default=None, min_length=1, max_length=120)
    village: str | None = Field(default=None, min_length=1, max_length=120)
    school: str | None = Field(default=None, min_length=1, max_length=180)
    preferred_lang: str | None = Field(default=None, pattern=r"^(en|kn)$")


class StudentProfileResponse(BaseModel):
    id: str
    student_code: str
    device_id: str
    role: str
    name: str
    village: str
    school: str
    preferred_lang: str
    total_xp: int
    current_level: str
    streak_count: int
    streak_freezes_left: int
    last_active_date: str
    created_at: datetime
    updated_at: datetime

    model_config = ConfigDict(from_attributes=True)


class TokenResponse(BaseModel):
    access_token: str
    token_type: str = "bearer"
    expires_at: datetime
    student: StudentProfileResponse
