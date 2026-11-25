package com.dbit.nannashikshana.ui.viewmodel

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import java.util.Locale
import androidx.lifecycle.viewModelScope
import com.dbit.nannashikshana.R
import com.dbit.nannashikshana.data.p2p.WifiDirectMeshManager
import com.dbit.nannashikshana.data.repository.LearningRepository
import com.dbit.nannashikshana.domain.gamification.StreakEvaluationResult
import com.dbit.nannashikshana.domain.model.AppLanguage
import com.dbit.nannashikshana.domain.model.AppToast
import com.dbit.nannashikshana.domain.model.BadgeItem
import com.dbit.nannashikshana.domain.model.DailyQuest
import com.dbit.nannashikshana.domain.model.LeaderboardEntry
import com.dbit.nannashikshana.domain.model.LessonModule
import com.dbit.nannashikshana.domain.model.PeerDevice
import com.dbit.nannashikshana.domain.model.StudentProfile
import com.dbit.nannashikshana.domain.model.SyncStatus
import com.dbit.nannashikshana.domain.model.ToastType
import com.dbit.nannashikshana.ui.util.AudioHelper
import dagger.hilt.android.lifecycle.ApplicationContext
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

enum class NavTab(val route: String) {
    HOME("home"),
    LESSON("lesson"),
    P2P("p2p"),
    LEADERBOARD("leaderboard"),
    ABOUT("about")
}

data class P2PUiState(
    val selectedLessonId: String = "math_fractions_ch3",
    val isScanning: Boolean = false,
    val discoveredPeers: List<PeerDevice> = emptyList(),
    val selectedPeerId: String? = null,
    val isTransferring: Boolean = false,
    val transferProgress: Int = 0,
    val transferDone: Boolean = false,
    val verifiedSha256: String = ""
)

data class QuizUiState(
    val isQuizMode: Boolean = false,
    val currentQuestionIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val answerSubmitted: Boolean = false,
    val score: Int = 0,
    val selectedIndices: List<Int> = emptyList(),
    val completed: Boolean = false
)

