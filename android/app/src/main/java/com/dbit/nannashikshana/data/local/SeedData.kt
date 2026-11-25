package com.dbit.nannashikshana.data.local

import com.dbit.nannashikshana.data.local.entity.BadgeEntity
import com.dbit.nannashikshana.data.local.entity.DailyQuestEntity
import com.dbit.nannashikshana.data.local.entity.LeaderboardPeerEntity
import com.dbit.nannashikshana.data.local.entity.LessonPackEntity
import com.dbit.nannashikshana.data.local.entity.LessonProgressEntity
import com.dbit.nannashikshana.data.local.entity.StudentProfileEntity
import com.dbit.nannashikshana.domain.gamification.GamificationEngine
import com.dbit.nannashikshana.domain.model.LessonSection
import com.dbit.nannashikshana.domain.model.PeerDevice
import com.dbit.nannashikshana.domain.model.QuizQuestion
import com.google.gson.Gson

object SeedData {
    private val gson = Gson()

    fun defaultProfile(): StudentProfileEntity = StudentProfileEntity(
        id = "user_1",
        studentCode = "STU_ANANDA_01",
        name = "Ananda",
        village = "Channapatna Rural",
        school = "Govt High School, Channapatna",
        totalXp = 450,
        currentLevel = "Level 2: District Champion",
        streakCount = 5,
        streakFreezesLeft = 2,
        lastActiveDate = GamificationEngine.daysAgoIso(1)
    )

    fun defaultProgress(): List<LessonProgressEntity> = listOf(
        LessonProgressEntity(
            lessonId = "math_fractions_ch3",
            completed = true,
            bestQuizScore = 3,
            totalQuestions = 3,
            updatedAtMs = System.currentTimeMillis()
        )
    )

    fun defaultQuests(): List<DailyQuestEntity> = listOf(
        DailyQuestEntity(
            id = "quest_science",
            titleEn = "Complete 1 Science Module",
            titleKn = "೧ ವಿಜ್ಞಾನ ಮಾಡ್ಯೂಲ್ ಪೂರ್ಣಗೊಳಿಸಿ",
            subEn = "Explore photosynthesis or water cycle",
            subKn = "ದ್ಯುತಿಸಂಶ್ಲೇಷಣೆ ಅಥವಾ ಜಲಚಕ್ರ ಓದಿ",
            xpReward = 50,
            targetType = "complete_science",
            progress = 0,
            maxProgress = 1,
            claimed = false,
            sortOrder = 1
        ),
        DailyQuestEntity(
            id = "quest_quiz_100",
            titleEn = "Score 100% on a Quiz",
            titleKn = "ರಸಪ್ರಶ್ನೆಯಲ್ಲಿ ೧೦೦% ಅಂಕ ಗಳಿಸಿ",
            subEn = "Answer all 3 questions correctly in any subject",
            subKn = "ಯಾವುದೇ ವಿಷಯದಲ್ಲಿ ಎಲ್ಲಾ ೩ ಪ್ರಶ್ನೆಗಳಿಗೆ ಸರಿಯುತ್ತರ ನೀಡಿ",
            xpReward = 70,
            targetType = "quiz_100",
            progress = 0,
            maxProgress = 1,
            claimed = false,
            sortOrder = 2
        ),
        DailyQuestEntity(
            id = "quest_p2p_share",
            titleEn = "Simulate P2P Module Share",
            titleKn = "P2P ಮಾಡ್ಯೂಲ್ ಹಂಚಿಕೆಯನ್ನು ಪೂರ್ಣಗೊಳಿಸಿ",
            subEn = "Transmit 1 module over Wi-Fi Direct mesh",
            subKn = "ವೈ-ಫೈ ಡೈರೆಕ್ಟ್ ಮೂಲಕ ೧ ಪಾಠವನ್ನು ವರ್ಗಾಯಿಸಿ",
            xpReward = 100,
            targetType = "p2p_share",
            progress = 0,
            maxProgress = 1,
            claimed = false,
            sortOrder = 3
        )
    )

