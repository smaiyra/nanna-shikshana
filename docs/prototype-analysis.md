# Prototype Analysis — Nanna Shikshana (ನಮ್ಮ ಶಿಕ್ಷಣ)

**Source of Truth Inspected:** `https://nanna-shikshana.ai.studio/` (live React + Tailwind CSS prototype bundle `assets/index-CMgy_w8M.js` and `assets/index-B8miQNdy.css`)  
**Published Description:** *"Offline-first gamified learning platform prototype for rural Karnataka students on low-spec budget smartphones with zero internet connectivity."*

---

## 1. Visual Design & Platform Context (Observed in Prototype)

- **Colors:**
  - App background: `#F8FAFC` (Slate 50)
  - Card surfaces: `#FFFFFF` with `#E2E8F0` borders (`rounded-3xl` / `24dp` cards, `rounded-2xl` / `16dp` controls)
  - Primary brand blue: `#0EA5E9` / `#0284C7` (Sky 500/600)
  - Gamification hero & P2P dark cards: `#0F172A` → `#1E293B` → `#082F49`
  - Accents: Purple `#8B5CF6` (Level/Badges), Emerald `#10B981` (Completion/Correct/Verified), Amber `#F59E0B` (Streak/Quests/Leaderboard), Rose `#E11D48` (Incorrect/Reset)
- **Typography:**
  - English/UI: `Plus Jakarta Sans`
  - Kannada script: `Noto Sans Kannada` (must be bundled in `android/app/src/main/res/font/`)
  - Monospace metrics/hashes: `JetBrains Mono`
- **Prototype-Only Browser Wrapper (Not replicated as fake UI on Android):**
  - Top bar `DBIT Hackathon Prototype` + `Phone Shell` / `Full Screen` toggle
  - Simulated iOS-style status bar (`9:41`, notch, `87%` battery)

---

## 2. User Roles & Authentication

- **Observed Roles:** **1 role only — `student`** (default demo profile: `Ananda`, village `Channapatna Rural`, school `Govt High School, Channapatna`).
- **Observed Auth UI:** None. The prototype launches directly into the Home Dashboard with zero login/registration forms and no teacher/admin screens.
- **Persistence in Prototype:** `localStorage` (`nanna_shikshana_user_profile_v1`, `nanna_shikshana_quests_v1`, `nanna_shikshana_lang_v1`), labeled in UI as `Local SQLite / IndexedDB`.
- **Target Native/Backend Identity Design (per Section 5):**
  - Since the prototype has no accounts, use a **device / student-code identity** (`student_code` + `device_id` + Argon2-hashed secret) with JWT access/refresh tokens for backend sync (`/api/v1/auth/register`, `/api/v1/auth/login`, `/api/v1/auth/logout`, `/api/v1/auth/me`).
  - On first launch offline, Android initializes a local Room `student_profile` immediately (usable with zero internet) and registers/authenticates with the backend when connectivity is available.

---

## 3. Language Support

- **Languages:** English (`en`) and Kannada (`kn`).
- **Toggle Control:** Persistent pill button in the top app bar (`ಕನ್ನಡ (KN)` ↔ `English (EN)`), persisted locally and triggering a UI chime (`Nd()`).
- **Scope:** 100% of labels, navigation tabs, lesson titles/chapters/descriptions/body sections, interactive diagram labels/Kannada numerals (`೦–೯`), audio TTS text (`audio_text_kn` / `audio_text_en`), quizzes, quests, badges, P2P mesh labels, leaderboard headers, and about/credits copy switch dynamically.

---

## 4. Navigation Architecture

### Persistent Chrome
- **Top App Bar:**
  - App title (`Nanna Shikshana | ನಮ್ಮ ಶಿಕ್ಷಣ` or `ನಮ್ಮ ಶಿಕ್ಷಣ | Nanna Shikshana`)
  - Institution subtitle (`Don Bosco Institute of Technology` / `ಡಾನ್ ಬಾಸ್ಕೋ ಇನ್‌ಸ್ಟಿಟ್ಯೂಟ್ ಆಫ್ ಟೆಕ್ನಾಲಜಿ`)
  - Language toggle button (`ಕನ್ನಡ (KN)` / `English (EN)`)
