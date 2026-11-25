package com.dbit.nannashikshana.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dbit.nannashikshana.R
import com.dbit.nannashikshana.domain.model.AppToast
import com.dbit.nannashikshana.domain.model.ToastType
import com.dbit.nannashikshana.ui.screens.CreditsScreen
import com.dbit.nannashikshana.ui.screens.DemoHelpersBottomSheet
import com.dbit.nannashikshana.ui.screens.HallOfFameScreen
import com.dbit.nannashikshana.ui.screens.HomeDashboardScreen
import com.dbit.nannashikshana.ui.screens.LessonReaderScreen
import com.dbit.nannashikshana.ui.screens.MeshP2PScreen
import com.dbit.nannashikshana.ui.theme.NannaAmber
import com.dbit.nannashikshana.ui.theme.NannaBg
import com.dbit.nannashikshana.ui.theme.NannaBorder
import com.dbit.nannashikshana.ui.theme.NannaEmeraldDark
import com.dbit.nannashikshana.ui.theme.NannaEmeraldSoft
import com.dbit.nannashikshana.ui.theme.NannaPurple
import com.dbit.nannashikshana.ui.theme.NannaSkyDark
import com.dbit.nannashikshana.ui.theme.NannaSkySoft
import com.dbit.nannashikshana.ui.theme.NannaSlate500
import com.dbit.nannashikshana.ui.theme.NannaSlate600
import com.dbit.nannashikshana.ui.theme.NannaSlate900
import com.dbit.nannashikshana.ui.theme.NannaSurface
import com.dbit.nannashikshana.ui.viewmodel.MainUiState
import com.dbit.nannashikshana.ui.viewmodel.MainViewModel
import com.dbit.nannashikshana.ui.viewmodel.NavTab

@Composable
fun NannaMainScaffold(
    uiState: MainUiState,
    viewModel: MainViewModel
) {
    val profile = uiState.profile

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = NannaBg,
            topBar = {
                NannaTopBar(
                    onToggleLanguage = viewModel::toggleLanguage
                )
            },
            bottomBar = {
                NannaBottomBar(
                    activeTab = uiState.activeTab,
                    onSelectTab = viewModel::navigateToTab
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.setDemoSheetVisible(true) },
                    containerColor = NannaSlate900,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Build,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = stringResource(R.string.demo_tools_fab),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        ) { innerPadding: PaddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (uiState.activeTab) {
                    NavTab.HOME -> {
                        if (profile != null) {
                            HomeDashboardScreen(
                                profile = profile,
                                lessons = uiState.lessons,
                                quests = uiState.quests,
                                language = uiState.language,
                                subjectFilter = uiState.subjectFilter,
                                syncStatus = uiState.syncStatus,
                                downloadingPackCode = uiState.downloadingPackCode,
                                onSelectFilter = viewModel::setSubjectFilter,
                                onSelectLesson = viewModel::openLesson,
                                onClaimQuest = viewModel::claimQuest,
                                onManualSync = viewModel::triggerManualSync,
                                onDownloadPack = viewModel::downloadPack
                            )
                        }
                    }

                    NavTab.LESSON -> {
                        val lesson = uiState.activeLesson
                        if (lesson != null) {
                            LessonReaderScreen(
                                lesson = lesson,
                                language = uiState.language,
                                quizState = uiState.quizState,
                                isAudioPlaying = uiState.isAudioPlaying,
                                onBack = { viewModel.navigateToTab(NavTab.HOME) },
                                onModeChange = viewModel::setReaderQuizMode,
                                onToggleAudio = viewModel::toggleLessonAudio,
                                onSpeakVowel = viewModel::speakKannadaVowel,
                                onChime = viewModel::playInteractionChime,
                                onBuzz = viewModel::playInteractionBuzz,
                                onSelectQuizOption = viewModel::selectQuizOption,
                                onConfirmQuizAnswer = viewModel::confirmQuizAnswer,
                                onAdvanceQuiz = viewModel::advanceQuiz,
                                onRetakeQuiz = viewModel::retakeQuiz
                            )
                        }
                    }

                    NavTab.P2P -> {
                        MeshP2PScreen(
                            lessons = uiState.lessons,
                            language = uiState.language,
                            p2pState = uiState.p2pState,
                            onSelectLesson = viewModel::selectP2PLesson,
                            onStartScan = viewModel::startP2PScan,
                            onSelectPeer = viewModel::selectP2PPeer,
                            onStartTransfer = viewModel::startP2PTransfer
                        )
                    }

                    NavTab.LEADERBOARD -> {
                        HallOfFameScreen(
                            leaderboard = uiState.leaderboard,
                            badges = uiState.badges,
                            language = uiState.language,
                            selectedBadge = uiState.selectedBadgeForModal,
                            onSelectBadge = viewModel::openBadgeModal,
                            onDismissBadge = viewModel::closeBadgeModal
                        )
                    }

                    NavTab.ABOUT -> {
                        CreditsScreen()
                    }
                }
            }
        }

        // Top Toast Overlay
        ToastOverlay(
            toasts = uiState.toasts,
            onDismiss = viewModel::dismissToast,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 64.dp, start = 16.dp, end = 16.dp)
        )

        if (uiState.showDemoHelpersSheet && profile != null) {
            DemoHelpersBottomSheet(
                profile = profile,
                onSimulateStreak = viewModel::simulateStreak,
                onAddXp = { viewModel.addDemoXp(50) },
                onResetDemoData = viewModel::resetDemoData,
                onDismiss = { viewModel.setDemoSheetVisible(false) }
            )
        }
    }
}