    fun defaultBadges(): List<BadgeEntity> = listOf(
        BadgeEntity(
            id = "First Lesson",
            nameEn = "First Step Scholar",
            nameKn = "ಮೊದಲ ಹೆಜ್ಜೆ ವಿದ್ವಾಂಸ",
            descriptionEn = "Completed your very first offline curriculum module.",
            descriptionKn = "ನಿಮ್ಮ ಮೊದಲ ಆಫ್‌ಲೈನ್ ಪಠ್ಯಕ್ರಮ ಮಾಡ್ಯೂಲ್ ಪೂರ್ಣಗೊಳಿಸಿದ್ದೀರಿ.",
            icon = "BookOpen",
            rarity = "bronze",
            unlocked = true,
            sortOrder = 1
        ),
        BadgeEntity(
            id = "3-Day Streak",
            nameEn = "3-Day Flame",
            nameKn = "೩ ದಿನಗಳ ನಿರಂತರ ಅಧ್ಯಯನ",
            descriptionEn = "Maintained a 3-day continuous offline study streak.",
            descriptionKn = "ಸತತ ೩ ದಿನಗಳ ಕಾಲ ನಿರಂತರವಾಗಿ ಕಲಿಕೆ ಮುಂದುವರಿಸಿದ್ದೀರಿ.",
            icon = "Flame",
            rarity = "bronze",
            unlocked = true,
            sortOrder = 2
        ),
        BadgeEntity(
            id = "P2P Sharer",
            nameEn = "P2P Mesh Sharer",
            nameKn = "P2P ಮೆಶ್ ಹಂಚಿಕೆದಾರ",
            descriptionEn = "Shared a curriculum package with a peer phone via Wi-Fi Direct with zero data.",
            descriptionKn = "ಇಂಟರ್ನೆಟ್ ಇಲ್ಲದೆ ವೈ-ಫೈ ಡೈರೆಕ್ಟ್ ಮೂಲಕ ಸಹಪಾಠಿಗೆ ಪಾಠ ಹಂಚಿದ್ದೀರಿ.",
            icon = "Radio",
            rarity = "silver",
            unlocked = true,
            sortOrder = 3
        ),
        BadgeEntity(
            id = "P2P Pioneer",
            nameEn = "Village Mesh Pioneer",
            nameKn = "ಗ್ರಾಮ ಮೆಶ್ ಪ್ರವರ್ತಕ",
            descriptionEn = "Distributed 3 or more modules to classmates in the village network.",
            descriptionKn = "ಗ್ರಾಮದ ಜಾಲದಲ್ಲಿ ೩ ಕ್ಕೂ ಹೆಚ್ಚು ಪಾಠಗಳನ್ನು ಯಶಸ್ವಿಯಾಗಿ ವಿತರಿಸಿದ್ದೀರಿ.",
            icon = "Share2",
            rarity = "gold",
            unlocked = false,
            sortOrder = 4
        ),
        BadgeEntity(
            id = "Math Master",
            nameEn = "Ganitha Visharada",
            nameKn = "ಗಣಿತ ವಿಶಾರದ",
            descriptionEn = "Scored 100% on the KTBS Fractions knowledge check quiz.",
            descriptionKn = "ಅಪೂರ್ಣಾಂಕಗಳ ರಸಪ್ರಶ್ನೆಯಲ್ಲಿ ಪೂರ್ಣ ಅಂಕ ಗಳಿಸಿದ್ದೀರಿ.",
            icon = "Divide",
            rarity = "gold",
            unlocked = false,
            sortOrder = 5
        ),
        BadgeEntity(
            id = "7-Day Flame",
            nameEn = "7-Day Iron Streak",
            nameKn = "೭ ದಿನಗಳ ಅಗ್ನಿ ಸ್ಟ್ರೀಕ್",
            descriptionEn = "Kept the learning fire burning strong for a full week without breaking.",
            descriptionKn = "ಪೂರ್ಣ ಒಂದು ವಾರ ಯಾವುದೇ ದಿನ ತಪ್ಪದೇ ಅಧ್ಯಯನ ನಡೆಸಿದ್ದೀರಿ.",
            icon = "Zap",
            rarity = "gold",
            unlocked = false,
            sortOrder = 6
        ),
        BadgeEntity(
            id = "Quiz Wizard",
            nameEn = "Prashna Ratna",
            nameKn = "ಪ್ರಶ್ನ ರತ್ನ",
            descriptionEn = "Answered over 10 quiz questions correctly on the first attempt.",
            descriptionKn = "ಹತ್ತಕ್ಕೂ ಹೆಚ್ಚು ರಸಪ್ರಶ್ನೆಗಳಿಗೆ ಮೊದಲ ಪ್ರಯತ್ನದಲ್ಲೇ ಸರಿಯುತ್ತರ ನೀಡಿದ್ದೀರಿ.",
            icon = "Award",
            rarity = "platinum",
            unlocked = false,
            sortOrder = 7
        )
    )

    fun defaultLeaderboardPeers(): List<LeaderboardPeerEntity> = listOf(
        LeaderboardPeerEntity("lead_1", "Ananya Rao", "Channapatna", "Govt Model Primary School", 850, 9),
        LeaderboardPeerEntity("lead_3", "Raju Kumar", "Ramanagara", "Rural Composite PU College", 320, 4),
        LeaderboardPeerEntity("lead_4", "Sneha C", "Kanakapura", "Govt Girls High School", 290, 3),
        LeaderboardPeerEntity("lead_5", "Basavaraj Patil", "Magadi", "Taluk Model High School", 210, 2),
        LeaderboardPeerEntity("lead_6", "Kavya Gowda", "Maddur", "Sri Chennakeshava Vidyalaya", 180, 1)
    )

