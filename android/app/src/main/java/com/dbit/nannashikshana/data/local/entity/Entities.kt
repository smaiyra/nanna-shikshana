package com.dbit.nannashikshana.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "student_profile")
data class StudentProfileEntity(
    @PrimaryKey val id: String,
    val studentCode: String,
    val name: String,
    val village: String,
    val school: String,
    val totalXp: Int,
    val currentLevel: String,
    val streakCount: Int,
    val streakFreezesLeft: Int,
    val lastActiveDate: String
)

@Entity(
    tableName = "lesson_packs",
    indices = [Index(value = ["subject"])]
)
data class LessonPackEntity(
    @PrimaryKey val id: String,
    val packCode: String,
    val subject: String,
    val grade: String,
    val version: Int,
    val sortOrder: Int,
    val fileSizeMb: Double,
    val packageName: String,
    val sha256: String,
    val isDownloaded: Boolean,
    val localArchivePath: String?,
    val titleEn: String,
    val titleKn: String,
    val chapterEn: String,
    val chapterKn: String,
    val descriptionEn: String,
    val descriptionKn: String,
    val diagramType: String,
    val audioTextEn: String,
    val audioTextKn: String,
    val sectionsJson: String,
    val quizJson: String
)

@Entity(tableName = "lesson_progress")
data class LessonProgressEntity(
    @PrimaryKey val lessonId: String,
    val completed: Boolean,
    val bestQuizScore: Int,
    val totalQuestions: Int,
    val updatedAtMs: Long
)

@Entity(tableName = "daily_quests")
data class DailyQuestEntity(
    @PrimaryKey val id: String,
    val titleEn: String,
    val titleKn: String,
    val subEn: String,
    val subKn: String,
    val xpReward: Int,
    val targetType: String,
    val progress: Int,
    val maxProgress: Int,
    val claimed: Boolean,
    val sortOrder: Int
)

@Entity(tableName = "badges")
data class BadgeEntity(
    @PrimaryKey val id: String,
    val nameEn: String,
    val nameKn: String,
    val descriptionEn: String,
    val descriptionKn: String,
    val icon: String,
    val rarity: String,
    val unlocked: Boolean,
    val sortOrder: Int
)

@Entity(tableName = "leaderboard_peers")
data class LeaderboardPeerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val village: String,
    val school: String,
    val totalXp: Int,
    val streak: Int
)

@Entity(
    tableName = "pending_sync_events",
    indices = [Index(value = ["idempotencyKey"], unique = true), Index(value = ["synced"])]
)
data class PendingSyncEventEntity(
    @PrimaryKey val eventId: String,
    val idempotencyKey: String,
    val eventType: String,
    val payloadJson: String,
    val clientTimestampIso: String,
    val synced: Boolean = false,
    val retryCount: Int = 0,
    val createdAtMs: Long = System.currentTimeMillis()
)
