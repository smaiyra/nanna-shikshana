package com.dbit.nannashikshana

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.dbit.nannashikshana.data.local.SeedData
import com.dbit.nannashikshana.domain.model.AppLanguage
import com.dbit.nannashikshana.domain.model.BadgeItem
import com.dbit.nannashikshana.domain.model.DailyQuest
import com.dbit.nannashikshana.domain.model.LeaderboardEntry
import com.dbit.nannashikshana.domain.model.LessonModule
import com.dbit.nannashikshana.domain.model.LessonSection
import com.dbit.nannashikshana.domain.model.QuizQuestion
import com.dbit.nannashikshana.domain.model.StudentProfile
import com.dbit.nannashikshana.domain.model.SyncStatus
import com.dbit.nannashikshana.ui.screens.HallOfFameScreen
import com.dbit.nannashikshana.ui.screens.HomeDashboardScreen
import com.dbit.nannashikshana.ui.theme.NannaShikshanaTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ComposeScreensUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val gson = Gson()

    private fun seededLessons(): List<LessonModule> {
        val secType = object : TypeToken<List<LessonSection>>() {}.type
        val quizType = object : TypeToken<List<QuizQuestion>>() {}.type
        return SeedData.defaultLessons().map { e ->
            LessonModule(
                id = e.id,
                packCode = e.packCode,
                subject = e.subject,
                grade = e.grade,
                version = e.version,
                fileSizeMb = e.fileSizeMb,
                packageName = e.packageName,
                sha256 = e.sha256,
                isDownloaded = e.isDownloaded,
                titleEn = e.titleEn,
                titleKn = e.titleKn,
                chapterEn = e.chapterEn,
                chapterKn = e.chapterKn,
                descriptionEn = e.descriptionEn,
                descriptionKn = e.descriptionKn,
                diagramType = e.diagramType,
                audioTextEn = e.audioTextEn,
                audioTextKn = e.audioTextKn,
                contentSections = gson.fromJson(e.sectionsJson, secType),
                quiz = gson.fromJson(e.quizJson, quizType)
            )
        }
    }

    @Test
    fun homeDashboardRendersStreakQuestsAndSubjectFilterCallback() {
        var selectedFilter = "all"
        val profile = StudentProfile(
            id = "user_1",
            studentCode = "STU_ANANDA_01",
            name = "Ananda",
            village = "Channapatna Rural",
            school = "Govt High School, Channapatna",
            totalXp = 450,
            currentLevel = "Level 2: District Champion",
            streakCount = 5,
            streakFreezesLeft = 2,
            lastActiveDate = "2026-11-24",
            unlockedBadges = listOf("First Lesson"),
            completedLessons = listOf("math_fractions_ch3"),
            completedQuizzes = mapOf("math_fractions_ch3" to 3),
            claimedQuests = emptyList()
        )
        val quests = SeedData.defaultQuests().map {
            DailyQuest(
                id = it.id,
                titleEn = it.titleEn,
                titleKn = it.titleKn,
                subEn = it.subEn,
                subKn = it.subKn,
                xpReward = it.xpReward,
                targetType = it.targetType,
                progress = it.progress,
                maxProgress = it.maxProgress,
                claimed = it.claimed
            )
        }

        composeRule.setContent {
            NannaShikshanaTheme {
                HomeDashboardScreen(
                    profile = profile,
                    lessons = seededLessons(),
                    quests = quests,
                    language = AppLanguage.EN,
                    subjectFilter = "all",
                    syncStatus = SyncStatus(),
                    downloadingPackCode = null,
                    onSelectFilter = { selectedFilter = it },
                    onSelectLesson = {},
                    onClaimQuest = {},
                    onManualSync = {},
                    onDownloadPack = {}
                )
            }
        }

        composeRule.onNodeWithText("450 XP").assertIsDisplayed()
        composeRule.onNodeWithText("Daily Quests").assertIsDisplayed()
        composeRule.onNodeWithText("Science").performClick()
        assertEquals("science", selectedFilter)
    }

    @Test
    fun hallOfFameShowsLeaderboardAndBadgeDialog() {
        val badges = SeedData.defaultBadges().map {
            BadgeItem(
                id = it.id,
                nameEn = it.nameEn,
                nameKn = it.nameKn,
                descriptionEn = it.descriptionEn,
                descriptionKn = it.descriptionKn,
                icon = it.icon,
                rarity = it.rarity,
                unlocked = it.unlocked
            )
        }
        val entries = listOf(
            LeaderboardEntry("lead_1", 1, "Ananya Rao", "Channapatna", "School", 850, 9, false),
            LeaderboardEntry("user_1", 2, "Ananda (You)", "Channapatna Rural", "School", 450, 5, true)
        )

        composeRule.setContent {
            NannaShikshanaTheme {
                HallOfFameScreen(
                    leaderboard = entries,
                    badges = badges,
                    language = AppLanguage.EN,
                    selectedBadge = badges.first(),
                    onSelectBadge = {},
                    onDismissBadge = {}
                )
            }
        }

        composeRule.onNodeWithText("Ananya Rao").assertIsDisplayed()
        composeRule.onNodeWithText("YOU").assertIsDisplayed()
        composeRule.onNodeWithText("Completed your very first offline curriculum module.").assertIsDisplayed()
    }
}
