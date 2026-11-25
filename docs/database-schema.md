# Database Schema & Sync Architecture — Nanna Shikshana (ನಮ್ಮ ಶಿಕ್ಷಣ)

Nanna Shikshana uses an **SQLite-only** persistence architecture across both tiers:
1. **Android Local Primary Store (`Room` SQLite):** Immediate offline-first reads/writes, zero network dependency.
2. **FastAPI Backend Store (`SQLite 3` in WAL Mode):** Persistent backup, versioned `.pkg.zip` distribution, classroom leaderboard aggregation, and idempotent event sync.

---

## 1. Backend SQLite Configuration (`/backend/app/database.py`)

Every SQLAlchemy connection enforces SQLite PRAGMA settings:

```sql
PRAGMA journal_mode=WAL;
PRAGMA foreign_keys=ON;
PRAGMA synchronous=NORMAL;
PRAGMA busy_timeout=5000;
```

All schema migrations live under `/backend/migrations/` (Alembic), starting with `20261124_0001_initial_schema.py`.

---

## 2. Backend Tables (SQLite + SQLAlchemy 2.0)

| Table | Primary Key | Foreign Keys | Key Constraints & Indexes | Purpose |
|---|---|---|---|---|
| `students` | `id` (UUID str) | — | `UNIQUE(student_code)`, `INDEX(school)` | Student profile, Argon2 `secret_hash`, `role='student'`, `total_xp`, `current_level`, `streak_count`, `streak_freezes_left`, `last_active_date`, `preferred_lang` (`en`/`kn`) |
| `revoked_tokens` | `jti` (UUID str) | `student_id -> students.id` (CASCADE) | `INDEX(expires_at)` | Persists logged-out JWT IDs until expiry |
| `content_packs` | `id` (UUID str) | — | `UNIQUE(pack_code)`, `INDEX(subject)` | Versioned KTBS modules (`version`, `package_name`, `file_size_mb`, `sha256`, `archive_path`, metadata) |
| `lesson_modules` | `id` (UUID str) | `pack_id -> content_packs.id` (CASCADE) | `UNIQUE(lesson_key)` | Bilingual lesson details (`title_en/kn`, `chapter_en/kn`, `audio_text_en/kn`, `diagram_type`, `sections_json`, `quiz_json`) |
| `lesson_progress` | `id` (UUID str) | `student_id -> students.id` (CASCADE) | `UNIQUE(student_id, lesson_key)` | Tracks completion flag and monotonic `best_quiz_score` |
| `quiz_attempts` | `id` (UUID str) | `student_id -> students.id` (CASCADE) | `UNIQUE(client_event_id)`, `INDEX(student_id, lesson_key)` | Immutable audit trail of 3-question quiz submissions and XP awarded |
| `badges` | `id` (UUID str) | — | `UNIQUE(badge_key)` | 7 prototype badges across `bronze`, `silver`, `gold`, `platinum` |
| `student_badges` | `id` (UUID str) | `student_id -> students.id`, `badge_id -> badges.id` | `UNIQUE(student_id, badge_id)` | Set-union unlocked badges per student with UTC timestamp |
| `daily_quests` | `id` (UUID str) | — | `UNIQUE(quest_key)` | 3 prototype daily quests (`quest_science`, `quest_quiz_100`, `quest_p2p_share`) |
| `quest_progress` | `id` (UUID str) | `student_id -> students.id`, `quest_id -> daily_quests.id` | `UNIQUE(student_id, quest_id, quest_date)` | Per-day progress & reward claim flag |
| `p2p_transfer_logs` | `id` (UUID str) | `student_id -> students.id` (CASCADE) | `UNIQUE(client_event_id)` | Offline Wi-Fi Direct transfer records with SHA-256 verification |
| `xp_ledger` | `id` (UUID str) | `student_id -> students.id` (CASCADE) | `UNIQUE(idempotency_key)` | Append-only XP delta journal (`quiz`, `quest`, `p2p`, `demo`) |
| `leaderboard_peers` | `id` (UUID str) | — | `INDEX(total_xp)` | Seeded rural Karnataka classroom peers merged with live `students` |
| `sync_events` | `id` (UUID str) | `student_id -> students.id` (CASCADE) | `UNIQUE(idempotency_key)`, `UNIQUE(server_seq)` | Monotonic cursor log (`server_seq`) powering `/sync/push` and `/sync/pull` |

All tables include UTC `created_at` and `updated_at` timestamps.

---

## 3. Android Room Local Schema (`/android/.../data/local/entity/Entities.kt`)

| Room Entity | Table Name | Primary Key | Notes |
|---|---|---|---|
| `StudentProfileEntity` | `student_profile` | `id` | Offline primary store for Ananda (`totalXp`, `currentLevel`, `streakCount`, `streakFreezesLeft`, `lastActiveDate`) |
| `LessonPackEntity` | `lesson_packs` | `id` | Bundled KTBS modules + `packCode`, `version`, `sha256`, `isDownloaded`, `localArchivePath`, `sectionsJson`, `quizJson` |
| `LessonProgressEntity` | `lesson_progress` | `lessonId` | Completion flag and monotonic `bestQuizScore` |
| `DailyQuestEntity` | `daily_quests` | `id` | 3 daily quests with local `progress` and `claimed` state |
| `BadgeEntity` | `badges` | `id` | 7 badges with `unlocked` state |
| `LeaderboardPeerEntity` | `leaderboard_peers` | `id` | Cached classroom peers merged live with user's `StudentProfileEntity` |
| `PendingSyncEventEntity` | `pending_sync_events` | `eventId` | Outbox queue (`idempotencyKey`, `eventType`, `payloadJson`, `synced`, `retryCount`) |

Non-relational preferences (`preferred_lang`, JWT access token, device ID, last sync cursor) are stored in Jetpack **DataStore**.

---

## 4. Offline Sync & Conflict Resolution Rules

1. **Outbox Queue (`pending_sync_events`):** Every offline user mutation (`lesson_viewed`, `quiz_completed`, `quest_claimed`, `p2p_transfer_completed`, `streak_updated`, `profile_updated`) is saved atomically to Room first, then queued with a deterministic `idempotency_key`.
2. **Idempotent Push (`POST /api/v1/sync/push`):**
   - Duplicate `idempotency_key` submissions are ignored cleanly (`status = "duplicate_ignored"`), preventing double XP awards on flaky 2G/3G retries.
   - `lesson_progress.best_quiz_score` resolves with monotonic `MAX(server, client)`.
   - `student_badges` resolves by set union.
   - `preferred_lang` and `last_active_date` resolve via deterministic timestamp comparison (`last_write_wins`).
3. **Incremental Pull (`GET /api/v1/sync/pull?since=<server_seq>`):**
   - Returns paginated changes (`limit <= 100`) and updates the client's `sync_cursor` in DataStore.
4. **Resumable / Compact Content Packs:**
   - Packs are compact ZIP archives (`< 1.5 MB`) verified via `X-Pack-SHA256` before marking `isDownloaded = true` in Room.
