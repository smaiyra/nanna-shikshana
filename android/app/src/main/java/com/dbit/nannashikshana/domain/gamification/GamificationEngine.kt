package com.dbit.nannashikshana.domain.gamification

import com.dbit.nannashikshana.domain.model.KannadaVowel
import com.dbit.nannashikshana.domain.model.LevelInfo
import com.dbit.nannashikshana.domain.model.StudentProfile
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.max
import kotlin.math.min

sealed class StreakEvaluationResult {
    data class Maintained(val profile: StudentProfile) : StreakEvaluationResult()
    data class Incremented(val profile: StudentProfile, val newStreak: Int) : StreakEvaluationResult()
    data class Frozen(val profile: StudentProfile) : StreakEvaluationResult()
    data class Reset(val profile: StudentProfile) : StreakEvaluationResult()
}

object GamificationEngine {

    fun todayIso(): String = formatDate(Date())

    fun daysAgoIso(daysAgo: Int): String {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
        return formatDate(cal.time)
    }

    private fun formatDate(date: Date): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(date)
    }

    private fun diffDays(currentIso: String, lastIso: String): Int {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val curr = sdf.parse(currentIso)?.time ?: return 0
        val prev = sdf.parse(lastIso)?.time ?: return 0
        return ((curr - prev) / 86_400_000L).toInt()
    }

    fun calculateLevelInfo(totalXp: Int): LevelInfo {
        return when {
            totalXp <= 200 -> {
                val pct = min(100f, max(0f, (totalXp / 200f) * 100f))
                LevelInfo(
                    levelNumber = 1,
                    titleEn = "Level 1: Village Scholar",
                    titleKn = "ಹಂತ 1: ಗ್ರಾಮ ವಿದ್ವಾಂಸ",
                    currentXp = totalXp,
                    nextLevelXp = 201,
                    neededXp = 201 - totalXp,
                    progressPct = pct,
                    colorHex = 0xFF0EA5E9L
                )
            }
            totalXp <= 600 -> {
                val pct = min(100f, max(0f, ((totalXp - 201) / 399f) * 100f))
                LevelInfo(
                    levelNumber = 2,
                    titleEn = "Level 2: District Champion",
                    titleKn = "ಹಂತ 2: ಜಿಲ್ಲಾ ಚಾಂಪಿಯನ್",
                    currentXp = totalXp,
                    nextLevelXp = 601,
                    neededXp = 601 - totalXp,
                    progressPct = pct,
                    colorHex = 0xFF8B5CF6L
                )
            }
            else -> {
                val n = totalXp - 601
                val pct = min(100f, 50f + (n % 500) / 10f)
                LevelInfo(
                    levelNumber = 3,
                    titleEn = "Level 3: State Leader",
                    titleKn = "ಹಂತ 3: ರಾಜ್ಯ ನಾಯಕ",
                    currentXp = totalXp,
                    nextLevelXp = 1200,
                    neededXp = max(0, 1200 - totalXp),
                    progressPct = pct,
                    colorHex = 0xFF10B981L
                )
            }
        }
    }

    fun evaluateStreak(profile: StudentProfile, targetDateIso: String = todayIso()): StreakEvaluationResult {
        val diff = diffDays(targetDateIso, profile.lastActiveDate)
        if (diff <= 0) {
            return StreakEvaluationResult.Maintained(profile)
        }
        if (diff == 1) {
            val newStreak = profile.streakCount + 1
            val updatedBadges = profile.unlockedBadges.toMutableList()
            if (newStreak >= 3 && !updatedBadges.contains("3-Day Streak")) {
                updatedBadges.add("3-Day Streak")
            }
            if (newStreak >= 7 && !updatedBadges.contains("7-Day Flame")) {
                updatedBadges.add("7-Day Flame")
            }
            val updated = profile.copy(
                streakCount = newStreak,
                lastActiveDate = targetDateIso,
                unlockedBadges = updatedBadges
            )
            return StreakEvaluationResult.Incremented(updated, newStreak)
        }
        if (diff == 2 && profile.streakFreezesLeft > 0) {
            val updated = profile.copy(
                streakFreezesLeft = profile.streakFreezesLeft - 1,
                lastActiveDate = targetDateIso
            )
            return StreakEvaluationResult.Frozen(updated)
        }
        val updated = profile.copy(
            streakCount = 1,
            lastActiveDate = targetDateIso
        )
        return StreakEvaluationResult.Reset(updated)
    }

    fun toKannadaDigits(number: Int): String {
        val digits = charArrayOf('೦', '೧', '೨', '೩', '೪', '೫', '೬', '೭', '೮', '೯')
        return number.toString().map { ch ->
            if (ch in '0'..'9') digits[ch - '0'] else ch
        }.joinToString("")
    }

    val aksharamaleVowels: List<KannadaVowel> = listOf(
        KannadaVowel("ಅ", "a", "ಹ್ರಸ್ವಸ್ವರ (೧ ಮಾತ್ರ)", "Short Vowel", "ಅಳಿಲು (Alilu)", "Alilu", "Squirrel"),
        KannadaVowel("ಆ", "aa", "ದೀರ್ಘಸ್ವರ (೨ ಮಾತ್ರ)", "Long Vowel", "ಆನೆ (Aane)", "Aane", "Elephant"),
        KannadaVowel("ಇ", "i", "ಹ್ರಸ್ವಸ್ವರ (೧ ಮಾತ್ರ)", "Short Vowel", "ಇಲಿ (Ili)", "Ili", "Mouse"),
        KannadaVowel("ಈ", "ee", "ದೀರ್ಘಸ್ವರ (೨ ಮಾತ್ರ)", "Long Vowel", "ಈಜು (Eeju)", "Eeju", "Swimming"),
        KannadaVowel("ಉ", "u", "ಹ್ರಸ್ವಸ್ವರ (೧ ಮಾತ್ರ)", "Short Vowel", "ಉಡುಗೊರೆ (Udugore)", "Udugore", "Gift"),
        KannadaVowel("ಊ", "oo", "ದೀರ್ಘಸ್ವರ (೨ ಮಾತ್ರ)", "Long Vowel", "ಊಟ (Oota)", "Oota", "Meal"),
        KannadaVowel("ಋ", "ru", "ಹ್ರಸ್ವಸ್ವರ", "Short Vowel", "ಋಷಿ (Rushi)", "Rishi", "Sage"),
        KannadaVowel("ಎ", "e", "ಹ್ರಸ್ವಸ್ವರ (೧ ಮಾತ್ರ)", "Short Vowel", "ಎಲೆ (Ele)", "Ele", "Leaf"),
        KannadaVowel("ಏ", "ae", "ದೀರ್ಘಸ್ವರ (೨ ಮಾತ್ರ)", "Long Vowel", "ಏಣಿ (Eani)", "Eani", "Ladder"),
        KannadaVowel("ಐ", "ai", "ದೀರ್ಘಸ್ವರ", "Long Vowel", "ಐದು (Aidu)", "Aidu", "Five"),
        KannadaVowel("ಒ", "o", "ಹ್ರಸ್ವಸ್ವರ (೧ ಮಾತ್ರ)", "Short Vowel", "ಒಂಟೆ (Onte)", "Onte", "Camel"),
        KannadaVowel("ಓ", "oh", "ದೀರ್ಘಸ್ವರ (೨ ಮಾತ್ರ)", "Long Vowel", "ಓಟ (Ota)", "Ota", "Race"),
        KannadaVowel("ಔ", "au", "ದೀರ್ಘಸ್ವರ", "Long Vowel", "ಔಷಧ (Aushadha)", "Aushadha", "Medicine"),
        KannadaVowel("ಅಂ", "am", "ಯೋಗವಾಹ (ಅನುಸ್ವಾರ)", "Anusvara", "ಅಂಗಡಿ (Angadi)", "Angadi", "Shop"),
        KannadaVowel("ಅಃ", "aha", "ಯೋಗವಾಹ (ವಿಸರ್ಗ)", "Visarga", "ನಮಃ (Namaha)", "Namaha", "Salutation")
    )
}