- **Bottom Navigation Bar (5 Tabs):**
  1. **Home** (`tabHome` / `ಮುಖಪುಟ`)
  2. **Reader & Quiz** (`tabLearn` / `ಪಾಠ & ರಸಪ್ರಶ್ನೆ`)
  3. **Mesh P2P** (`tabP2P` / `ಮೆಶ್ ಪಿ೨ಪಿ`)
  4. **Hall of Fame** (`tabRank` / `ಶ್ರೇಯಾಂಕ`)
  5. **Credits** (`tabAbout` / `ಮಾಹಿತಿ`)
- **Floating Overlay Controls:**
  - Bottom-right FAB (`Demo Tools`) → Opens `Faculty / Hackathon Demo Helpers` modal sheet.
  - Top Toast Banner (`Qd`) → Auto-dismissing (4s) or tap-to-dismiss notifications (`quest`, `quiz`, `p2p`, `streak`, `level`, `info`).

---

## 5. Complete Screen Inventory, Buttons, Forms & Interactions

### Screen 1: Home Dashboard (`Ad`)
- **Offline / Sync Status Strip:** Displays `100% Offline Mode · 0 KB Data` and storage indicator (`Room SQLite` + non-blocking sync badge).
- **Gamification Hero Card:**
  - Streak counter (`🔥 <streak_count> Day Streak`)
  - Streak freezes pill (`🛡️ <streak_freezes_left> Freezes`)
  - Current level label (`Level 1/2/3`) + total XP (`<total_xp> XP`)
  - Animated progress bar (`0%–100%`), `% Progress`, and `<neededXp> XP to next level` (or `Maximum Rank Attained`)
- **Daily Quests Card (`resetsDaily` at `00:00`):**
  - Lists 3 quests:
    1. `quest_science`: *Complete 1 Science Module* (`+50 XP`)
    2. `quest_quiz_100`: *Score 100% on a Quiz* (`+70 XP`)
    3. `quest_p2p_share`: *Simulate P2P Module Share* (`+100 XP`)
  - Buttons/States per quest: `progress/max_progress` indicator; when complete and unclaimed → **`Claim XP`** button (adds XP, marks claimed, triggers chime + confetti + toast); when claimed → `Claimed` badge.
- **Karnataka Rural Curriculum (KTBS) Feed:**
  - Module count (`4 Modules`)
  - 5 Subject Filter Chips: `All`, `Math`, `Science`, `Kannada`, `Social`
  - Module Cards (filtered by subject): Chapter & Subject, Grade, Size badge (`MB`), Title, 2-line Description, Status (`Vector Module Ready` or `Completed (score/3)`), CTA Button (**`Start Lesson`** / **`Resume`** → opens Screen 2 with selected lesson).

### Screen 2: Lesson Reader & Interactive Model (`qd` — `reader` mode)
- **Top Sub-Navigation:**
  - **`Back to Modules`** button → returns to Home Dashboard
  - Segmented toggle: **`Reader`** (`ಪಾಠ ಓದು`) | **`Quiz (3)`** (`ರಸಪ್ರಶ್ನೆ (೩)`)
- **Lesson Header Card:**
  - Chapter, Grade, File Size (`MB`), Title
  - **`Listen in Kannada 🔊`** / **`Stop Audio ⏹️`** toggle button (speaks `audio_text_kn` in `kn` or `audio_text_en` in `en` via SpeechSynthesis / Android `TextToSpeech`).
