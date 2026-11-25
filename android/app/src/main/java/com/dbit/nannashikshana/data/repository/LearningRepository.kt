package com.dbit.nannashikshana.data.repository

import android.content.Context
import com.dbit.nannashikshana.data.local.PreferencesStore
import com.dbit.nannashikshana.data.local.SeedData
import com.dbit.nannashikshana.data.local.dao.BadgeDao
import com.dbit.nannashikshana.data.local.dao.LeaderboardDao
import com.dbit.nannashikshana.data.local.dao.LessonDao
import com.dbit.nannashikshana.data.local.dao.QuestDao
import com.dbit.nannashikshana.data.local.dao.StudentDao
import com.dbit.nannashikshana.data.local.dao.SyncEventDao
import com.dbit.nannashikshana.data.local.entity.LeaderboardPeerEntity
import com.dbit.nannashikshana.data.local.entity.LessonProgressEntity
import com.dbit.nannashikshana.data.local.entity.PendingSyncEventEntity
import com.dbit.nannashikshana.data.local.entity.StudentProfileEntity
import com.dbit.nannashikshana.data.remote.NannaApiService
import com.dbit.nannashikshana.data.remote.dto.LoginRequestDto
import com.dbit.nannashikshana.data.remote.dto.PackVersionCheckRequestDto
import com.dbit.nannashikshana.data.remote.dto.RegisterRequestDto
import com.dbit.nannashikshana.data.remote.dto.SyncEventDto
import com.dbit.nannashikshana.data.remote.dto.SyncPushRequestDto
import com.dbit.nannashikshana.data.sync.SyncWorker
import com.dbit.nannashikshana.domain.gamification.GamificationEngine
import com.dbit.nannashikshana.domain.gamification.StreakEvaluationResult
import com.dbit.nannashikshana.domain.model.AppLanguage
import com.dbit.nannashikshana.domain.model.BadgeItem
import com.dbit.nannashikshana.domain.model.DailyQuest
import com.dbit.nannashikshana.domain.model.LeaderboardEntry
import com.dbit.nannashikshana.domain.model.LessonModule
import com.dbit.nannashikshana.domain.model.LessonSection
import com.dbit.nannashikshana.domain.model.PeerDevice
import com.dbit.nannashikshana.domain.model.QuizQuestion
import com.dbit.nannashikshana.domain.model.StudentProfile
import com.dbit.nannashikshana.domain.model.SyncState
import com.dbit.nannashikshana.domain.model.SyncStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.io.File
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

data class QuizCompletionOutcome(
    val xpEarned: Int,
    val leveledUpTo: String? = null,
    val newlyUnlockedBadges: List<String> = emptyList()
)

data class QuestClaimOutcome(
    val questTitleEn: String,
    val questTitleKn: String,
    val xpEarned: Int,
    val leveledUpTo: String? = null
)

data class P2POutcome(
    val xpEarned: Int,
    val leveledUpTo: String? = null
)

