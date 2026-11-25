import json
import math
from pathlib import Path

from fastapi import APIRouter, Depends, HTTPException, Query, status
from fastapi.responses import FileResponse
from sqlalchemy import func, select
from sqlalchemy.orm import Session, joinedload

from app.database import get_db
from app.models.content import ContentPack, LessonModule
from app.models.student import Student
from app.schemas.common import PaginatedResponse, PaginationMeta
from app.schemas.content import (
    ContentPackDetail,
    ContentPackSummary,
    LessonModuleDetail,
    LessonModuleSummary,
    LessonSectionSchema,
    PackManifestEntry,
    PackVersionCheckRequest,
    PackVersionCheckResponse,
    QuizQuestionSchema,
)
from app.security import require_role

router = APIRouter(prefix="/content", tags=["content"])


def _to_lesson_summary(pack: ContentPack, lesson: LessonModule) -> LessonModuleSummary:
    return LessonModuleSummary(
        id=lesson.id,
        lesson_key=lesson.lesson_key,
        subject=lesson.subject,
        grade=lesson.grade,
        title_en=lesson.title_en,
        title_kn=lesson.title_kn,
        chapter_en=lesson.chapter_en,
        chapter_kn=lesson.chapter_kn,
        description_en=lesson.description_en,
        description_kn=lesson.description_kn,
        diagram_type=lesson.diagram_type,
        file_size_mb=pack.file_size_mb,
        package_name=pack.package_name,
        sha256=pack.sha256,
        version=pack.version,
    )


def _to_pack_summary(pack: ContentPack) -> ContentPackSummary:
    lesson = pack.lessons[0]
    return ContentPackSummary(
        id=pack.id,
        pack_code=pack.pack_code,
        subject=pack.subject,
        grade=pack.grade,
        version=pack.version,
        package_name=pack.package_name,
        file_size_mb=pack.file_size_mb,
        sha256=pack.sha256,
        download_url=f"/api/v1/content/packs/{pack.id}/download",
        lesson=_to_lesson_summary(pack, lesson),
        updated_at=pack.updated_at,
    )


@router.get("/packs", response_model=PaginatedResponse[ContentPackSummary])
def list_content_packs(
    subject: str | None = Query(default=None, pattern=r"^(math|science|kannada|social)$"),
    page: int = Query(default=1, ge=1),
    page_size: int = Query(default=20, ge=1, le=100),
    _student: Student = Depends(require_role("student")),
    db: Session = Depends(get_db),
):
    base_query = select(ContentPack).options(joinedload(ContentPack.lessons))
    count_query = select(func.count(ContentPack.id))
    if subject:
        base_query = base_query.where(ContentPack.subject == subject)
        count_query = count_query.where(ContentPack.subject == subject)

    total_items = db.scalar(count_query) or 0
    total_pages = max(1, math.ceil(total_items / page_size))
    offset = (page - 1) * page_size

    packs = list(
        db.scalars(base_query.order_by(ContentPack.package_name.asc()).offset(offset).limit(page_size)).unique()
    )
    items = [_to_pack_summary(p) for p in packs if p.lessons]
    return PaginatedResponse(
        items=items,
        pagination=PaginationMeta(
            page=page,
            page_size=page_size,
            total_items=total_items,
            total_pages=total_pages,
        ),
    )


@router.get("/packs/manifest", response_model=list[PackManifestEntry])
def get_pack_manifest(
    _student: Student = Depends(require_role("student")),
    db: Session = Depends(get_db),
):
    packs = list(db.scalars(select(ContentPack).options(joinedload(ContentPack.lessons))).unique())
    return [
        PackManifestEntry(
            pack_code=p.pack_code,
            package_name=p.package_name,
            version=p.version,
            sha256=p.sha256,
            file_size_mb=p.file_size_mb,
            subject=p.subject,
            lesson_key=p.lessons[0].lesson_key,
        )
        for p in packs
        if p.lessons
    ]


@router.post("/packs/version-check", response_model=PackVersionCheckResponse)
def check_pack_versions(
    req: PackVersionCheckRequest,
    _student: Student = Depends(require_role("student")),
    db: Session = Depends(get_db),
):
    packs = list(db.scalars(select(ContentPack).options(joinedload(ContentPack.lessons))).unique())
    updates: list[PackManifestEntry] = []
    up_to_date: list[str] = []

    for p in packs:
        if not p.lessons:
            continue
        installed_ver = req.installed_versions.get(p.pack_code, 0)
        if installed_ver < p.version:
            updates.append(
                PackManifestEntry(
                    pack_code=p.pack_code,
                    package_name=p.package_name,
                    version=p.version,
                    sha256=p.sha256,
                    file_size_mb=p.file_size_mb,
                    subject=p.subject,
                    lesson_key=p.lessons[0].lesson_key,
                )
            )
        else:
            up_to_date.append(p.pack_code)

    return PackVersionCheckResponse(updates_available=updates, up_to_date_codes=up_to_date)


@router.get("/packs/{pack_id}", response_model=ContentPackDetail)
def get_pack_detail(
    pack_id: str,
    _student: Student = Depends(require_role("student")),
    db: Session = Depends(get_db),
):
    pack = db.scalar(
        select(ContentPack)
        .options(joinedload(ContentPack.lessons))
        .where((ContentPack.id == pack_id) | (ContentPack.pack_code == pack_id))
    )
    if pack is None or not pack.lessons:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Content pack not found.")

    lesson = pack.lessons[0]
    sections = [LessonSectionSchema(**s) for s in json.loads(lesson.sections_json)]
    quiz = [QuizQuestionSchema(**q) for q in json.loads(lesson.quiz_json)]
    lesson_detail = LessonModuleDetail(
        **_to_lesson_summary(pack, lesson).model_dump(),
        audio_text_en=lesson.audio_text_en,
        audio_text_kn=lesson.audio_text_kn,
        content_sections=sections,
        quiz=quiz,
    )
    summary = _to_pack_summary(pack)
    return ContentPackDetail(**summary.model_dump(), lesson_detail=lesson_detail)


@router.get("/packs/{pack_id}/download")
def download_pack_archive(
    pack_id: str,
    _student: Student = Depends(require_role("student")),
    db: Session = Depends(get_db),
):
    pack = db.scalar(
        select(ContentPack).where((ContentPack.id == pack_id) | (ContentPack.pack_code == pack_id))
    )
    if pack is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Content pack not found.")
    archive = Path(pack.archive_path)
    if not archive.exists():
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Pack archive file missing on server.")

    return FileResponse(
        path=archive,
        media_type="application/zip",
        filename=pack.package_name,
        headers={
            "X-Pack-Version": str(pack.version),
            "X-Pack-SHA256": pack.sha256,
            "ETag": f'"{pack.sha256}"',
        },
    )
