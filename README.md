# Nanna Shikshana (ನಮ್ಮ ಶಿಕ್ಷಣ)

**Offline-first gamified learning platform for rural Karnataka students on low-spec budget smartphones (Android `minSdk 24+`) with a FastAPI + SQLite (WAL mode) sync backend.**

---

## Architecture Overview

- **Native Android App (`android/`):** Kotlin, Jetpack Compose, Material 3, Room (SQLite primary offline store), Jetpack DataStore, WorkManager background sync, Hilt DI, Retrofit/OkHttp, Android `TextToSpeech`, and `WifiP2pManager` Wi-Fi Direct mesh sharing (with single-device/emulator fallback).
- **Backend API (`backend/`):** Python 3.11+, FastAPI, SQLAlchemy 2.0, Alembic migrations, SQLite in WAL mode with foreign keys enabled, Argon2 password hashing (`argon2-cffi`), JWT auth with logout revocation, and idempotent offline sync endpoints under `/api/v1/`.
- **Bilingual Support:** Full English (`en`) and Kannada (`kn`) runtime switching via `res/values/strings.xml`, `res/values-kn/strings.xml`, and bundled `Noto Sans Kannada` fonts (`minSdk 24+`).

---

## Repository Layout

```text
├── android/                  # Native Kotlin + Jetpack Compose Android project
│   ├── app/src/main/         # Compose screens, Room DB, Hilt DI, P2P mesh, WorkManager sync
│   ├── app/src/test/         # JUnit4 + Robolectric Room offline tests
│   └── app/src/androidTest/  # Jetpack Compose instrumentation UI tests
├── backend/                  # FastAPI + SQLAlchemy 2.0 + SQLite (WAL) backend
│   ├── app/                  # Models, schemas, services, routers (/api/v1/)
│   ├── migrations/           # Alembic schema migrations
│   └── tests/                # Pytest integration & sync conflict test suite
└── docs/                     # Prototype analysis, API mapping, DB schema, setup & testing docs
    ├── prototype-analysis.md
    ├── screen-api-mapping.md
    ├── database-schema.md
    ├── setup-guide.md
    └── testing.md
```

---

## Implemented Screens & Features (1:1 Prototype Fidelity)

1. **Home Dashboard (`HomeDashboardScreen`):** Offline/sync banner, gamification hero card (streak counter, streak freeze shields, 3-tier XP progress bar), 3 Daily Quests with instant `Claim XP`, and KTBS curriculum feed with subject filters (`All`, `Math`, `Science`, `Kannada`, `Social`) and versioned pack downloads.
2. **Lesson Reader & Interactive Models (`LessonReaderScreen` & `InteractiveModels`):**
   - **Math (`math_fractions_ch3`):** Interactive Roti Fraction Disc (tap slices, change denominator `2/3/4/6/8`, Kannada digits).
   - **Science (`sci_photosynthesis_ch5`):** Interactive Photosynthesis Canvas (toggle sunlight & water inputs, live reaction status).
   - **Kannada (`kan_aksharamale_ch2`):** 15-vowel Aksharamale Interactive Soundboard with on-device TTS pronunciation.
   - **Social Studies (`soc_hoysala_ch4`):** Belur Chennakeshava Hoysala Stellate Architecture Blueprint (`Star Platform`, `Pillars`, `Frieze Bands`).
   - **3-Question Mini Quiz:** Instant explanation feedback, sound chimes, `+20 XP` per correct answer, monotonic best-score persistence.
3. **Wi-Fi Direct Mesh P2P (`MeshP2PScreen`):** 3-step package selector, nearby peer radar scan, progress transfer, and SHA-256 verification.
4. **Hall of Fame (`HallOfFameScreen`):** Rural classroom leaderboard + 7 achievement badges with detail modal.
5. **Credits (`CreditsScreen`) & Demo Helpers (`DemoHelpersBottomSheet`):** DBIT Bangalore architecture overview, streak freeze/reset testing, `+50 XP`, and demo reset.

---

## Quickstart

See [docs/setup-guide.md](docs/setup-guide.md) for backend and Android build instructions, and [docs/testing.md](docs/testing.md) for test verification results.
