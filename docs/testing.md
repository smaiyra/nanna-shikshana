# Testing Report & Verification Plan — Nanna Shikshana (ನಮ್ಮ ಶಿಕ್ಷಣ)

## 1. Executed & Verified in This Environment

### Backend (`backend/` — Pytest + SQLite WAL + Alembic)

Executed locally using Python 3.11.2 virtualenv (`/app/applet/backend/.venv`):

1. **Alembic Migration:**
   - Command: `alembic upgrade head`
   - Outcome: **Passed** (`Running upgrade -> 0001_initial`)
2. **Pytest Suite:**
   - Command: `pytest -v`
   - Outcome: **9 passed** (`backend/tests/test_auth.py`, `test_content.py`, `test_progress_gamification.py`, `test_sync.py`):
     - `test_register_login_me_and_logout_revocation`: verifies Argon2 password hashing, JWT issuance, RBAC `/auth/me`, and `jti` token revocation on logout.
     - `test_invalid_login_returns_error`: verifies consistent `ErrorResponse` JSON on invalid credentials.
     - `test_content_packs_list_manifest_and_download_sha256`: verifies all 4 KTBS modules, manifest checksums, `.pkg.zip` download, and SHA-256 verification.
     - `test_version_check_returns_available_updates`: verifies version-check endpoint returns packs needing updates.
     - `test_quiz_completion_quest_claim_and_leaderboard`: verifies `+20 XP` quiz scoring, quest completion/claim, idempotent duplicate guard, and classroom leaderboard ranking.
     - `test_p2p_transfer_log_and_badges`: verifies P2P transfer logging (`+50 XP`) and `P2P Sharer` badge unlocking.
     - `test_sync_push_idempotency_and_pull_cursor`: verifies batched `/sync/push`, `duplicate_ignored` on replay, and `/sync/pull` pagination cursor.
     - `test_sync_conflict_resolution_max_quiz_score`: verifies monotonic `MAX` conflict resolution for `best_quiz_score`.
     - `test_sqlite_wal_and_foreign_keys_enabled`: verifies `PRAGMA journal_mode == 'wal'` and `PRAGMA foreign_keys == 1`.

---

## 2. Not Executed in This Container (Requires Android SDK / JDK 17)

The build container does not have `java`, `gradle`, `adb`, or Android SDK installed. Therefore, the Android Gradle build, Robolectric tests, and Compose emulator tests **were not executed here** and must be run on a workstation or CI runner with JDK 17 + Android SDK 35.

### Android Test Suite & Verification Commands

| Test File | Scope | Command |
|---|---|---|
| `GamificationEngineTest.kt` | XP 3-tier levels, streak `+1` / freeze / `>2d` reset, Kannada digits (`೦–೯`) | `./gradlew testDebugUnitTest --tests "*.GamificationEngineTest"` |
| `RoomOfflineRepositoryTest.kt` | Robolectric + Room in-memory DB offline seeding, quiz completion, quest claiming, P2P badge unlock, queued sync events | `./gradlew testDebugUnitTest --tests "*.RoomOfflineRepositoryTest"` |
| `ComposeScreensUiTest.kt` | Compose UI rendering for `HomeDashboardScreen` filters/quests and `HallOfFameScreen` leaderboard & badge dialog | `./gradlew connectedDebugAndroidTest` |
| Release APK Shrink Check | Verify R8 minification & resource shrinking (`minSdk 24`) | `./gradlew assembleRelease` |

### Manual Emulator Verification Checklist (Low-Spec 2GB RAM Profile + Airplane Mode)

1. Boot an **API 24+ Emulator configured with 2048 MB RAM** and enable **Airplane Mode (Zero Connectivity)**.
2. Launch **Nanna Shikshana** — confirm Home Dashboard loads immediately from Room with Ananda's streak (`5`), `450 XP`, 3 quests, and 4 KTBS modules.
3. Toggle `ಕನ್ನಡ (KN)` ↔ `English (EN)` in the top bar — verify all tabs, labels, Kannada numerals, and Noto Sans Kannada glyphs update cleanly without overlap.
4. Open **Fractions**, **Photosynthesis**, **Kannada Aksharamale**, and **Hoysala Architecture** — verify interactive diagrams, `TextToSpeech` audio, and 3-question quiz completion persist across app restarts while offline.
5. Open **Mesh P2P** — run radar scan, pick a classmate phone, and transfer a package; verify progress bar, SHA-256 checksum card, `+50 XP`, and `P2P Sharer` quest completion.
6. Disable Airplane Mode with FastAPI running on `localhost:8000` — tap **Sync** and verify queued offline events flush cleanly (`/api/v1/sync/push`) without duplicate XP.
