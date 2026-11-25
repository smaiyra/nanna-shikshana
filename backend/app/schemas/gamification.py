from datetime import datetime
from pydantic import BaseModel, ConfigDict


class LevelInfoResponse(BaseModel):
    level_number: int
    title_en: str
    title_kn: str
    current_xp: int
    next_level_xp: int
    needed_xp: int
    progress_pct: float
    color_hex: str


class QuestStatusResponse(BaseModel):
    quest_key: str
    title_en: str
    title_kn: str
    sub_en: str
    sub_kn: str
    xp_reward: int
    target_type: str
    progress: int
    max_progress: int
    claimed: bool


class ClaimQuestResponse(BaseModel):
    quest_key: str
    xp_awarded: int
    total_xp: int
    current_level: str
    already_claimed: bool


class BadgeResponse(BaseModel):
    badge_key: str
    name_en: str
    name_kn: str
    description_en: str
    description_kn: str
    icon: str
    rarity: str
    unlocked: bool
    unlocked_at: datetime | None = None


class LeaderboardEntryResponse(BaseModel):
    rank: int
    id: str
    name: str
    village: str
    school: str
    total_xp: int
    streak: int
    is_current_user: bool


class GamificationProfileResponse(BaseModel):
    student_id: str
    student_code: str
    name: str
    village: str
    school: str
    total_xp: int
    current_level: str
    level_info: LevelInfoResponse
    streak_count: int
    streak_freezes_left: int
    last_active_date: str
    unlocked_badges: list[str]
    completed_lessons: list[str]
    completed_quizzes: dict[str, int]
    quests: list[QuestStatusResponse]

    model_config = ConfigDict(from_attributes=True)
