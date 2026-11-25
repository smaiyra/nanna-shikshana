package com.dbit.nannashikshana.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dbit.nannashikshana.data.local.entity.BadgeEntity
import com.dbit.nannashikshana.data.local.entity.DailyQuestEntity
import com.dbit.nannashikshana.data.local.entity.LeaderboardPeerEntity
import com.dbit.nannashikshana.data.local.entity.LessonPackEntity
import com.dbit.nannashikshana.data.local.entity.LessonProgressEntity
import com.dbit.nannashikshana.data.local.entity.PendingSyncEventEntity
import com.dbit.nannashikshana.data.local.entity.StudentProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Query("SELECT * FROM student_profile LIMIT 1")
    fun observeProfile(): Flow<StudentProfileEntity?>

    @Query("SELECT * FROM student_profile LIMIT 1")
    suspend fun getProfile(): StudentProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfile(profile: StudentProfileEntity)
}

@Dao
interface LessonDao {
    @Query("SELECT * FROM lesson_packs ORDER BY sortOrder ASC")
    fun observeLessons(): Flow<List<LessonPackEntity>>

    @Query("SELECT * FROM lesson_packs ORDER BY sortOrder ASC")
    suspend fun getLessons(): List<LessonPackEntity>

    @Query("SELECT * FROM lesson_packs WHERE id = :lessonId LIMIT 1")
    suspend fun getLesson(lessonId: String): LessonPackEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLessons(lessons: List<LessonPackEntity>)

    @Query("UPDATE lesson_packs SET isDownloaded = :downloaded, localArchivePath = :localPath, version = :version, sha256 = :sha256 WHERE packCode = :packCode")
    suspend fun updatePackDownloadState(packCode: String, downloaded: Boolean, localPath: String?, version: Int, sha256: String)

    @Query("SELECT * FROM lesson_progress")
    fun observeProgress(): Flow<List<LessonProgressEntity>>

    @Query("SELECT * FROM lesson_progress WHERE lessonId = :lessonId LIMIT 1")
    suspend fun getProgress(lessonId: String): LessonProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: LessonProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgressList(progress: List<LessonProgressEntity>)

    @Query("DELETE FROM lesson_progress")
    suspend fun clearProgress()
}

@Dao
interface QuestDao {
    @Query("SELECT * FROM daily_quests ORDER BY sortOrder ASC")
    fun observeQuests(): Flow<List<DailyQuestEntity>>

    @Query("SELECT * FROM daily_quests WHERE id = :questId LIMIT 1")
    suspend fun getQuest(questId: String): DailyQuestEntity?

    @Query("SELECT * FROM daily_quests WHERE targetType = :targetType")
    suspend fun getQuestsByTarget(targetType: String): List<DailyQuestEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertQuests(quests: List<DailyQuestEntity>)

    @Query("UPDATE daily_quests SET progress = :progress WHERE id = :questId")
    suspend fun updateProgress(questId: String, progress: Int)

    @Query("UPDATE daily_quests SET claimed = :claimed WHERE id = :questId")
    suspend fun markClaimed(questId: String, claimed: Boolean)
}

@Dao
interface BadgeDao {
    @Query("SELECT * FROM badges ORDER BY sortOrder ASC")
    fun observeBadges(): Flow<List<BadgeEntity>>

    @Query("SELECT * FROM badges")
    suspend fun getBadges(): List<BadgeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBadges(badges: List<BadgeEntity>)

    @Query("UPDATE badges SET unlocked = 1 WHERE id = :badgeId")
    suspend fun unlockBadge(badgeId: String)
}

@Dao
interface LeaderboardDao {
    @Query("SELECT * FROM leaderboard_peers ORDER BY totalXp DESC")
    fun observePeers(): Flow<List<LeaderboardPeerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPeers(peers: List<LeaderboardPeerEntity>)
}

@Dao
interface SyncEventDao {
    @Query("SELECT COUNT(*) FROM pending_sync_events WHERE synced = 0")
    fun observePendingCount(): Flow<Int>

    @Query("SELECT * FROM pending_sync_events WHERE synced = 0 ORDER BY createdAtMs ASC LIMIT :limit")
    suspend fun getPendingEvents(limit: Int = 50): List<PendingSyncEventEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun enqueue(event: PendingSyncEventEntity)

    @Query("UPDATE pending_sync_events SET synced = 1 WHERE idempotencyKey IN (:keys)")
    suspend fun markSynced(keys: List<String>)

    @Query("UPDATE pending_sync_events SET retryCount = retryCount + 1 WHERE eventId IN (:eventIds)")
    suspend fun incrementRetries(eventIds: List<String>)

    @Query("DELETE FROM pending_sync_events")
    suspend fun clearAll()
}