- **Interactive Learning Model (`diagram_type`):**
  1. `fractions` (**Interactive Fraction Disc / Roti**): Tap SVG pie slices to toggle shaded status; live `% Filled` pill; live fraction readout (`Numerator ÷ Denom`, Kannada digits in `kn`); 5 denominator selector buttons (`2`, `3`, `4`, `6`, `8` slices).
  2. `photosynthesis` (**Photosynthesis Interactive Model**): Vector plant/sun/roots diagram; status pill (`● Glucose Active` / `▲ Reaction Halted`); 2 toggle buttons (**`Sunlight: ON/OFF`**, **`Water: ON/OFF`**); chemical equation card (`6CO₂ + 6H₂O + Sunlight → C₆H₁₂O₆ + 6O₂`).
  3. `aksharamale` (**Kannada Vowels Interactive Soundboard**): **`Listen`** button (speaks `<letter>, <example_kn>`); 5×3 grid of 15 vowel/yogavaha buttons (`ಅ, ಆ, ಇ, ಈ, ಉ, ಊ, ಋ, ಎ, ಏ, ಐ, ಒ, ಓ, ಔ, ಅಂ, ಅಃ` with transliteration); selected vowel detail card (large glyph, vowel type, example word + English meaning).
  4. `water_cycle` (**Hoysala Stellate Architecture Blueprint** — mapped to `soc_hoysala_ch4`): Vector 16-point star Jagati blueprint of Belur Chennakeshava (`1117 CE`), Sanctum (`ಗರ್ಭಗೃಹ`), compass (`N ↑`), dynamic description bar, 3 layer selector buttons (**`Star Platform`**, **`Pillars`**, **`Frieze Bands`**).
- **Core Concept & Breakdown Card:** Lists `content_sections` (`heading`, `body`).
- **Bottom CTA Button:** **`Ready for 3-Question Mini Quiz`** → switches to Quiz mode.

### Screen 2B: Knowledge Check Quiz (`qd` — `quiz` mode)
- **Active Question View:**
  - Progress header (`Question X of 3`) + `+60 XP Pool`
  - Question prompt
  - 4 answer option buttons (`A`, `B`, `C`, `D`)
  - **`Confirm Choice`** button (disabled until an option is selected; reveals green/red feedback, plays chime/buzz, shows explanation box)
  - **`Next Question`** / **`View Quiz Results`** button (shown after confirming)
- **Quiz Completed Summary View:**
  - Trophy header + score message (`Flawless 100% Score! 🌟` if 3/3 else `Great effort! Keep practicing.`)
  - 2 metric tiles: `Correct Answers (X / 3)` and `Total Earned (+X*20 XP)`
  - **`Complete Module & Save`** button → returns to Home Dashboard
  - **`Retake Quiz`** button → resets quiz to Question 1

### Screen 3: Wi-Fi Direct P2P Mesh Sharing (`Jd`)
- **Hero Banner:** `Wi-Fi Direct P2P Mesh Sharing`, subtitle, `Avg Speed: 4.8 MB/s · Air-gapped Direct Transfer`.
- **Step 1 Card (Select Lesson Package):** Selectable list of the 4 KTBS lesson packages (`package_name`, `file_size_mb`).
- **Step 2 Card (Discover Nearby Peer Phones):**
  - **`Scan for Nearby Peer Phones`** / **`Re-scan Nearby Phones`** button
  - Animated radar wave panel while scanning (`Scanning Nearby Spectrum (Wi-Fi Direct)...`)
  - Discovered peer phone cards (`Raju's Phone - Redmi 9`, `Priyanka's Phone - Realme C2`, `Sneha's Phone - Samsung Galaxy M02`, `Shriya's Phone - POCO C31` with distance & battery `%`), selectable.
- **Step 3 Card (Transfer via High-Speed Wi-Fi Direct, visible after peer selected):**
  - **`Transfer via Wi-Fi Direct`** button
  - Progress panel (`0–100%`, `Speed: 4.8 MB/s · Wi-Fi Direct`, `Time: ~3s`)
  - Verified completion panel: `Package Verified: 0KB Data Used`, selectable `SHA-256 Checksum`, `Transfer Complete & Verified!`, `+100 XP Quest 🌟`
  - Rewards `+50 XP`, unlocks `P2P Sharer` badge, sets `quest_p2p_share` progress to `1/1`.

