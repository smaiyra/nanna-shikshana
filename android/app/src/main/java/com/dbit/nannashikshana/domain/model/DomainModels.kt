package com.dbit.nannashikshana.domain.model

enum class AppLanguage(val code: String) {
    EN("en"),
    KN("kn");

    companion object {
        fun fromCode(code: String?): AppLanguage = if (code == "kn") KN else EN
    }
}

enum class SyncState {
    OFFLINE,
    SYNCING,
    SYNCED
}

data class SyncStatus(
    val state: SyncState = SyncState.OFFLINE,
    val pendingEventsCount: Int = 0,
    val lastSyncedAtMs: Long? = null
)

data class LevelInfo(
    val levelNumber: Int,
    val titleEn: String,
    val titleKn: String,
    val currentXp: Int,
    val nextLevelXp: Int,
    val neededXp: Int,
    val progressPct: Float,
    val colorHex: Long
)

data class StudentProfile(
    val id: String,
    val studentCode: String,
    val name: String,
    val village: String,
    val school: String,
    val totalXp: Int,
    val currentLevel: String,
    val streakCount: Int,
    val streakFreezesLeft: Int,
    val lastActiveDate: String,
    val unlockedBadges: List<String>,
    val completedLessons: List<String>,
    val completedQuizzes: Map<String, Int>,
    val claimedQuests: List<String>
)

data class LessonSection(
    val headingEn: String,
    val headingKn: String,
    val bodyEn: String,
    val bodyKn: String
)

data class QuizQuestion(
    val id: String,
    val questionEn: String,
    val questionKn: String,
    val optionsEn: List<String>,
    val optionsKn: List<String>,
    val correctIndex: Int,
    val explanationEn: String,
    val explanationKn: String
)

data class LessonModule(
    val id: String,
    val packCode: String,
    val subject: String,
    val grade: String,
    val version: Int,
    val fileSizeMb: Double,
    val packageName: String,
    val sha256: String,
    val isDownloaded: Boolean,
    val titleEn: String,
    val titleKn: String,
    val chapterEn: String,
    val chapterKn: String,
    val descriptionEn: String,
    val descriptionKn: String,
    val diagramType: String,
    val audioTextEn: String,
    val audioTextKn: String,
    val contentSections: List<LessonSection>,
    val quiz: List<QuizQuestion>
)

data class DailyQuest(
    val id: String,
    val titleEn: String,
    val titleKn: String,
    val subEn: String,
    val subKn: String,
    val xpReward: Int,
    val targetType: String,
    val progress: Int,
    val maxProgress: Int,
    val claimed: Boolean
)

data class BadgeItem(
    val id: String,
    val nameEn: String,
    val nameKn: String,
    val descriptionEn: String,
    val descriptionKn: String,
    val icon: String,
    val rarity: String,
    val unlocked: Boolean
)

data class LeaderboardEntry(
    val id: String,
    val rank: Int,
    val name: String,
    val village: String,
    val school: String,
    val totalXp: Int,
    val streak: Int,
    val isUser: Boolean
)

data class PeerDevice(
    val id: String,
    val deviceName: String,
    val deviceModel: String,
    val ownerName: String,
    val signalStrength: Int,
    val distanceM: String,
    val batteryPct: Int,
    val deviceAddress: String? = null,
    val isRealWifiDirect: Boolean = false
)

data class KannadaVowel(
    val letter: String,
    val translit: String,
    val typeKn: String,
    val typeEn: String,
    val exampleKn: String,
    val exampleEn: String,
    val meaningEn: String
)

enum class ToastType {
    INFO,
    QUEST,
    QUIZ,
    P2P,
    STREAK,
    LEVEL
}

data class AppToast(
    val id: String,
    val type: ToastType,
    val title: String,
    val description: String? = null
)