    fun demoPeerDevices(): List<PeerDevice> = listOf(
        PeerDevice("peer_1", "Raju's Phone", "Redmi 9 (3GB RAM)", "Raju Kumar", 4, "2.4 m", 68),
        PeerDevice("peer_2", "Priyanka's Phone", "Realme C2 (2GB RAM)", "Priyanka H", 3, "4.1 m", 42),
        PeerDevice("peer_3", "Sneha's Phone", "Samsung Galaxy M02 (2GB RAM)", "Sneha C", 4, "1.8 m", 85),
        PeerDevice("peer_4", "Shriya's Phone", "POCO C31 (3GB RAM)", "Shriya K H", 2, "6.5 m", 54)
    )

    fun defaultLessons(): List<LessonPackEntity> = listOf(
        LessonPackEntity(
            id = "math_fractions_ch3",
            packCode = "pack_math_fractions_ch3",
            subject = "math",
            grade = "Grade 6 KTBS",
            version = 1,
            sortOrder = 1,
            fileSizeMb = 0.8,
            packageName = "KTBS_Math_Fractions_G6.pkg.zip",
            sha256 = "9f82a17cb583e4091a104f29d9365e8841a02b1c8537b0df73e21190ca38dbb7",
            isDownloaded = true,
            localArchivePath = null,
            titleEn = "Fractions: Parts & Wholes",
            titleKn = "ಅಪೂರ್ಣಾಂಕಗಳು: ಭಾಗಗಳು ಮತ್ತು ಪೂರ್ಣ",
            chapterEn = "Chapter 3 · Mathematics",
            chapterKn = "ಅಧ್ಯಾಯ ೩ · ಗಣಿತ",
            descriptionEn = "Master fractional slices, numerators, and denominators through visual fraction discs and real-world roti cutting.",
            descriptionKn = "ರೋಟಿ ಮತ್ತು ಚಕ್ರದ ತುಂಡುಗಳ ಮೂಲಕ ಅಂಶ ಮತ್ತು ಛೇದಗಳ ಸಂಕಲ್ಪನೆಯನ್ನು ದೃಶ್ಯರೂಪದಲ್ಲಿ ಸುಲಭವಾಗಿ ಕಲಿಯಿರಿ.",
            diagramType = "fractions",
            audioTextEn = "Bravo! A fraction represents equal parts of a whole object. The top number is the numerator, and the bottom is the denominator.",
            audioTextKn = "ಶಭಾಷ್! ಅಪೂರ್ಣಾಂಕ ಎಂದರೆ ಒಂದು ಪೂರ್ಣ ವಸ್ತುವಿನ ಸಮಾನ ಭಾಗಗಳು. ಮೇಲಿನ ಸಂಖ್ಯೆ ಅಂಶ, ಕೆಳಗಿನ ಸಂಖ್ಯೆ ಛೇದ.",
            sectionsJson = gson.toJson(
                listOf(
                    LessonSection(
                        "1. What is a Fraction?",
                        "೧. ಅಪೂರ್ಣಾಂಕ ಎಂದರೇನು?",
                        "When a whole roti or circle is divided into equal slices, each slice is a fraction. For example, 1/2 means one part out of two equal halves.",
                        "ಒಂದು ಪೂರ್ಣ ರೊಟ್ಟಿ ಅಥವಾ ವೃತ್ತವನ್ನು ಸಮಾನ ಭಾಗಗಳಾಗಿ ಹಂಚಿದಾಗ, ಪ್ರತಿಯೊಂದು ಭಾಗವನ್ನೂ ಅಪೂರ್ಣಾಂಕ ಎನ್ನುತ್ತೇವೆ. ಉದಾಹರಣೆಗೆ ೧/೨ ಎಂದರೆ ಎರಡು ಸಮಭಾಗಗಳಲ್ಲಿ ಒಂದು ಭಾಗ."
                    ),
                    LessonSection(
                        "2. Numerator vs Denominator",
                        "೨. ಅಂಶ ಮತ್ತು ಛೇದ",
                        "The Denominator (bottom) tells how many equal parts make the whole. The Numerator (top) counts how many parts we are considering.",
                        "ಛೇದವು (ಕೆಳಗಿನ ಸಂಖ್ಯೆ) ಒಟ್ಟು ಸಮಭಾಗಗಳ ಸಂಖ್ಯೆಯನ್ನು ಸೂಚಿಸುತ್ತದೆ. ಅಂಶವು (ಮೇಲಿನ ಸಂಖ್ಯೆ) ನಾವು ಆಯ್ದುಕೊಂಡ ಭಾಗಗಳನ್ನು ತಿಳಿಸುತ್ತದೆ."
                    )
                )
            ),
            quizJson = gson.toJson(
                listOf(
                    QuizQuestion(
                        "q_frac_1",
                        "If a circle is divided into 4 equal slices and 3 are shaded, what fraction is shaded?",
                        "ಒಂದು ವೃತ್ತವನ್ನು ೪ ಸಮಭಾಗಗಳನ್ನಾಗಿ ಮಾಡಿ ೩ ಭಾಗಗಳಿಗೆ ಬಣ್ಣ ಹಚ್ಚಿದರೆ, ಆ ಭಾಗದ ಅಪೂರ್ಣಾಂಕ ಎಷ್ಟು?",
                        listOf("1/4", "3/4", "4/3", "2/4"),
                        listOf("೧/೪", "೩/೪", "೪/೩", "೨/೪"),
                        1,
                        "3 parts are taken out of 4 total equal slices, so the fraction is 3/4.",
                        "ಒಟ್ಟು ೪ ಸಮಭಾಗಗಳಲ್ಲಿ ೩ ಭಾಗಗಳನ್ನು ಆರಿಸಿಕೊಳ್ಳಲಾಗಿದೆ, ಆದ್ದರಿಂದ ೩/೪."
                    ),
                    QuizQuestion(
                        "q_frac_2",
                        "In the fraction 2/5, what is the Denominator (ಛೇದ)?",
                        "೨/೫ ಅಪೂರ್ಣಾಂಕದಲ್ಲಿ ಛೇದ ಯಾವುದು?",
                        listOf("2", "5", "7", "10"),
                        listOf("೨", "೫", "೭", "೧೦"),
                        1,
                        "The bottom number is the denominator, which is 5.",
                        "ಅಪೂರ್ಣಾಂಕದ ಕೆಳಗಿನ ಸಂಖ್ಯೆ ಛೇದ, ಅಂದರೆ ೫."
                    ),
                    QuizQuestion(
                        "q_frac_3",
                        "Which fraction represents exactly one half of a whole?",
                        "ಯಾವ ಅಪೂರ್ಣಾಂಕವು ನಿಖರವಾಗಿ ಅರ್ಧ ಭಾಗವನ್ನು ಸೂಚಿಸುತ್ತದೆ?",
                        listOf("2/4", "1/3", "3/5", "1/4"),
                        listOf("೨/೪", "೧/೩", "೩/೫", "೧/೪"),
                        0,
                        "2/4 simplifies directly to 1/2 (half).",
                        "೨/೪ ಅನ್ನು ಸರಳೀಕರಿಸಿದರೆ ೧/೨ (ಅರ್ಧ) ಆಗುತ್ತದೆ."
                    )
                )
            )
        ),
        LessonPackEntity(
            id = "sci_photosynthesis_ch5",
            packCode = "pack_sci_photosynthesis_ch5",
            subject = "science",
            grade = "Grade 7 KTBS",
            version = 1,
            sortOrder = 2,
            fileSizeMb = 0.9,
            packageName = "KTBS_Sci_Photosynthesis_G7.pkg.zip",
            sha256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
            isDownloaded = true,
            localArchivePath = null,
            titleEn = "Photosynthesis: Plant Food Factory",
            titleKn = "ದ್ಯುತಿಸಂಶ್ಲೇಷಣೆ: ಸಸ್ಯಗಳ ಆಹಾರ ಕಾರ್ಖಾನೆ",
            chapterEn = "Chapter 5 · Science",
            chapterKn = "ಅಧ್ಯಾಯ ೫ · ವಿಜ್ಞಾನ",
            descriptionEn = "Discover how green leaves use solar energy, chlorophyll, carbon dioxide, and root water to produce glucose and release life-giving oxygen.",
            descriptionKn = "ಕ್ಲೋರೋಫಿಲ್, ಸೂರ್ಯನ ಬೆಳಕು, ನೀರಿನಂಶ ಮತ್ತು ಇಂಗಾಲದ ಡೈಆಕ್ಸೈಡ್ ಬಳಸಿ ಗಿಡಗಳು ಆಹಾರ ತಯಾರಿಸುವ ಅದ್ಭುತ ಪ್ರಕ್ರಿಯೆ ತಿಳಿಯಿರಿ.",
            diagramType = "photosynthesis",
            audioTextEn = "In photosynthesis, green leaves use sunlight and water to produce glucose and release oxygen for all living beings.",
            audioTextKn = "ದ್ಯುತಿಸಂಶ್ಲೇಷಣೆಯಲ್ಲಿ ಹಸಿರು ಎಲೆಗಳು ಸೂರ್ಯನ ಬೆಳಕು ಮತ್ತು ನೀರಿನ ಸಹಾಯದಿಂದ ಗ್ಲುಕೋಸ್ ತಯಾರಿಸಿ ನಮಗೆ ಆಮ್ಲಜನಕವನ್ನು ನೀಡುತ್ತವೆ.",
            sectionsJson = gson.toJson(
                listOf(
                    LessonSection(
                        "1. The Solar Recipe",
                        "೧. ಸಸ್ಯಗಳ ಆಹಾರ ತಯಾರಿಕೆ",
                        "Chlorophyll in the chloroplasts absorbs sunlight. Roots absorb water (H2O), while stomata beneath the leaf absorb Carbon Dioxide (CO2).",
                        "ಎಲೆಗಳಲ್ಲಿನ ಪತ್ರಹರಿತ್ತು (Chlorophyll) ಸೂರ್ಯನ ಬೆಳಕನ್ನು ಹೀರುತ್ತದೆ. ಬೇರುಗಳು ನೀರನ್ನು ಹೀರಿ, ಎಲೆಯ ರಂಧ್ರಗಳು ಗಾಳಿಯಿಂದ ಇಂಗಾಲದ ಡೈಆಕ್ಸೈಡ್ ಪಡೆಯುತ್ತವೆ."
                    ),
                    LessonSection(
                        "2. Output: Glucose & Oxygen",
                        "೨. ಫಲಿತಾಂಶ: ಗ್ಲುಕೋಸ್ ಮತ್ತು ಆಮ್ಲಜನಕ",
                        "The chemical reaction converts these raw ingredients into Glucose (stored energy for the plant) and releases Oxygen (O2) into the atmosphere.",
                        "ಈ ರಾಸಾಯನಿಕ ಪ್ರಕ್ರಿಯೆಯಿಂದ ಸಸ್ಯಕ್ಕೆ ಗ್ಲುಕೋಸ್ ಸಿಗುತ್ತದೆ ಮತ್ತು ವಾತಾವರಣಕ್ಕೆ ಶುದ್ಧ ಆಮ್ಲಜನಕ (O2) ಬಿಡುಗಡೆಯಾಗುತ್ತದೆ."
                    )
                )
            ),
            quizJson = gson.toJson(
                listOf(
                    QuizQuestion(
                        "q_sci_1",
                        "Which green pigment in leaves absorbs solar energy for photosynthesis?",
                        "ದ್ಯುತಿಸಂಶ್ಲೇಷಣೆಗೆ ಸೂರ್ಯನ ಬೆಳಕನ್ನು ಹೀರಿಕೊಳ್ಳುವ ಎಲೆಗಳ ಹಸಿರು ವರ್ಣದ್ರವ್ಯ ಯಾವುದು?",
                        listOf("Chlorophyll", "Hemoglobin", "Keratin", "Melanin"),
                        listOf("ಕ್ಲೋರೋಫಿಲ್ (ಪತ್ರಹರಿತ್ತು)", "ಹಿಮೋಗ್ಲೋಬಿನ್", "ಕೆರಾಟಿನ್", "ಮೆಲನಿನ್"),
                        0,
                        "Chlorophyll traps sunlight inside the plant chloroplast cells.",
                        "ಕ್ಲೋರೋಫಿಲ್ ಸೂರ್ಯನ ಬೆಳಕಿನ ಶಕ್ತಿಯನ್ನು ಎಲೆಗಳಲ್ಲಿ ಹೀರಿಕೊಳ್ಳುತ್ತದೆ."
                    ),
                    QuizQuestion(
                        "q_sci_2",
                        "What life-giving gas is released by plants into the atmosphere during photosynthesis?",
                        "ದ್ಯುತಿಸಂಶ್ಲೇಷಣೆಯ ಸಮಯದಲ್ಲಿ ಸಸ್ಯಗಳು ವಾತಾವರಣಕ್ಕೆ ಬಿಡುಗಡೆ ಮಾಡುವ ಜೀವವಾಯು ಯಾವುದು?",
                        listOf("Carbon Monoxide", "Oxygen", "Nitrogen", "Methane"),
                        listOf("ಕಾರ್ಬನ್ ಮೊನಾಕ್ಸೈಡ್", "ಆಮ್ಲಜನಕ (Oxygen)", "ಸಾರಜನಕ", "ಮೀಥೇನ್"),
                        1,
                        "Oxygen (O2) is produced as a byproduct of water photolysis.",
                        "ಸಸ್ಯಗಳು ನಮಗೆ ಉಸಿರಾಡಲು ಅಗತ್ಯವಾದ ಆಮ್ಲಜನಕವನ್ನು ಹೊರಸೂಸುತ್ತವೆ."
                    ),
                    QuizQuestion(
                        "q_sci_3",
                        "From where do plants primarily absorb water (H2O)?",
                        "ಸಸ್ಯಗಳು ನೀರನ್ನು ಮುಖ್ಯವಾಗಿ ಎಲ್ಲಿಂದ ಹೀರಿಕೊಳ್ಳುತ್ತವೆ?",
                        listOf("Flowers", "Roots from Soil", "Fruit Skin", "Bark"),
                        listOf("ಹೂವುಗಳು", "ಮಣ್ಣಿನಿಂದ ಬೇರುಗಳು", "ಹಣ್ಣಿನ ಸಿಪ್ಪೆ", "ತೊಗಟೆ"),
                        1,
                        "Root hairs absorb water and minerals from moist soil through xylem vessels.",
                        "ಬೇರುಗಳು ಮಣ್ಣಿನಲ್ಲಿರುವ ತೇವಾಂಶ ಮತ್ತು ನೀರನ್ನು ಜೈಲಮ್ ನಾಳಗಳ ಮೂಲಕ ಎಲೆಗಳಿಗೆ ಕಳುಹಿಸುತ್ತವೆ."
                    )
                )
            )
        ),
        LessonPackEntity(
            id = "kan_aksharamale_ch2",
            packCode = "pack_kan_aksharamale_ch2",
            subject = "kannada",
            grade = "Grade 5 KTBS",
            version = 1,
            sortOrder = 3,
            fileSizeMb = 0.5,
            packageName = "KTBS_Kannada_Aksharamale_G5.pkg.zip",
            sha256 = "c3ab8ff13720e8ad9047dd39466b3c8974e592c2fa383d4a3960714caef0c4f2",
            isDownloaded = true,
            localArchivePath = null,
            titleEn = "Kannada Aksharamale & Swaragalu",
            titleKn = "ಕನ್ನಡ ಅಕ್ಷರಮಾಲೆ ಮತ್ತು ಸ್ವರಗಳು",
            chapterEn = "Chapter 2 · Kannada",
            chapterKn = "ಅಧ್ಯಾಯ ೨ · ಕನ್ನಡ",
            descriptionEn = "Explore the phonetic harmony of Kannada vowels (ಸ್ವರಗಳು), short and long vowel distinctions, and native proverbs.",
            descriptionKn = "ಹ್ರಸ್ವಸ್ವರ, ದೀರ್ಘಸ್ವರ ಮತ್ತು ಪ್ಲುತಸ್ವರಗಳ ಉಚ್ಚಾರಣಾ ನಿಯಮಗಳನ್ನು ಮತ್ತು ಪ್ರಮುಖ ಗಾದೆಗಳನ್ನು ಕಲಿಯಿರಿ.",
            diagramType = "aksharamale",
            audioTextEn = "In the Kannada alphabet there are 49 letters in total, including 14 vowels.",
            audioTextKn = "ಕನ್ನಡ ಅಕ್ಷರಮಾಲೆಯಲ್ಲಿ ಒಟ್ಟು ನಲವತ್ತೊಂಬತ್ತು ಅಕ್ಷರಗಳಿವೆ. ಅದರಲ್ಲಿ ಹದಿನಾಲ್ಕು ಸ್ವರಗಳು ಸೇರಿವೆ.",
            sectionsJson = gson.toJson(
                listOf(
                    LessonSection(
                        "1. Kannada Vowels (ಸ್ವರಗಳು)",
                        "೧. ಕನ್ನಡದ ಸ್ವರಗಳು",
                        "Vowels are independent sounds produced without obstruction. They include Hrasva (short sounds like ಅ, ಇ, ಉ) and Deergha (long sounds like ಆ, ಈ, ಊ).",
                        "ಸ್ವತಂತ್ರವಾಗಿ ಉಚ್ಚರಿಸಲಾಗುವ ಅಕ್ಷರಗಳೇ ಸ್ವರಗಳು. ಇವುಗಳಲ್ಲಿ ಹ್ರಸ್ವಸ್ವರಗಳು (ಅ, ಇ, ಉ, ಋ, ಎ, ಒ) ಮತ್ತು ದೀರ್ಘಸ್ವರಗಳು (ಆ, ಈ, ಊ, ಏ, ಐ, ಓ, ಔ) ಇವೆ."
                    )
                )
            ),
            quizJson = gson.toJson(
                listOf(
                    QuizQuestion(
                        "q_kan_1",
                        "How many vowels (ಸ್ವರಗಳು) are there in the standard Kannada Aksharamale?",
                        "ಕನ್ನಡ ಅಕ್ಷರಮಾಲೆಯಲ್ಲಿ ಒಟ್ಟು ಎಷ್ಟು ಸ್ವರಗಳಿವೆ?",
                        listOf("10", "12", "14", "25"),
                        listOf("೧೦", "೧೨", "೧೪", "೨೫"),
                        2,
                        "There are 14 vowels in traditional Kannada script.",
                        "ಸಾಂಪ್ರದಾಯಿಕ ಕನ್ನಡ ಅಕ್ಷರಮಾಲೆಯಲ್ಲಿ ೧೪ ಸ್ವರಗಳಿವೆ."
                    ),
                    QuizQuestion(
                        "q_kan_2",
                        "Which of the following is a short vowel (ಹ್ರಸ್ವಸ್ವರ)?",
                        "ಕೆಳಗಿನವುಗಳಲ್ಲಿ ಯಾವುದು ಹ್ರಸ್ವಸ್ವರ?",
                        listOf("ಅ", "ಆ", "ಈ", "ಊ"),
                        listOf("ಅ", "ಆ", "ಈ", "ಊ"),
                        0,
                        "\"ಅ\" requires just one matra of time to pronounce, making it Hrasva.",
                        "\"ಅ\" ಒಂದು ಮಾತ್ರಾ ಕಾಲದಲ್ಲಿ ಉಚ್ಚರಿಸಲ್ಪಡುವ ಹ್ರಸ್ವಸ್ವರವಾಗಿದೆ."
                    ),
                    QuizQuestion(
                        "q_kan_3",
                        "Complete the proverb: \"ಕೈ ಕೆಸರಾದರೆ... (If your hand is muddied...)\"",
                        "\"ಕೈ ಕೆಸರಾದರೆ...\" ಎಂಬ ಗಾದೆಯ ಮುಂದಿನ ಭಾಗ ಯಾವುದು?",
                        listOf("ಬಾಯಿ ಮೊಸರು", "ಮೈ ಕೆಸರು", "ಕಣ್ಣು ತಣ್ಣಗೆ", "ಮನಸ್ಸು ಶುದ್ಧ"),
                        listOf("ಬಾಯಿ ಮೊಸರು", "ಮೈ ಕೆಸರು", "ಕಣ್ಣು ತಣ್ಣಗೆ", "ಮನಸ್ಸು ಶುದ್ಧ"),
                        0,
                        "\"ಕೈ ಕೆಸರಾದರೆ ಬಾಯಿ ಮೊಸರು\" means hard labor in the fields yields sweet nourishment.",
                        "ಪ್ರಾಮಾಣಿಕ ಕಠಿಣ ದುಡಿಮೆಯಿಂದ ಸಮೃದ್ಧಿ ಲಭಿಸುತ್ತದೆ ಎಂಬುದು ಇದರ ಅರ್ಥ."
                    )
                )
            )
        ),
        LessonPackEntity(
            id = "soc_hoysala_ch4",
            packCode = "pack_soc_hoysala_ch4",
            subject = "social",
            grade = "Grade 7 KTBS",
            version = 1,
            sortOrder = 4,
            fileSizeMb = 1.1,
            packageName = "KTBS_Social_Hoysala_G7.pkg.zip",
            sha256 = "7d557cac701b39c943641772658828459462619a847d43a3ecdced3072ebd9ae",
            isDownloaded = true,
            localArchivePath = null,
            titleEn = "Hoysala Architecture & Heritage",
            titleKn = "ಕರ್ನಾಟಕದ ಹೊಯ್ಸಳ ಶಿಲ್ಪಕಲೆ ಮತ್ತು ವೈಭವ",
            chapterEn = "Chapter 4 · Social Studies",
            chapterKn = "ಅಧ್ಯಾಯ ೪ · ಸಮಾಜ ವಿಜ್ಞಾನ",
            descriptionEn = "Examine the intricate soapstone carvings, stellate (star-shaped) platforms, and lathe-turned pillars of Belur & Halebidu.",
            descriptionKn = "ಬೇಲೂರು ಮತ್ತು ಹಳೇಬೀಡಿನ ನಕ್ಷತ್ರಾಕಾರದ ಜಗತಿಗಳು, ಸಾಬೂನು ಕಲ್ಲಿನ ಸೂಕ್ಷ್ಮ ಕೆತ್ತನೆಗಳು ಮತ್ತು ಲೇತ್-ಕಂಬಗಳ ಇತಿಹಾಸ ತಿಳಿಯಿರಿ.",
            diagramType = "water_cycle",
            audioTextEn = "The Chennakeshava Temple at Belur, built by Hoysala King Vishnuvardhana, is a crowning jewel of Indian architecture.",
            audioTextKn = "ಹೊಯ್ಸಳ ರಾಜ ವಿಷ್ಣುವರ್ಧನನು ಕಟ್ಟಿಸಿದ ಬೇಲೂರಿನ ಚೆನ್ನಕೇಶವ ದೇವಾಲಯವು ಭಾರತೀಯ ವಾಸ್ತುಶಿಲ್ಪದ ಹೆಗ್ಗುರುತಾಗಿದೆ.",
            sectionsJson = gson.toJson(
                listOf(
                    LessonSection(
                        "1. Star-Shaped Platforms (Stellate Jagati)",
                        "೧. ನಕ್ಷತ್ರಾಕಾರದ ಜಗತಿ",
                        "Hoysala temples are built on raised star-shaped platforms called Jagati, providing a grand circumambulation path.",
                        "ಹೊಯ್ಸಳ ದೇವಾಲಯಗಳನ್ನು ನಕ್ಷತ್ರಾಕಾರದ ಎತ್ತರವಾದ ಜಗತಿಯ ಮೇಲೆ ನಿರ್ಮಿಸಲಾಗಿದೆ. ಇದು ಪ್ರದಕ್ಷಿಣೆಗೆ ಅವಕಾಶ ನೀಡುತ್ತದೆ."
                    )
                )
            ),
            quizJson = gson.toJson(
                listOf(
                    QuizQuestion(
                        "q_soc_1",
                        "Which soft stone was primarily carved with micro-details in Hoysala temples?",
                        "ಹೊಯ್ಸಳ ದೇವಾಲಯಗಳ ಸೂಕ್ಷ್ಮ ಕೆತ್ತನೆಗೆ ಯಾವ ಮೃದು ಶಿಲೆಯನ್ನು ಬಳಸಲಾಗಿದೆ?",
                        listOf("Soapstone (Chloritic Schist)", "Sandstone", "Basalt", "Red Brick"),
                        listOf("ಸಾಬೂನು ಕಲ್ಲು (ಸೋಪ್‌ಸ್ಟೋನ್)", "ಮರಳುಗಲ್ಲು", "ಬಸಾಲ್ಟ್", "ಕೆಂಪು ಇಟ್ಟಿಗೆ"),
                        0,
                        "Soapstone is soft when quarried and hardens upon exposure to air.",
                        "ಸಾಬೂನು ಕಲ್ಲು ಆರಂಭದಲ್ಲಿ ಮೃದುವಾಗಿದ್ದು, ನಂತರ ಗಟ್ಟಿಯಾಗುತ್ತದೆ."
                    ),
                    QuizQuestion(
                        "q_soc_2",
                        "In which Karnataka district is the world-famous Belur Chennakeshava Temple located?",
                        "ಪ್ರಸಿದ್ಧ ಬೇಲೂರು ಚೆನ್ನಕೇಶವ ದೇವಾಲಯವು ಕರ್ನಾಟಕದ ಯಾವ ಜಿಲ್ಲೆಯಲ್ಲಿದೆ?",
                        listOf("Hassan", "Mysuru", "Ballari", "Belagavi"),
                        listOf("ಹಾಸನ", "ಮೈಸೂರು", "ಬಳ್ಳಾರಿ", "ಬೆಳಗಾವಿ"),
                        0,
                        "Belur is in Hassan district of Karnataka.",
                        "ಬೇಲೂರು ಕರ್ನಾಟಕದ ಹಾಸನ ಜಿಲ್ಲೆಯಲ್ಲಿದೆ."
                    ),
                    QuizQuestion(
                        "q_soc_3",
                        "What unique platform shape is characteristic of Hoysala temples?",
                        "ಹೊಯ್ಸಳ ದೇವಾಲಯಗಳ ಅಡಿಪಾಯದ (ಜಗತಿ) ವಿಶೇಷ ಆಕಾರ ಯಾವುದು?",
                        listOf("Star-shaped (Stellate)", "Hexagonal", "Octagonal", "Circular"),
                        listOf("ನಕ್ಷತ್ರಾಕಾರ", "ಷಡ್ಭುಜಾಕಾರ", "ಅಷ್ಟಭುಜಾಕಾರ", "ವೃತ್ತಾಕಾರ"),
                        0,
                        "The stellate star shape maximized exterior surface area for sculptures.",
                        "ನಕ್ಷತ್ರಾಕಾರದ ರಚನೆಯು ಗೋಡೆಗಳ ಮೇಲೆ ಹೆಚ್ಚಿನ ಶಿಲ್ಪಗಳನ್ನು ಕೆತ್ತಲು ನೆರವಾಗುತ್ತದೆ."
                    )
                )
            )
        )
    )
}
