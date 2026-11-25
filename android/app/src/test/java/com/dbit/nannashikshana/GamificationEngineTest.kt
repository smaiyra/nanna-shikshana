package com.dbit.nannashikshana

import com.dbit.nannashikshana.domain.gamification.GamificationEngine
import com.dbit.nannashikshana.domain.gamification.StreakEvaluationResult
import com.dbit.nannashikshana.domain.model.StudentProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GamificationEngineTest {

    private fun sampleProfile(
        xp: Int = 450,
        streak: Int = 5,
        freezes: Int = 2,
        lastActiveDate: String = "2026-11-23"
    ) = StudentProfile(
        id = "user_1",
        studentCode = "STU_ANANDA_01",
        name = "Ananda",
        village = "Channapatna Rural",
        school = "Govt High School, Channapatna",
        totalXp = xp,
        currentLevel = "Level 2: District Champion",
        streakCount = streak,
        streakFreezesLeft = freezes,
        lastActiveDate = lastActiveDate,
        unlockedBadges = emptyList(),
        completedLessons = emptyList(),
        completedQuizzes = emptyMap(),
        claimedQuests = emptyList()
    )

    @Test
    fun levelProgressionMatchesThreeTiers() {
        val lvl1 = GamificationEngine.calculateLevelInfo(150)
        assertEquals(1, lvl1.levelNumber)
        assertEquals("Level 1: Village Scholar", lvl1.titleEn)
        assertEquals(51, lvl1.neededXp)

        val lvl2 = GamificationEngine.calculateLevelInfo(450)
        assertEquals(2, lvl2.levelNumber)
        assertEquals("Level 2: District Champion", lvl2.titleEn)
        assertEquals(151, lvl2.neededXp)

        val lvl3 = GamificationEngine.calculateLevelInfo(800)
        assertEquals(3, lvl3.levelNumber)
        assertEquals("Level 3: State Leader", lvl3.titleEn)
        assertEquals(400, lvl3.neededXp)
    }

    @Test
    fun consecutiveDayIncrementsStreakAndUnlocksSevenDayFlame() {
        val profile = sampleProfile(streak = 6, lastActiveDate = "2026-11-23")
        val result = GamificationEngine.evaluateStreak(profile, targetDateIso = "2026-11-24")
        assertTrue(result is StreakEvaluationResult.Incremented)
        val inc = result as StreakEvaluationResult.Incremented
        assertEquals(7, inc.newStreak)
        assertTrue(inc.profile.unlockedBadges.contains("3-Day Streak"))
        assertTrue(inc.profile.unlockedBadges.contains("7-Day Flame"))
    }

    @Test
    fun missedOneDayConsumesFreezeShield() {
        val profile = sampleProfile(streak = 5, freezes = 2, lastActiveDate = "2026-11-22")
        val result = GamificationEngine.evaluateStreak(profile, targetDateIso = "2026-11-24")
        assertTrue(result is StreakEvaluationResult.Frozen)
        val frozen = result as StreakEvaluationResult.Frozen
        assertEquals(5, frozen.profile.streakCount)
        assertEquals(1, frozen.profile.streakFreezesLeft)
    }

    @Test
    fun missedThreeDaysResetsStreakToOne() {
        val profile = sampleProfile(streak = 5, freezes = 2, lastActiveDate = "2026-11-20")
        val result = GamificationEngine.evaluateStreak(profile, targetDateIso = "2026-11-24")
        assertTrue(result is StreakEvaluationResult.Reset)
        val reset = result as StreakEvaluationResult.Reset
        assertEquals(1, reset.profile.streakCount)
    }

    @Test
    fun kannadaDigitConversionFormatsProperly() {
        assertEquals("೦", GamificationEngine.toKannadaDigits(0))
        assertEquals("೪೫೦", GamificationEngine.toKannadaDigits(450))
        assertEquals("೧೨", GamificationEngine.toKannadaDigits(12))
    }
}