@Singleton
class LearningRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val studentDao: StudentDao,
    private val lessonDao: LessonDao,
    private val questDao: QuestDao,
    private val badgeDao: BadgeDao,
    private val leaderboardDao: LeaderboardDao,
    private val syncEventDao: SyncEventDao,
    private val preferencesStore: PreferencesStore,
    private val apiService: NannaApiService
) {
    private val gson = Gson()
    private val isSyncingFlow = MutableStateFlow(false)
    private var p2pTransferCountLocal = 1

    val languageFlow: Flow<AppLanguage> = preferencesStore.languageFlow

    val syncStatusFlow: Flow<SyncStatus> = combine(
        isSyncingFlow,
        syncEventDao.observePendingCount(),
        preferencesStore.lastSyncedMsFlow
    ) { syncing, pendingCount, lastSynced ->
        val state = when {
            syncing -> SyncState.SYNCING
            lastSynced != null && pendingCount == 0 -> SyncState.SYNCED
            else -> SyncState.OFFLINE
        }
        SyncStatus(state = state, pendingEventsCount = pendingCount, lastSyncedAtMs = lastSynced)
    }

    val profileFlow: Flow<StudentProfile?> = combine(
        studentDao.observeProfile(),
        lessonDao.observeProgress(),
        badgeDao.observeBadges(),
        questDao.observeQuests()
    ) { profile, progressList, badges, quests ->
        profile?.let {
            StudentProfile(
                id = it.id,
                studentCode = it.studentCode,
                name = it.name,
                village = it.village,
                school = it.school,
                totalXp = it.totalXp,
                currentLevel = it.currentLevel,
                streakCount = it.streakCount,
                streakFreezesLeft = it.streakFreezesLeft,
                lastActiveDate = it.lastActiveDate,
                unlockedBadges = badges.filter { b -> b.unlocked }.map { b -> b.id },
                completedLessons = progressList.filter { p -> p.completed }.map { p -> p.lessonId },
                completedQuizzes = progressList.filter { p -> p.completed }.associate { p -> p.lessonId to p.bestQuizScore },
                claimedQuests = quests.filter { q -> q.claimed }.map { q -> q.id }
            )
        }
    }

    val lessonsFlow: Flow<List<LessonModule>> = lessonDao.observeLessons().combine(
        kotlinx.coroutines.flow.flowOf(Unit)
    ) { entities, _ ->
        val sectionsType = object : TypeToken<List<LessonSection>>() {}.type
        val quizType = object : TypeToken<List<QuizQuestion>>() {}.type
        entities.map { entity ->
            LessonModule(
                id = entity.id,
                packCode = entity.packCode,
                subject = entity.subject,
                grade = entity.grade,
                version = entity.version,
                fileSizeMb = entity.fileSizeMb,
                packageName = entity.packageName,
                sha256 = entity.sha256,
                isDownloaded = entity.isDownloaded,
                titleEn = entity.titleEn,
                titleKn = entity.titleKn,
                chapterEn = entity.chapterEn,
                chapterKn = entity.chapterKn,
                descriptionEn = entity.descriptionEn,
                descriptionKn = entity.descriptionKn,
                diagramType = entity.diagramType,
                audioTextEn = entity.audioTextEn,
                audioTextKn = entity.audioTextKn,
                contentSections = gson.fromJson(entity.sectionsJson, sectionsType),
                quiz = gson.fromJson(entity.quizJson, quizType)
            )
        }
    }

    val questsFlow: Flow<List<DailyQuest>> = questDao.observeQuests().combine(
        kotlinx.coroutines.flow.flowOf(Unit)
    ) { list, _ ->
        list.map {
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
    }

    val badgesFlow: Flow<List<BadgeItem>> = badgeDao.observeBadges().combine(
        kotlinx.coroutines.flow.flowOf(Unit)
    ) { list, _ ->
        list.map {
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
    }

    val leaderboardFlow: Flow<List<LeaderboardEntry>> = combine(
        leaderboardDao.observePeers(),
        studentDao.observeProfile()
    ) { peers, student ->
        val rows = peers.map {
            LeaderboardEntry(
                id = it.id,
                rank = 0,
                name = it.name,
                village = it.village,
                school = it.school,
                totalXp = it.totalXp,
                streak = it.streak,
                isUser = false
            )
        }.toMutableList()
        if (student != null) {
            rows.add(
                LeaderboardEntry(
                    id = student.id,
                    rank = 0,
                    name = "${student.name} (You)",
                    village = student.village,
                    school = student.school,
                    totalXp = student.totalXp,
                    streak = student.streakCount,
                    isUser = true
                )
            )
        }
        rows.sortedByDescending { it.totalXp }
            .mapIndexed { index, entry -> entry.copy(rank = index + 1) }
    }

    suspend fun ensureSeeded() {
        if (studentDao.getProfile() == null) {
            studentDao.upsertProfile(SeedData.defaultProfile())
            lessonDao.upsertLessons(SeedData.defaultLessons())
            lessonDao.upsertProgressList(SeedData.defaultProgress())
            questDao.upsertQuests(SeedData.defaultQuests())
            badgeDao.upsertBadges(SeedData.defaultBadges())
            leaderboardDao.upsertPeers(SeedData.defaultLeaderboardPeers())
        }
    }

    suspend fun toggleLanguage(): AppLanguage {
        val current = preferencesStore.languageFlow.first()
        val next = if (current == AppLanguage.EN) AppLanguage.KN else AppLanguage.EN
        preferencesStore.setLanguage(next)
        enqueueSyncEvent(
            eventType = "profile_updated",
            payload = mapOf("preferred_lang" to next.code)
        )
        return next
    }

    suspend fun evaluateStartupStreak(): StreakEvaluationResult? {
        val profile = profileFlow.first() ?: return null
        val result = GamificationEngine.evaluateStreak(profile)
        applyStreakResult(result)
        return result
    }

    suspend fun simulateStreak(daysAgo: Int): StreakEvaluationResult? {
        val profile = profileFlow.first() ?: return null
        val simulatedLastDate = GamificationEngine.daysAgoIso(daysAgo)
        val result = GamificationEngine.evaluateStreak(
            profile.copy(lastActiveDate = simulatedLastDate),
            GamificationEngine.todayIso()
        )
        applyStreakResult(result)
        return result
    }

    private suspend fun applyStreakResult(result: StreakEvaluationResult) {
        val updated = when (result) {
            is StreakEvaluationResult.Maintained -> return
            is StreakEvaluationResult.Incremented -> result.profile
            is StreakEvaluationResult.Frozen -> result.profile
            is StreakEvaluationResult.Reset -> result.profile
        }
        val entity = studentDao.getProfile() ?: return
        studentDao.upsertProfile(
            entity.copy(
                streakCount = updated.streakCount,
                streakFreezesLeft = updated.streakFreezesLeft,
                lastActiveDate = updated.lastActiveDate
            )
        )
        updated.unlockedBadges.forEach { badgeDao.unlockBadge(it) }
        enqueueSyncEvent(
            eventType = "streak_updated",
            payload = mapOf("active_date" to updated.lastActiveDate)
        )
    }

    suspend fun recordLessonViewed(lessonId: String) {
        val existing = lessonDao.getProgress(lessonId)
        if (existing == null) {
            lessonDao.upsertProgress(
                LessonProgressEntity(
                    lessonId = lessonId,
                    completed = false,
                    bestQuizScore = 0,
                    totalQuestions = 3,
                    updatedAtMs = System.currentTimeMillis()
                )
            )
        } else {
            lessonDao.upsertProgress(existing.copy(updatedAtMs = System.currentTimeMillis()))
        }
        enqueueSyncEvent(
            eventType = "lesson_viewed",
            payload = mapOf("lesson_key" to lessonId)
        )
    }

    suspend fun completeQuiz(
        lesson: LessonModule,
        score: Int,
        selectedIndices: List<Int>
    ): QuizCompletionOutcome {
        val xpEarned = score * 20
        val existingProgress = lessonDao.getProgress(lesson.id)
        val bestScore = maxOf(score, existingProgress?.bestQuizScore ?: 0)
        lessonDao.upsertProgress(
            LessonProgressEntity(
                lessonId = lesson.id,
                completed = true,
                bestQuizScore = bestScore,
                totalQuestions = lesson.quiz.size,
                updatedAtMs = System.currentTimeMillis()
            )
        )

        val newlyUnlocked = mutableListOf<String>()
        val badgeMap = badgeDao.getBadges().associateBy { it.id }
        if (badgeMap["First Lesson"]?.unlocked == false) {
            badgeDao.unlockBadge("First Lesson")
            newlyUnlocked.add("First Lesson")
        }
        if (score == lesson.quiz.size && lesson.id == "math_fractions_ch3" && badgeMap["Math Master"]?.unlocked == false) {
            badgeDao.unlockBadge("Math Master")
            newlyUnlocked.add("Math Master")
        }

        if (lesson.subject == "science") {
            questDao.getQuestsByTarget("complete_science").forEach {
                questDao.updateProgress(it.id, it.maxProgress)
            }
        }
        if (score == lesson.quiz.size) {
            questDao.getQuestsByTarget("quiz_100").forEach {
                questDao.updateProgress(it.id, it.maxProgress)
            }
        }

        val leveledUpTo = addXpInternal(xpEarned)
        enqueueSyncEvent(
            eventType = "quiz_completed",
            payload = mapOf(
                "lesson_key" to lesson.id,
                "score" to score,
                "total_questions" to lesson.quiz.size,
                "selected_indices" to selectedIndices
            )
        )
        return QuizCompletionOutcome(
            xpEarned = xpEarned,
            leveledUpTo = leveledUpTo,
            newlyUnlockedBadges = newlyUnlocked
        )
    }

    suspend fun claimQuest(questId: String): QuestClaimOutcome? {
        val quest = questDao.getQuest(questId) ?: return null
        if (quest.claimed || quest.progress < quest.maxProgress) return null
        questDao.markClaimed(questId, true)
        val leveledUp = addXpInternal(quest.xpReward)
        enqueueSyncEvent(
            eventType = "quest_claimed",
            payload = mapOf(
                "quest_key" to quest.id,
                "quest_date" to GamificationEngine.todayIso()
            )
        )
        return QuestClaimOutcome(
            questTitleEn = quest.titleEn,
            questTitleKn = quest.titleKn,
            xpEarned = quest.xpReward,
            leveledUpTo = leveledUp
        )
    }

    suspend fun recordP2PTransfer(
        lesson: LessonModule,
        peer: PeerDevice,
        verifiedSha256: String
    ): P2POutcome {
        p2pTransferCountLocal += 1
        questDao.getQuestsByTarget("p2p_share").forEach {
            questDao.updateProgress(it.id, it.maxProgress)
        }
        badgeDao.unlockBadge("P2P Sharer")
        if (p2pTransferCountLocal >= 3) {
            badgeDao.unlockBadge("P2P Pioneer")
        }
        val leveledUp = addXpInternal(50)
        enqueueSyncEvent(
            eventType = "p2p_transfer_completed",
            payload = mapOf(
                "lesson_key" to lesson.id,
                "package_name" to lesson.packageName,
                "sha256" to verifiedSha256,
                "peer_device_name" to peer.deviceName
            )
        )
        return P2POutcome(xpEarned = 50, leveledUpTo = leveledUp)
    }

    suspend fun addDemoXp(delta: Int): String? = addXpInternal(delta)

    suspend fun resetDemoData() {
        studentDao.upsertProfile(SeedData.defaultProfile())
        lessonDao.clearProgress()
        lessonDao.upsertProgressList(SeedData.defaultProgress())
        questDao.upsertQuests(SeedData.defaultQuests())
        badgeDao.upsertBadges(SeedData.defaultBadges())
        leaderboardDao.upsertPeers(SeedData.defaultLeaderboardPeers())
        syncEventDao.clearAll()
        p2pTransferCountLocal = 1
    }

    private suspend fun addXpInternal(delta: Int): String? {
        if (delta <= 0) return null
        val entity = studentDao.getProfile() ?: return null
        val prevLevel = GamificationEngine.calculateLevelInfo(entity.totalXp)
        val nextXp = entity.totalXp + delta
        val nextLevel = GamificationEngine.calculateLevelInfo(nextXp)
        studentDao.upsertProfile(
            entity.copy(
                totalXp = nextXp,
                currentLevel = nextLevel.titleEn
            )
        )
        return if (nextLevel.levelNumber > prevLevel.levelNumber) nextLevel.titleEn else null
    }

    private suspend fun enqueueSyncEvent(eventType: String, payload: Map<String, Any>) {
        val eventId = UUID.randomUUID().toString()
        val idempotencyKey = "$eventType-$eventId"
        syncEventDao.enqueue(
            PendingSyncEventEntity(
                eventId = eventId,
                idempotencyKey = idempotencyKey,
                eventType = eventType,
                payloadJson = gson.toJson(payload),
                clientTimestampIso = nowIsoUtc()
            )
        )
        try {
            SyncWorker.enqueueImmediate(context)
        } catch (_: Exception) {
            // Safe in unit tests without WorkManager initialized
        }
    }

    suspend fun downloadPack(packCode: String): Boolean {
        return try {
            ensureAuthenticated()
            val resp = apiService.downloadPack(packCode)
            if (!resp.isSuccessful) return false
            val body = resp.body() ?: return false
            val bytes = body.bytes()
            val expectedSha = resp.headers()["X-Pack-SHA256"]
            val version = resp.headers()["X-Pack-Version"]?.toIntOrNull() ?: 1
            val actualSha = sha256Hex(bytes)
            if (expectedSha != null && !expectedSha.equals(actualSha, ignoreCase = true)) {
                return false
            }
            val packsDir = File(context.filesDir, "packs").apply { mkdirs() }
            val lesson = lessonDao.getLessons().firstOrNull { it.packCode == packCode } ?: return false
            val targetFile = File(packsDir, lesson.packageName)
            targetFile.writeBytes(bytes)
            lessonDao.updatePackDownloadState(
                packCode = packCode,
                downloaded = true,
                localPath = targetFile.absolutePath,
                version = version,
                sha256 = actualSha
            )
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun syncWithBackend(): Boolean {
        isSyncingFlow.value = true
        return try {
            if (!ensureAuthenticated()) {
                return false
            }
            val pending = syncEventDao.getPendingEvents(limit = 50)
            val mapType = object : TypeToken<Map<String, Any>>() {}.type
            if (pending.isNotEmpty()) {
                val dtoEvents = pending.map { ev ->
                    SyncEventDto(
                        eventId = ev.eventId,
                        idempotencyKey = ev.idempotencyKey,
                        eventType = ev.eventType,
                        clientTimestamp = ev.clientTimestampIso,
                        payload = gson.fromJson(ev.payloadJson, mapType)
                    )
                }
                val pushResp = apiService.pushSyncEvents(SyncPushRequestDto(dtoEvents))
                if (!pushResp.isSuccessful) {
                    syncEventDao.incrementRetries(pending.map { it.eventId })
                    return false
                }
                val body = pushResp.body()
                if (body != null) {
                    val ackedKeys = body.results
                        .filter { it.status == "applied" || it.status == "duplicate_ignored" || it.status == "conflict_resolved" }
                        .map { it.idempotencyKey }
                    if (ackedKeys.isNotEmpty()) {
                        syncEventDao.markSynced(ackedKeys)
                    }
                    preferencesStore.setSyncCursor(body.latestCursor, System.currentTimeMillis())
                }
            }

            val cursor = preferencesStore.getSyncCursor()
            val pullResp = apiService.pullSyncChanges(since = cursor, limit = 50)
            if (pullResp.isSuccessful) {
                val pullBody = pullResp.body()
                if (pullBody != null) {
                    preferencesStore.setSyncCursor(pullBody.nextCursor, System.currentTimeMillis())
                }
            }

            // Check pack versions
            val installedVersions = lessonDao.getLessons().associate { it.packCode to it.version }
            val verResp = apiService.checkPackVersions(PackVersionCheckRequestDto(installedVersions))
            if (verResp.isSuccessful) {
                verResp.body()?.updatesAvailable?.forEach { entry ->
                    downloadPack(entry.packCode)
                }
            }

            // Refresh classroom leaderboard peers
            val lbResp = apiService.getLeaderboard()
            if (lbResp.isSuccessful) {
                val peers = lbResp.body()?.items
                    ?.filter { !it.isCurrentUser }
                    ?.map {
                        LeaderboardPeerEntity(
                            id = it.id,
                            name = it.name,
                            village = it.village,
                            school = it.school,
                            totalXp = it.totalXp,
                            streak = it.streak
                        )
                    }
                if (!peers.isNullOrEmpty()) {
                    leaderboardDao.upsertPeers(peers)
                }
            }
            true
        } catch (_: Exception) {
            false
        } finally {
            isSyncingFlow.value = false
        }
    }

    private suspend fun ensureAuthenticated(): Boolean {
        if (preferencesStore.getJwtToken() != null) return true
        val profile = studentDao.getProfile() ?: return false
        val deviceId = preferencesStore.getOrCreateDeviceId()
        val lang = preferencesStore.languageFlow.first().code
        val studentCode = "${profile.studentCode}_${deviceId.takeLast(6)}"
        val passcode = "nanna_${deviceId}"

        val regResp = apiService.register(
            RegisterRequestDto(
                studentCode = studentCode,
                deviceId = deviceId,
                passcode = passcode,
                name = profile.name,
                village = profile.village,
                school = profile.school,
                preferredLang = lang
            )
        )
        if (regResp.isSuccessful) {
            val token = regResp.body()?.accessToken
            if (token != null) {
                preferencesStore.setJwtToken(token)
                preferencesStore.setStudentCode(studentCode)
                return true
            }
        }
        val loginResp = apiService.login(
            LoginRequestDto(
                studentCode = studentCode,
                passcode = passcode,
                deviceId = deviceId
            )
        )
        if (loginResp.isSuccessful) {
            val token = loginResp.body()?.accessToken
            if (token != null) {
                preferencesStore.setJwtToken(token)
                return true
            }
        }
        return false
    }

    private fun sha256Hex(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }

    private fun nowIsoUtc(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date())
    }
}