@Composable
private fun NannaTopBar(onToggleLanguage: () -> Unit) {
    Surface(
        color = NannaSurface,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.statusBarsPadding()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        color = NannaSkyDark,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.School,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.app_name),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = NannaSlate900,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = stringResource(R.string.app_sub),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NannaSkyDark,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = stringResource(R.string.institution),
                            fontSize = 11.sp,
                            color = NannaSlate500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(Modifier.width(8.dp))

                Surface(
                    color = NannaSkySoft,
                    shape = RoundedCornerShape(999.dp),
                    border = BorderStroke(1.dp, Color(0xFFBAE6FD)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .clickable(onClick = onToggleLanguage)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Language,
                            contentDescription = null,
                            tint = NannaSkyDark,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = stringResource(R.string.lang_toggle_button),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NannaSkyDark
                        )
                    }
                }
            }
            HorizontalDivider(color = NannaBorder)
        }
    }
}

private data class BottomTabItem(
    val tab: NavTab,
    val labelRes: Int,
    val icon: ImageVector
)

@Composable
private fun NannaBottomBar(
    activeTab: NavTab,
    onSelectTab: (NavTab) -> Unit
) {
    val tabs = listOf(
        BottomTabItem(NavTab.HOME, R.string.tab_home, Icons.Rounded.Home),
        BottomTabItem(NavTab.LESSON, R.string.tab_learn, Icons.Rounded.AutoStories),
        BottomTabItem(NavTab.P2P, R.string.tab_p2p, Icons.Rounded.Wifi),
        BottomTabItem(NavTab.LEADERBOARD, R.string.tab_rank, Icons.Rounded.EmojiEvents),
        BottomTabItem(NavTab.ABOUT, R.string.tab_about, Icons.Rounded.Info)
    )

    Column {
        HorizontalDivider(color = NannaBorder)
        NavigationBar(
            containerColor = NannaSurface,
            tonalElevation = 0.dp
        ) {
            tabs.forEach { item ->
                val selected = activeTab == item.tab
                NavigationBarItem(
                    selected = selected,
                    onClick = { onSelectTab(item.tab) },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = stringResource(item.labelRes),
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(item.labelRes),
                            fontSize = 10.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NannaSkyDark,
                        selectedTextColor = NannaSkyDark,
                        indicatorColor = NannaSkySoft,
                        unselectedIconColor = NannaSlate500,
                        unselectedTextColor = NannaSlate600
                    )
                )
            }
        }
    }
}

@Composable
private fun ToastOverlay(
    toasts: List<AppToast>,
    onDismiss: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        toasts.forEach { toast ->
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically { -it } + fadeIn(),
                exit = slideOutVertically { -it } + fadeOut()
            ) {
                val accent = when (toast.type) {
                    ToastType.QUEST -> NannaAmber
                    ToastType.QUIZ -> NannaEmeraldDark
                    ToastType.P2P -> NannaSkyDark
                    ToastType.STREAK -> NannaAmber
                    ToastType.LEVEL -> NannaPurple
                    ToastType.INFO -> NannaSlate900
                }
                val icon = when (toast.type) {
                    ToastType.QUEST -> Icons.Rounded.Bolt
                    ToastType.QUIZ -> Icons.Rounded.CheckCircle
                    ToastType.P2P -> Icons.Rounded.Wifi
                    ToastType.STREAK -> Icons.Rounded.LocalFireDepartment
                    ToastType.LEVEL -> Icons.Rounded.EmojiEvents
                    ToastType.INFO -> Icons.Rounded.Info
                }
                Surface(
                    color = NannaSlate900,
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 6.dp,
                    border = BorderStroke(1.dp, accent.copy(alpha = 0.55f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onDismiss(toast.id) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = accent.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = accent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = toast.title,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (!toast.description.isNullOrBlank()) {
                                Text(
                                    text = toast.description,
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        IconButton(
                            onClick = { onDismiss(toast.id) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = stringResource(R.string.close_cd),
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