### Screen 4: Hall of Fame — Leaderboard & Badges (`Yd`)
- **Hero Banner:** `Offline Village Classroom Leaderboard`, `Govt High School, Channapatna · Synchronized over local mesh`.
- **Leaderboard Card:** Ranked rows (`Rank & Student`, `Streak`, `Total XP`) merging live user (`Ananda (You)`) with 5 peers (`Ananya Rao` 850 XP, `Raju Kumar` 320 XP, `Sneha C` 290 XP, `Basavaraj Patil` 210 XP, `Kavya Gowda` 180 XP), sorted descending by `total_xp` with 🥇/🥈/🥉 rank badges and `YOU` pill.
- **Achievement Badges & Rewards Hall Card:**
  - Header showing `X / 7 Unlocked`
  - 2-column grid of 7 tappable badge cards (`First Lesson`, `3-Day Streak`, `P2P Sharer`, `P2P Pioneer`, `Math Master`, `7-Day Flame`, `Quiz Wizard`) with lock icon if locked.
- **Badge Detail Modal (tapping any badge):**
  - Close (`X`) button, large icon, badge name, `<rarity> Badge`, description, `✓ Unlocked` / `🔒 Locked` pill.

### Screen 5: Credits / About (`Xd`)
- **Hero Card:** `Nanna Shikshana | ನಮ್ಮ ಶಿಕ್ಷಣ`, tagline, `Don Bosco Institute of Technology, Bangalore`, `An Autonomous Institute Affiliated to VTU, Belagavi`.
- **Problem Statement Card:** Rural Karnataka connectivity & hardware context.
- **Architecture Card:** Description + 2×2 spec grid (`Target Hardware: 2GB–3GB RAM Phones`, `Data Storage: Local SQLite / IndexedDB`, `P2P Distribution: Wi-Fi Direct Mesh`, `Module Weight: < 1.5MB SVG Vectors`).
- **Project Mentor & Guide Card:** `Prof. Sivakumar`, `Department of Computer Science & Engineering, DBIT`.
- **Team Members Card:** 4 student engineers (`01 Raj Ananda`, `02 Sneha Channakeshavaiah`, `03 Shriya K H`, `04 Priyanka H`).

### Overlay Modal: Faculty / Hackathon Demo Helpers (`$d`)
- **Trigger:** Floating `Demo Tools` button.
- **State Readout:** `Last Active Date`, `Streak Count`, `Streak Freezes`, `Total XP`.
- **Action Buttons:**
  - `Test: Consecutive Day (+1 Streak)` (`Diff = 1d`)
  - `Test: Missed 1 Day (Use Freeze 🛡️)` (`Diff = 2d`)
  - `Test: Missed 3 Days (Streak Reset)` (`Diff > 2d`)
  - `+50 XP (Test Level Up)`
  - `Reset Demo Data`

---

## 6. Gamification Mechanics Summary

1. **XP & 3-Tier Level Progression (`$e`):**
   - `0–200 XP`: Level 1 — *Village Scholar* / *ಗ್ರಾಮ ವಿದ್ವಾಂಸ* (`#0EA5E9`)
   - `201–600 XP`: Level 2 — *District Champion* / *ಜಿಲ್ಲಾ ಚಾಂಪಿಯನ್* (`#8B5CF6`)
   - `601+ XP` (bar target `1200`): Level 3 — *State Leader* / *ರಾಜ್ಯ ನಾಯಕ* (`#10B981`)
2. **Daily Streak & Freeze Shields (`et`):**
   - `diff == 1 day`: `+1 streak`; auto-unlocks `3-Day Streak` (`>=3`) and `7-Day Flame` (`>=7`).
   - `diff == 2 days`: consumes 1 freeze shield if `streak_freezes_left > 0` to preserve streak; otherwise resets streak to `1`.
   - `diff > 2 days`: resets streak to `1`.
