# Screen-to-API & SQLite Mapping — Nanna Shikshana (ನಮ್ಮ ಶಿಕ್ಷಣ)

## 1. Screen, Endpoint & SQLite Mapping Table

| Prototype screen | Android screen | API endpoint(s) | SQLite table(s) | Works offline? |
|---|---|---|---|---|
| Persistent Top Bar (Language Toggle `en`/`kn` + Sync/Offline Strip) | `NannaTopBar` + `SyncStatusBanner` | `POST /api/v1/auth/device-register`, `POST /api/v1/auth/login`, `GET /api/v1/auth/me`, `POST /api/v1/sync/push`, `GET /api/v1/sync/pull` | `students`, `sync_events`, `sync_cursors` (Room: `student_profile`, `pending_sync_events`, `app_preferences`) | Yes (DataStore + Room primary; background WorkManager sync) |
| Home Dashboard (`Ad`) — Streak/Level Hero, Daily Quests, KTBS Curriculum Feed | `HomeDashboardScreen` | `GET /api/v1/content/packs`, `GET /api/v1/content/packs/manifest`, `GET /api/v1/gamification/profile`, `POST /api/v1/gamification/quests/{quest_id}/claim`, `POST /api/v1/sync/push`, `GET /api/v1/sync/pull` | `students`, `content_packs`, `lesson_modules`, `daily_quests`, `quest_progress`, `xp_ledger`, `sync_events` | Yes (pre-bundled + downloaded packs in Room; quest claims queued for sync) |
| Lesson Reader & Interactive Models (`qd` `reader` mode: Fractions Disc, Photosynthesis, Aksharamale Soundboard, Hoysala Blueprint + Kannada TTS) | `LessonReaderScreen` + Compose Canvas/SVG Diagrams + Android `TextToSpeech` | `GET /api/v1/content/packs/{pack_id}`, `GET /api/v1/content/packs/{pack_id}/download`, `POST /api/v1/progress/lessons/{lesson_id}/view` | `content_packs`, `lesson_modules`, `lesson_sections`, `aksharamale_vowels`, `lesson_progress` | Yes (100% local Room + local downloaded pack archive + on-device `TextToSpeech`) |
| Knowledge Check Quiz (`qd` `quiz` mode: 3-Q MCQ + Feedback + Score Summary) | `QuizScreen` (mode within `LessonReaderRoute`) | `POST /api/v1/progress/quizzes/attempt`, `POST /api/v1/sync/push` | `quiz_questions`, `quiz_attempts`, `lesson_progress`, `xp_ledger`, `student_badges`, `quest_progress`, `sync_events` | Yes (scored locally in Room, XP/badges/quests updated immediately, synced later) |
| Wi-Fi Direct P2P Mesh Sharing (`Jd` — Package Picker, Peer Radar, Transfer + SHA-256 Verification) | `MeshP2PScreen` (`WifiP2pManager` + emulator/single-device fallback) | `POST /api/v1/p2p/transfers`, `POST /api/v1/sync/push` (telemetry backup only; transfer itself is device-to-device) | `content_packs`, `p2p_transfer_logs`, `xp_ledger`, `student_badges`, `quest_progress`, `sync_events` | Yes (air-gapped Wi-Fi Direct socket transfer + local SHA-256 verification) |
| Hall of Fame (`Yd` — Classroom Leaderboard + 7 Achievement Badges + Badge Detail Modal) | `HallOfFameScreen` + `BadgeDetailDialog` | `GET /api/v1/gamification/leaderboard`, `GET /api/v1/gamification/badges`, `GET /api/v1/sync/pull` | `students`, `leaderboard_entries`, `badges`, `student_badges` | Yes (Room cache + local user XP/streak merged live; refreshes on sync) |
| Credits / About (`Xd` — Problem Statement, Architecture Specs, DBIT Mentor & Team) | `CreditsScreen` | None required (static institutional metadata; optional `GET /api/v1/content/packs/manifest` for local pack stats) | None (string resources `strings.xml` / `values-kn/strings.xml`) | Yes (100% static native Compose UI) |
| Faculty / Hackathon Demo Helpers (`$d` — Streak Simulation, +50 XP, Reset Demo Data) | `DemoHelpersBottomSheet` | `POST /api/v1/sync/push` (or local-only reset/simulation with opt-in sync) | `student_profile`, `quest_progress`, `lesson_progress`, `pending_sync_events` (Room) | Yes (operates directly on Room & DataStore) |

