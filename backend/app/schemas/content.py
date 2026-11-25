from datetime import datetime
from pydantic import BaseModel, ConfigDict, Field


class LessonSectionSchema(BaseModel):
    heading_en: str
    heading_kn: str
    body_en: str
    body_kn: str


class QuizQuestionSchema(BaseModel):
    id: str
    question_en: str
    question_kn: str
    options_en: list[str]
    options_kn: list[str]
    correct_index: int
    explanation_en: str
    explanation_kn: str


class LessonModuleSummary(BaseModel):
    id: str
    lesson_key: str
    subject: str
    grade: str
    title_en: str
    title_kn: str
    chapter_en: str
    chapter_kn: str
    description_en: str
    description_kn: str
    diagram_type: str
    file_size_mb: float
    package_name: str
    sha256: str
    version: int


class LessonModuleDetail(LessonModuleSummary):
    audio_text_en: str
    audio_text_kn: str
    content_sections: list[LessonSectionSchema]
    quiz: list[QuizQuestionSchema]


class ContentPackSummary(BaseModel):
    id: str
    pack_code: str
    subject: str
    grade: str
    version: int
    package_name: str
    file_size_mb: float
    sha256: str
    download_url: str
    lesson: LessonModuleSummary
    updated_at: datetime

    model_config = ConfigDict(from_attributes=True)


class ContentPackDetail(ContentPackSummary):
    lesson_detail: LessonModuleDetail


class PackManifestEntry(BaseModel):
    pack_code: str
    package_name: str
    version: int
    sha256: str
    file_size_mb: float
    subject: str
    lesson_key: str


class PackVersionCheckRequest(BaseModel):
    installed_versions: dict[str, int] = Field(default_factory=dict)


class PackVersionCheckResponse(BaseModel):
    updates_available: list[PackManifestEntry]
    up_to_date_codes: list[str]
