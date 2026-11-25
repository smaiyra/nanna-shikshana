# Setup & Build Guide — Nanna Shikshana (ನಮ್ಮ ಶಿಕ್ಷಣ)

## 1. Prerequisites

- **Backend:** Python 3.11+ (3.12+ recommended), `pip` / `venv`
- **Android:** Android Studio Ladybug / Koala+ (or JDK 17 + Android SDK Platform 35, Build-Tools 35, Android SDK Platform-Tools)

---

## 2. Backend Setup (`backend/`)

```bash
cd backend
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt

# Copy environment template and configure a strong JWT secret for production
cp .env.example .env

# Run Alembic migrations (creates SQLite DB in WAL mode)
alembic upgrade head

# Start FastAPI server (auto-seeds KTBS packs, quests, badges, and peer leaderboard)
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```

### Verify Backend Health & API Docs

- Health check: `GET http://127.0.0.1:8000/health`
- Interactive OpenAPI docs: `http://127.0.0.1:8000/docs`
- API prefix: `/api/v1/`

---

## 3. Android App Setup (`android/`)

### Configure Backend Base URL

In `android/app/build.gradle.kts`, `API_BASE_URL` defaults to the Android Emulator loopback host:

```kotlin
buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:8000/api/v1/\"")
```

- **Android Emulator:** keep `http://10.0.2.2:8000/api/v1/`
- **Physical device on same Wi-Fi LAN:** update `API_BASE_URL` to your computer's LAN IP (e.g., `http://192.168.1.50:8000/api/v1/`) and add the IP to `res/xml/network_security_config.xml` if using plain HTTP in local dev.

### Build Debug & Release APKs

Open `/android` in Android Studio or run via Gradle:

```bash
cd android

# Debug APK
./gradlew assembleDebug

# Minified & Resource-Shrunk Release APK (optimized for 2GB-3GB RAM budget devices)
./gradlew assembleRelease
```

Outputs:
- Debug APK: `android/app/build/outputs/apk/debug/app-debug.apk`
- Release APK: `android/app/build/outputs/apk/release/app-release-unsigned.apk`

---

## 4. Kannada Font & Localization Verification

- Bundled Noto Sans Kannada fonts reside in `android/app/src/main/res/font/noto_sans_kannada_regular.ttf` and `noto_sans_kannada_bold.ttf`.
- All UI text is defined in `android/app/src/main/res/values/strings.xml` and `android/app/src/main/res/values-kn/strings.xml`.
- Toggling the header pill (`ಕನ್ನಡ (KN)` ↔ `English (EN)`) updates Compose `LocalContext` & `LocalConfiguration` dynamically and persists the choice in DataStore.
