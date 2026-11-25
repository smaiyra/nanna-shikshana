package com.dbit.nannashikshana.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dbit.nannashikshana.R
import com.dbit.nannashikshana.domain.gamification.GamificationEngine
import com.dbit.nannashikshana.domain.model.AppLanguage
import com.dbit.nannashikshana.domain.model.DailyQuest
import com.dbit.nannashikshana.domain.model.LessonModule
import com.dbit.nannashikshana.domain.model.StudentProfile
import com.dbit.nannashikshana.domain.model.SyncState
import com.dbit.nannashikshana.domain.model.SyncStatus
import com.dbit.nannashikshana.ui.theme.NannaAmber
import com.dbit.nannashikshana.ui.theme.NannaAmberSoft
import com.dbit.nannashikshana.ui.theme.NannaBorder
import com.dbit.nannashikshana.ui.theme.NannaEmerald
import com.dbit.nannashikshana.ui.theme.NannaEmeraldDark
import com.dbit.nannashikshana.ui.theme.NannaEmeraldSoft
import com.dbit.nannashikshana.ui.theme.NannaPurple
import com.dbit.nannashikshana.ui.theme.NannaSky
import com.dbit.nannashikshana.ui.theme.NannaSkyDark
import com.dbit.nannashikshana.ui.theme.NannaSlate400
import com.dbit.nannashikshana.ui.theme.NannaSlate500
import com.dbit.nannashikshana.ui.theme.NannaSlate600
import com.dbit.nannashikshana.ui.theme.NannaSlate800
import com.dbit.nannashikshana.ui.theme.NannaSlate900
import com.dbit.nannashikshana.ui.theme.NannaSlate950
import com.dbit.nannashikshana.ui.theme.NannaSurface
import com.dbit.nannashikshana.ui.theme.NannaSurfaceMuted