data class MainUiState(
    val language: AppLanguage = AppLanguage.EN,
    val activeTab: NavTab = NavTab.HOME,
    val subjectFilter: String = "all",
    val profile: StudentProfile? = null,
    val lessons: List<LessonModule> = emptyList(),
    val selectedLessonId: String = "math_fractions_ch3",
    val quests: List<DailyQuest> = emptyList(),
    val badges: List<BadgeItem> = emptyList(),
    val leaderboard: List<LeaderboardEntry> = emptyList(),
    val syncStatus: SyncStatus = SyncStatus(),
    val quizState: QuizUiState = QuizUiState(),
    val p2pState: P2PUiState = P2PUiState(),
    val isAudioPlaying: Boolean = false,
    val selectedBadgeForModal: BadgeItem? = null,
    val showDemoHelpersSheet: Boolean = false,
    val toasts: List<AppToast> = emptyList(),
    val downloadingPackCode: String? = null
) {
    val activeLesson: LessonModule?
        get() = lessons.firstOrNull { it.id == selectedLessonId } ?: lessons.firstOrNull()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: LearningRepository,
    private val meshManager: WifiDirectMeshManager,
    private val audioHelper: AudioHelper
) : ViewModel() {

    private data class EphemeralState(
        val activeTab: NavTab = NavTab.HOME,
        val subjectFilter: String = "all",
        val selectedLessonId: String = "math_fractions_ch3",
        val quizState: QuizUiState = QuizUiState(),
        val p2pState: P2PUiState = P2PUiState(),
        val isAudioPlaying: Boolean = false,
        val selectedBadge: BadgeItem? = null,
        val showDemoSheet: Boolean = false,
        val toasts: List<AppToast> = emptyList(),
        val downloadingPackCode: String? = null
    )

    private val ephemeral = MutableStateFlow(EphemeralState())

    val uiState: StateFlow<MainUiState> = combine(
        repository.languageFlow,
        repository.profileFlow,
        repository.lessonsFlow,
        repository.questsFlow,
        repository.badgesFlow,
        repository.leaderboardFlow,
        repository.syncStatusFlow,
        ephemeral
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        val lang = values[0] as AppLanguage
        val profile = values[1] as StudentProfile?
        @Suppress("UNCHECKED_CAST")
        val lessons = values[2] as List<LessonModule>
        @Suppress("UNCHECKED_CAST")
        val quests = values[3] as List<DailyQuest>
        @Suppress("UNCHECKED_CAST")
        val badges = values[4] as List<BadgeItem>
        @Suppress("UNCHECKED_CAST")
        val leaderboard = values[5] as List<LeaderboardEntry>
        val syncStatus = values[6] as SyncStatus
        val eph = values[7] as EphemeralState

        MainUiState(
            language = lang,
            activeTab = eph.activeTab,
            subjectFilter = eph.subjectFilter,
            profile = profile,
            lessons = lessons,
            selectedLessonId = eph.selectedLessonId,
            quests = quests,
            badges = badges,
            leaderboard = leaderboard,
            syncStatus = syncStatus,
            quizState = eph.quizState,
            p2pState = eph.p2pState,
            isAudioPlaying = eph.isAudioPlaying,
            selectedBadgeForModal = eph.selectedBadge,
            showDemoHelpersSheet = eph.showDemoSheet,
            toasts = eph.toasts,
            downloadingPackCode = eph.downloadingPackCode
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MainUiState())

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
            when (val res = repository.evaluateStartupStreak()) {
                is StreakEvaluationResult.Frozen -> {
                    pushToast(ToastType.STREAK, str(R.string.toast_streak_frozen))
                }
                is StreakEvaluationResult.Incremented -> {
                    pushToast(ToastType.STREAK, str(R.string.toast_streak_up, res.newStreak))
                }
                else -> Unit
            }
        }
    }

    fun toggleLanguage() {
        viewModelScope.launch {
            audioHelper.playChime()
            repository.toggleLanguage()
        }
    }

    fun navigateToTab(tab: NavTab) {
        audioHelper.stopSpeaking()
        audioHelper.playChime()
        ephemeral.update { it.copy(activeTab = tab, isAudioPlaying = false) }
    }

    fun setSubjectFilter(subject: String) {
        ephemeral.update { it.copy(subjectFilter = subject) }
    }

    fun openLesson(lessonId: String) {
        audioHelper.stopSpeaking()
        viewModelScope.launch { repository.recordLessonViewed(lessonId) }
        ephemeral.update {
            it.copy(
                selectedLessonId = lessonId,
                activeTab = NavTab.LESSON,
                isAudioPlaying = false,
                quizState = QuizUiState()
            )
        }
    }

    fun setReaderQuizMode(isQuiz: Boolean) {
        audioHelper.stopSpeaking()
        ephemeral.update {
            it.copy(
                isAudioPlaying = false,
                quizState = it.quizState.copy(isQuizMode = isQuiz)
            )
        }
    }

    fun toggleLessonAudio() {
        val state = uiState.value
        val lesson = state.activeLesson ?: return
        if (state.isAudioPlaying) {
            audioHelper.stopSpeaking()
            ephemeral.update { it.copy(isAudioPlaying = false) }
        } else {
            val text = if (state.language == AppLanguage.KN) lesson.audioTextKn else lesson.audioTextEn
            ephemeral.update { it.copy(isAudioPlaying = true) }
            audioHelper.speak(text, state.language.code) {
                ephemeral.update { it.copy(isAudioPlaying = false) }
            }
        }
    }

    fun speakKannadaVowel(letter: String, exampleKn: String) {
        audioHelper.playChime()
        audioHelper.speak("$letter, $exampleKn", "kn")
    }

    fun playInteractionChime() = audioHelper.playChime()

    fun playInteractionBuzz() = audioHelper.playBuzz()

    fun selectQuizOption(index: Int) {
        val q = ephemeral.value.quizState
        if (q.answerSubmitted) return
        audioHelper.playChime()
        ephemeral.update { it.copy(quizState = q.copy(selectedOptionIndex = index)) }
    }

    fun confirmQuizAnswer() {
        val lesson = uiState.value.activeLesson ?: return
        val qState = ephemeral.value.quizState
        val selected = qState.selectedOptionIndex ?: return
        val question = lesson.quiz.getOrNull(qState.currentQuestionIndex) ?: return
        val correct = selected == question.correctIndex
        if (correct) audioHelper.playChime() else audioHelper.playBuzz()

        ephemeral.update {
            it.copy(
                quizState = qState.copy(
                    answerSubmitted = true,
                    score = if (correct) qState.score + 1 else qState.score,
                    selectedIndices = qState.selectedIndices + selected
                )
            )
        }
    }

    fun advanceQuiz() {
        val lesson = uiState.value.activeLesson ?: return
        val qState = ephemeral.value.quizState
        if (qState.currentQuestionIndex < lesson.quiz.lastIndex) {
            ephemeral.update {
                it.copy(
                    quizState = qState.copy(
                        currentQuestionIndex = qState.currentQuestionIndex + 1,
                        selectedOptionIndex = null,
                        answerSubmitted = false
                    )
                )
            }
        } else {
            val perfect = qState.score == lesson.quiz.size
            if (perfect) audioHelper.playFanfare() else audioHelper.playChime()
            ephemeral.update { it.copy(quizState = qState.copy(completed = true)) }
            viewModelScope.launch {
                val outcome = repository.completeQuiz(lesson, qState.score, qState.selectedIndices)
                if (outcome.xpEarned > 0) {
                    pushToast(
                        ToastType.QUIZ,
                        str(R.string.toast_quiz_xp, outcome.xpEarned),
                        "${qState.score}/${lesson.quiz.size}"
                    )
                }
                outcome.leveledUpTo?.let { lvl ->
                    delay(300)
                    audioHelper.playFanfare()
                    pushToast(ToastType.LEVEL, str(R.string.toast_level_up, lvl))
                }
            }
        }
    }

    fun retakeQuiz() {
        ephemeral.update { it.copy(quizState = QuizUiState(isQuizMode = true)) }
    }

    fun claimQuest(questId: String) {
        viewModelScope.launch {
            val outcome = repository.claimQuest(questId) ?: return@launch
            audioHelper.playChime()
            val title = if (uiState.value.language == AppLanguage.KN) outcome.questTitleKn else outcome.questTitleEn
            pushToast(ToastType.QUEST, str(R.string.toast_quest_claimed, outcome.xpEarned), title)
            outcome.leveledUpTo?.let { lvl ->
                delay(300)
                audioHelper.playFanfare()
                pushToast(ToastType.LEVEL, str(R.string.toast_level_up, lvl))
            }
        }
    }

    fun selectP2PLesson(lessonId: String) {
        ephemeral.update {
            it.copy(p2pState = it.p2pState.copy(selectedLessonId = lessonId, transferDone = false))
        }
    }

    fun startP2PScan() {
        if (ephemeral.value.p2pState.isScanning) return
        viewModelScope.launch {
            audioHelper.playChime()
            ephemeral.update {
                it.copy(
                    p2pState = it.p2pState.copy(
                        isScanning = true,
                        discoveredPeers = emptyList(),
                        selectedPeerId = null,
                        transferDone = false
                    )
                )
            }
            meshManager.discoverNearbyPeers().collect { peers ->
                audioHelper.playChime()
                val done = peers.size >= 4 || peers.any { it.isRealWifiDirect }
                ephemeral.update {
                    it.copy(
                        p2pState = it.p2pState.copy(
                            discoveredPeers = peers,
                            isScanning = !done,
                            selectedPeerId = if (done) peers.firstOrNull()?.id else it.p2pState.selectedPeerId
                        )
                    )
                }
            }
            ephemeral.update { it.copy(p2pState = it.p2pState.copy(isScanning = false)) }
        }
    }

    fun selectP2PPeer(peerId: String) {
        ephemeral.update {
            it.copy(p2pState = it.p2pState.copy(selectedPeerId = peerId, transferDone = false))
        }
    }

    fun startP2PTransfer() {
        val p2p = ephemeral.value.p2pState
        if (p2p.isTransferring) return
        val peer = p2p.discoveredPeers.firstOrNull { it.id == p2p.selectedPeerId } ?: return
        val lesson = uiState.value.lessons.firstOrNull { it.id == p2p.selectedLessonId } ?: return

        viewModelScope.launch {
            ephemeral.update {
                it.copy(
                    p2pState = it.p2pState.copy(
                        isTransferring = true,
                        transferProgress = 0,
                        transferDone = false
                    )
                )
            }
            val sha = meshManager.transferPackageAndVerify(lesson, peer) { pct ->
                ephemeral.update { it.copy(p2pState = it.p2pState.copy(transferProgress = pct)) }
            }
            val outcome = repository.recordP2PTransfer(lesson, peer, sha)
            audioHelper.playFanfare()
            ephemeral.update {
                it.copy(
                    p2pState = it.p2pState.copy(
                        isTransferring = false,
                        transferProgress = 100,
                        transferDone = true,
                        verifiedSha256 = sha
                    )
                )
            }
            val lessonName = if (uiState.value.language == AppLanguage.KN) lesson.titleKn else lesson.titleEn
            pushToast(ToastType.P2P, str(R.string.toast_p2p_done), "$lessonName → ${peer.deviceName}")
            outcome.leveledUpTo?.let { lvl ->
                delay(300)
                pushToast(ToastType.LEVEL, str(R.string.toast_level_up, lvl))
            }
        }
    }

    fun openBadgeModal(badge: BadgeItem) {
        audioHelper.playChime()
        ephemeral.update { it.copy(selectedBadge = badge) }
    }

    fun closeBadgeModal() {
        ephemeral.update { it.copy(selectedBadge = null) }
    }

    fun setDemoSheetVisible(visible: Boolean) {
        if (visible) audioHelper.playChime()
        ephemeral.update { it.copy(showDemoSheet = visible) }
    }

    fun simulateStreak(daysAgo: Int) {
        viewModelScope.launch {
            when (val res = repository.simulateStreak(daysAgo)) {
                is StreakEvaluationResult.Incremented -> {
                    audioHelper.playChime()
                    pushToast(ToastType.STREAK, str(R.string.toast_streak_up, res.newStreak))
                }
                is StreakEvaluationResult.Frozen -> {
                    audioHelper.playChime()
                    pushToast(ToastType.STREAK, str(R.string.toast_streak_frozen))
                }
                is StreakEvaluationResult.Reset -> {
                    audioHelper.playBuzz()
                    pushToast(ToastType.STREAK, str(R.string.toast_streak_reset))
                }
                else -> Unit
            }
        }
    }

    fun addDemoXp(delta: Int = 50) {
        viewModelScope.launch {
            audioHelper.playChime()
            val leveledUp = repository.addDemoXp(delta)
            if (leveledUp != null) {
                audioHelper.playFanfare()
                pushToast(ToastType.LEVEL, str(R.string.toast_level_up, leveledUp))
            } else {
                pushToast(ToastType.QUEST, str(R.string.toast_quiz_xp, delta))
            }
        }
    }

    fun resetDemoData() {
        viewModelScope.launch {
            repository.resetDemoData()
            ephemeral.update { it.copy(showDemoSheet = false) }
            pushToast(ToastType.INFO, str(R.string.toast_reset_done))
        }
    }

    fun triggerManualSync() {
        viewModelScope.launch {
            repository.syncWithBackend()
        }
    }

    fun downloadPack(packCode: String) {
        viewModelScope.launch {
            ephemeral.update { it.copy(downloadingPackCode = packCode) }
            repository.downloadPack(packCode)
            ephemeral.update { it.copy(downloadingPackCode = null) }
        }
    }

    fun dismissToast(id: String) {
        ephemeral.update { it.copy(toasts = it.toasts.filterNot { t -> t.id == id }) }
    }

    private fun pushToast(type: ToastType, title: String, description: String? = null) {
        val toast = AppToast(id = UUID.randomUUID().toString(), type = type, title = title, description = description)
        ephemeral.update { it.copy(toasts = it.toasts + toast) }
        viewModelScope.launch {
            delay(4000)
            dismissToast(toast.id)
        }
    }

    private fun str(@StringRes resId: Int, vararg args: Any): String {
        val lang = uiState.value.language.code
        val config = Configuration(context.resources.configuration).apply {
            setLocale(Locale(lang))
        }
        return context.createConfigurationContext(config).getString(resId, *args)
    }

    override fun onCleared() {
        super.onCleared()
        audioHelper.release()
    }
}
