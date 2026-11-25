package com.dbit.nannashikshana

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.dbit.nannashikshana.data.local.NannaDatabase
import com.dbit.nannashikshana.data.local.PreferencesStore
import com.dbit.nannashikshana.data.remote.NannaApiService
import com.dbit.nannashikshana.data.remote.dto.LoginRequestDto
import com.dbit.nannashikshana.data.remote.dto.PackVersionCheckRequestDto
import com.dbit.nannashikshana.data.remote.dto.PackVersionCheckResponseDto
import com.dbit.nannashikshana.data.remote.dto.PaginatedLeaderboardDto
import com.dbit.nannashikshana.data.remote.dto.RegisterRequestDto
import com.dbit.nannashikshana.data.remote.dto.SyncPullResponseDto
import com.dbit.nannashikshana.data.remote.dto.SyncPushRequestDto
import com.dbit.nannashikshana.data.remote.dto.SyncPushResponseDto
import com.dbit.nannashikshana.data.remote.dto.TokenResponseDto
import com.dbit.nannashikshana.data.repository.LearningRepository
import com.dbit.nannashikshana.domain.model.AppLanguage
import com.dbit.nannashikshana.domain.model.PeerDevice
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class RoomOfflineRepositoryTest {

    private lateinit var context: Context
    private lateinit var db: NannaDatabase
    private lateinit var repository: LearningRepository

    private val offlineFailingApi = object : NannaApiService {
        override suspend fun register(req: RegisterRequestDto): Response<TokenResponseDto> =
            throw IllegalStateException("Offline")

        override suspend fun login(req: LoginRequestDto): Response<TokenResponseDto> =
            throw IllegalStateException("Offline")

        override suspend fun checkPackVersions(req: PackVersionCheckRequestDto): Response<PackVersionCheckResponseDto> =
            throw IllegalStateException("Offline")

        override suspend fun downloadPack(packCode: String): Response<ResponseBody> =
            throw IllegalStateException("Offline")

        override suspend fun getLeaderboard(page: Int, pageSize: Int): Response<PaginatedLeaderboardDto> =
            throw IllegalStateException("Offline")

        override suspend fun pushSyncEvents(req: SyncPushRequestDto): Response<SyncPushResponseDto> =
            throw IllegalStateException("Offline")

        override suspend fun pullSyncChanges(since: Int, limit: Int): Response<SyncPullResponseDto> =
            throw IllegalStateException("Offline")
    }

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, NannaDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = LearningRepository(
            context = context,
            studentDao = db.studentDao(),
            lessonDao = db.lessonDao(),
            questDao = db.questDao(),
            badgeDao = db.badgeDao(),
            leaderboardDao = db.leaderboardDao(),
            syncEventDao = db.syncEventDao(),
            preferencesStore = PreferencesStore(context),
            apiService = offlineFailingApi
        )
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun offlineSeedingQuizAndQuestClaimPersistInRoomAndQueueSync() = runTest {
        repository.ensureSeeded()

        val initialProfile = repository.profileFlow.first()
        assertNotNull(initialProfile)
        assertEquals(450, initialProfile?.totalXp)

        val lessons = repository.lessonsFlow.first()
        assertEquals(4, lessons.size)

        val scienceLesson = lessons.first { it.id == "sci_photosynthesis_ch5" }
        val quizOutcome = repository.completeQuiz(scienceLesson, score = 3, selectedIndices = listOf(0, 1, 1))
        assertEquals(60, quizOutcome.xpEarned)

        val questsAfterQuiz = repository.questsFlow.first()
        val sciQuest = questsAfterQuiz.first { it.id == "quest_science" }
        assertEquals(1, sciQuest.progress)

        val claimOutcome = repository.claimQuest("quest_science")
        assertNotNull(claimOutcome)
        assertEquals(50, claimOutcome?.xpEarned)

        val updatedProfile = repository.profileFlow.first()
        assertEquals(560, updatedProfile?.totalXp)
        assertTrue(updatedProfile?.completedLessons?.contains("sci_photosynthesis_ch5") == true)

        val pending = db.syncEventDao().getPendingEvents(50)
        assertTrue(pending.any { it.eventType == "quiz_completed" })
        assertTrue(pending.any { it.eventType == "quest_claimed" })
    }

    @Test
    fun offlineP2PTransferAwardsXpAndLanguageTogglePersists() = runTest {
        repository.ensureSeeded()
        val mathLesson = repository.lessonsFlow.first().first { it.id == "math_fractions_ch3" }
        val peer = PeerDevice("peer_1", "Raju's Phone", "Redmi 9", "Raju", 4, "2.4 m", 68)

        val outcome = repository.recordP2PTransfer(mathLesson, peer, mathLesson.sha256)
        assertEquals(50, outcome.xpEarned)

        val nextLang = repository.toggleLanguage()
        assertEquals(AppLanguage.KN, nextLang)

        val pending = db.syncEventDao().getPendingEvents(50)
        assertTrue(pending.any { it.eventType == "p2p_transfer_completed" })
        assertTrue(pending.any { it.eventType == "profile_updated" })
    }
}