@Composable
fun HomeDashboardScreen(
    profile: StudentProfile,
    lessons: List<LessonModule>,
    quests: List<DailyQuest>,
    language: AppLanguage,
    subjectFilter: String,
    syncStatus: SyncStatus,
    downloadingPackCode: String?,
    onSelectFilter: (String) -> Unit,
    onSelectLesson: (String) -> Unit,
    onClaimQuest: (String) -> Unit,
    onManualSync: () -> Unit,
    onDownloadPack: (String) -> Unit
) {
    val levelInfo = GamificationEngine.calculateLevelInfo(profile.totalXp)
    val animatedProgress by animateFloatAsState(
        targetValue = (levelInfo.progressPct / 100f).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 600),
        label = "xp_progress"
    )
    val filteredLessons = lessons.filter { subjectFilter == "all" || it.subject == subjectFilter }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 92.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Offline / Sync status strip
        item {
            Surface(
                color = NannaSlate900,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.WifiOff,
                            contentDescription = null,
                            tint = NannaEmerald,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = stringResource(R.string.offline_mode),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = onManualSync)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        val syncText = when (syncStatus.state) {
                            SyncState.SYNCING -> stringResource(R.string.sync_status_syncing)
                            SyncState.SYNCED -> stringResource(R.string.sync_status_synced)
                            SyncState.OFFLINE -> stringResource(R.string.sync_status_offline, syncStatus.pendingEventsCount)
                        }
                        Text(
                            text = syncText,
                            color = NannaSlate400,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Icon(
                            imageVector = Icons.Rounded.Sync,
                            contentDescription = stringResource(R.string.sync_now),
                            tint = NannaSky,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // Gamification Hero Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(NannaSlate900, NannaSlate800, Color(0xFF082F49))
                        )
                    )
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color.White.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = "🔥", fontSize = 18.sp)
                                Text(
                                    text = "${profile.streakCount}",
                                    color = NannaAmber,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = stringResource(R.string.streak_title),
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Surface(
                            color = Color.White.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Shield,
                                    contentDescription = null,
                                    tint = Color(0xFF7DD3FC),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "${profile.streakFreezesLeft} ${stringResource(R.string.streak_freezes)}",
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.EmojiEvents,
                                contentDescription = null,
                                tint = NannaPurple,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (language == AppLanguage.KN) levelInfo.titleKn else levelInfo.titleEn,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = stringResource(R.string.xp_unit, profile.totalXp),
                            color = NannaSky,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(NannaSlate950.copy(alpha = 0.7f))
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedProgress)
                                .height(8.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(NannaSky, Color(0xFF6366F1), NannaPurple)
                                    )
                                )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.progress_pct, levelInfo.progressPct.toInt()),
                            color = NannaSlate400,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = if (levelInfo.neededXp > 0) {
                                stringResource(R.string.to_next_level, levelInfo.neededXp)
                            } else {
                                stringResource(R.string.max_rank_attained)
                            },
                            color = NannaSlate400,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Daily Quests Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NannaSurface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, NannaBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Bolt,
                                contentDescription = null,
                                tint = NannaAmber,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = stringResource(R.string.daily_quests),
                                style = MaterialTheme.typography.titleSmall,
                                color = NannaSlate900
                            )
                        }
                        Text(
                            text = stringResource(R.string.resets_daily),
                            fontSize = 10.sp,
                            color = NannaSlate500
                        )
                    }

                    quests.forEach { quest ->
                        val isDone = quest.progress >= quest.maxProgress
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, NannaBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isDone) NannaEmeraldSoft else NannaSurfaceMuted),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isDone) {
                                        Icon(
                                            imageVector = Icons.Rounded.Check,
                                            contentDescription = null,
                                            tint = NannaEmeraldDark,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "${quest.progress}/${quest.maxProgress}",
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = NannaSlate600
                                        )
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (language == AppLanguage.KN) quest.titleKn else quest.titleEn,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NannaSlate900,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${if (language == AppLanguage.KN) quest.subKn else quest.subEn} · +${quest.xpReward} XP",
                                        fontSize = 11.sp,
                                        color = NannaSlate500,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                when {
                                    quest.claimed -> {
                                        Surface(
                                            color = NannaEmeraldSoft,
                                            shape = RoundedCornerShape(10.dp),
                                            border = BorderStroke(1.dp, Color(0xFFA7F3D0))
                                        ) {
                                            Text(
                                                text = stringResource(R.string.claimed),
                                                color = NannaEmeraldDark,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                    isDone -> {
                                        Button(
                                            onClick = { onClaimQuest(quest.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = NannaAmber),
                                            shape = RoundedCornerShape(12.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                            modifier = Modifier.height(40.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Bolt,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            Text(
                                                text = stringResource(R.string.claim_xp),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    else -> {
                                        Text(
                                            text = "${quest.progress}/${quest.maxProgress}",
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = NannaSlate400
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Curriculum Header + Filter Row
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.MenuBook,
                            contentDescription = null,
                            tint = NannaSkyDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = stringResource(R.string.curriculum_feed),
                            style = MaterialTheme.typography.titleSmall,
                            color = NannaSlate900
                        )
                    }
                    Text(
                        text = stringResource(R.string.modules_count, filteredLessons.size),
                        fontSize = 12.sp,
                        color = NannaSlate500
                    )
                }

                val filters = listOf(
                    "all" to R.string.filter_all,
                    "math" to R.string.filter_math,
                    "science" to R.string.filter_science,
                    "kannada" to R.string.filter_kannada,
                    "social" to R.string.filter_social
                )
                Surface(
                    color = NannaSurfaceMuted,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        filters.forEach { (id, labelRes) ->
                            val selected = subjectFilter == id
                            Surface(
                                color = if (selected) NannaSurface else Color.Transparent,
                                shape = RoundedCornerShape(12.dp),
                                shadowElevation = if (selected) 1.dp else 0.dp,
                                modifier = Modifier
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onSelectFilter(id) }
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(horizontal = 14.dp)
                                ) {
                                    Text(
                                        text = stringResource(labelRes),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) NannaSlate900 else NannaSlate600
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Lesson Cards
        items(filteredLessons, key = { it.id }) { lesson ->
            val completed = profile.completedLessons.contains(lesson.id)
            val quizScore = profile.completedQuizzes[lesson.id]
            Card(
                colors = CardDefaults.cardColors(containerColor = NannaSurface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, NannaBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (language == AppLanguage.KN) lesson.chapterKn else lesson.chapterEn,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NannaSkyDark
                            )
                            Text(text = "·", color = NannaSlate400)
                            Text(text = lesson.grade, fontSize = 12.sp, color = NannaSlate500)
                        }
                        Surface(
                            color = NannaSurfaceMuted,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, NannaBorder)
                        ) {
                            Text(
                                text = "${lesson.fileSizeMb} MB",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold,
                                color = NannaSlate600,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Text(
                        text = if (language == AppLanguage.KN) lesson.titleKn else lesson.titleEn,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = NannaSlate900
                    )

                    Text(
                        text = if (language == AppLanguage.KN) lesson.descriptionKn else lesson.descriptionEn,
                        fontSize = 12.sp,
                        color = NannaSlate500,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    HorizontalDivider(color = NannaSurfaceMuted)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (completed) {
                                Surface(
                                    color = NannaEmeraldSoft,
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, Color(0xFFA7F3D0))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.CheckCircle,
                                            contentDescription = null,
                                            tint = NannaEmeraldDark,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        val scoreLabel = if (quizScore != null) " ($quizScore/3)" else ""
                                        Text(
                                            text = "${stringResource(R.string.completed_lesson)}$scoreLabel",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NannaEmeraldDark
                                        )
                                    }
                                }
                            } else {
                                Text(
                                    text = stringResource(R.string.vector_ready),
                                    fontSize = 11.sp,
                                    color = NannaSlate400,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (downloadingPackCode == lesson.packCode) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = NannaSkyDark
                                )
                            } else {
                                OutlinedButton(
                                    onClick = { onDownloadPack(lesson.packCode) },
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                                    modifier = Modifier.height(44.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.CloudDownload,
                                        contentDescription = stringResource(R.string.download_pack),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Button(
                                onClick = { onSelectLesson(lesson.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = NannaSkyDark),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                modifier = Modifier.height(44.dp)
                            ) {
                                Text(
                                    text = if (completed) stringResource(R.string.resume_lesson) else stringResource(R.string.start_lesson),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