3. **Daily Quests (3 quests):** Claimable once progress reaches `max_progress` (`1/1`), granting `+50`, `+70`, or `+100 XP`.
4. **Quiz Scoring:** `+20 XP` per correct answer (max `60 XP` per 3-question quiz); stores best score per lesson; unlocks `First Lesson` and `Math Master` (100% on `math_fractions_ch3`).
5. **P2P Sharing Reward:** `+50 XP` on transfer completion, unlocks `P2P Sharer`, completes `quest_p2p_share`.
6. **Badges:** 7 defined badges across `bronze`, `silver`, `gold`, and `platinum` rarities.

---

## 7. Learning Content Catalog (4 KTBS Modules)

| ID | Subject | Grade | Title (EN / KN) | Size (MB) | Package Name | SHA-256 | Sections | Quiz Qs | Interactive Model |
|---|---|---|---|---|---|---|---|---|---|
| `math_fractions_ch3` | `math` | Grade 6 KTBS | Fractions: Parts & Wholes / ಅಪೂರ್ಣಾಂಕಗಳು: ಭಾಗಗಳು ಮತ್ತು ಪೂರ್ಣ | 0.8 | `KTBS_Math_Fractions_G6.pkg.zip` | `9f82a17c...dbb7` | 2 | 3 | Interactive Fraction Disc |
| `sci_photosynthesis_ch5` | `science` | Grade 7 KTBS | Photosynthesis: Plant Food Factory / ದ್ಯುತಿಸಂಶ್ಲೇಷಣೆ: ಸಸ್ಯಗಳ ಆಹಾರ ಕಾರ್ಖಾನೆ | 0.9 | `KTBS_Sci_Photosynthesis_G7.pkg.zip` | `e3b0c442...b855` | 2 | 3 | Photosynthesis Model |
| `kan_aksharamale_ch2` | `kannada` | Grade 5 KTBS | Kannada Aksharamale & Swaragalu / ಕನ್ನಡ ಅಕ್ಷರಮಾಲೆ ಮತ್ತು ಸ್ವರಗಳು | 0.5 | `KTBS_Kannada_Aksharamale_G5.pkg.zip` | `c3ab8ff1...c4f2` | 1 | 3 | Kannada Vowels Soundboard (15 vowels) |
| `soc_hoysala_ch4` | `social` | Grade 7 KTBS | Hoysala Architecture & Heritage / ಕರ್ನಾಟಕದ ಹೊಯ್ಸಳ ಶಿಲ್ಪಕಲೆ ಮತ್ತು ವೈಭವ | 1.1 | `KTBS_Social_Hoysala_G7.pkg.zip` | `7d557cac...d9ae` | 1 | 3 | Hoysala Stellate Architecture Blueprint |

---

## 8. Not Accessible / Clarifications Needed

- **Not accessible:** **None.** Every screen, sub-view, modal, translation string, interactive model, and curriculum module in `https://nanna-shikshana.ai.studio/` was directly inspected from the live deployed bundle.
- **Architectural Confirmations Before Building Backend & Android App:**
  1. **Demo Tools FAB (`Faculty / Hackathon Demo Helpers`):** Should this floating debug drawer be included in debug/all builds on Android so evaluators can test streak freeze/reset and XP level-ups, or hidden behind a debug/flag?
  2. **Wi-Fi Direct P2P Mesh (`Jd`):** On Android, should Step 2 & 3 implement real `WifiP2pManager` peer discovery/socket transfer when hardware peers are available **plus** a built-in simulated peer/loopback mode when running on an emulator or single device (so no button is ever non-functional)?
  3. **Student Identity & Sync Auth:** Since the prototype has no login screen, we plan to auto-provision a local student profile offline in Room (`Ananda` / editable student code) and authenticate lazily with the FastAPI backend via device/student-code JWT registration when online. Please confirm if you want an optional non-blocking "Switch / Link Student Code" action or keep launch 100% zero-friction like the prototype.