---

## 2. Not Accessible

- **None.** All screens, modals, interactive models, and data arrays from `https://nanna-shikshana.ai.studio/` were inspected directly from the live application bundle.

---

## 3. Minimal Backend Schema Scope (SQLite WAL Mode)

Only tables directly required by the prototype screens and offline-first sync:
1. `students` (UUID PK, `student_code`, `device_id`, `secret_hash` Argon2, `name`, `village`, `school`, `role`=`student`, `total_xp`, `current_level`, `streak_count`, `streak_freezes_left`, `last_active_date`, `preferred_lang`, `created_at`, `updated_at`)
2. `content_packs` (UUID PK, `pack_code`, `subject`, `grade`, `version`, `package_name`, `file_size_mb`, `sha256`, `payload_json` / downloadable zip path, `created_at`, `updated_at`)
3. `lesson_modules` (UUID PK, `pack_id` FK, `lesson_key` e.g. `math_fractions_ch3`, `title_en`, `title_kn`, `chapter_en`, `chapter_kn`, `description_en`, `description_kn`, `diagram_type`, `audio_text_en`, `audio_text_kn`, `sections_json`, `quiz_json`, `created_at`, `updated_at`)
4. `lesson_progress` (UUID PK, `student_id` FK, `lesson_key`, `completed`, `best_quiz_score`, `total_questions`, `last_studied_at`, `created_at`, `updated_at`)
5. `quiz_attempts` (UUID PK, `student_id` FK, `lesson_key`, `score`, `total_questions`, `xp_awarded`, `answers_json`, `client_event_id` unique, `attempted_at`, `created_at`, `updated_at`)
6. `badges` & `student_badges` (7 prototype badges + unlocked records per student with `unlocked_at`)
7. `daily_quests` & `quest_progress` (3 prototype quests + `quest_date`, `progress`, `max_progress`, `claimed`, `claimed_at`)
8. `p2p_transfer_logs` (UUID PK, `student_id` FK, `lesson_key`, `package_name`, `sha256`, `peer_device_name`, `xp_awarded`, `client_event_id` unique, `transferred_at`, `created_at`, `updated_at`)
9. `sync_events` (UUID PK, `student_id` FK, `event_id` client UUID, `idempotency_key` unique, `event_type`, `payload_json`, `server_seq` monotonic cursor, `status`, `created_at`, `updated_at`)

---

## 4. Offline Sync & Conflict Resolution Rule

- **Push (`POST /api/v1/sync/push`):** Accepts batched offline events (`quiz_completed`, `quest_claimed`, `p2p_transfer_completed`, `streak_updated`, `profile_updated`), each carrying `event_id` (UUID) and `idempotency_key`. Duplicate `idempotency_key` entries are safely ignored (`status: "duplicate_ignored"`).
- **Pull (`GET /api/v1/sync/pull?since=<cursor>`):** Returns paginated server changes (`server_seq > since`) including updated pack versions, student progress/gamification state, and classroom leaderboard snapshot.
- **Conflict Rule:** Append-only events (`quiz_attempts`, `p2p_transfer_logs`, `xp_ledger`) are deduplicated by `idempotency_key`; `lesson_progress.best_quiz_score` uses monotonic `MAX(server, client)`; `unlocked_badges` uses set union; `streak_count` / `last_active_date` / `preferred_lang` use deterministic `last_write_wins` by `client_timestamp` with server-side streak validation.
