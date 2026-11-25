package com.dbit.nannashikshana.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.WifiTethering
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.dbit.nannashikshana.R
import com.dbit.nannashikshana.domain.model.AppLanguage
import com.dbit.nannashikshana.domain.model.BadgeItem
import com.dbit.nannashikshana.domain.model.LeaderboardEntry
import com.dbit.nannashikshana.ui.theme.NannaAmber
import com.dbit.nannashikshana.ui.theme.NannaAmberSoft
import com.dbit.nannashikshana.ui.theme.NannaBorder
import com.dbit.nannashikshana.ui.theme.NannaEmeraldDark
import com.dbit.nannashikshana.ui.theme.NannaEmeraldSoft
import com.dbit.nannashikshana.ui.theme.NannaPurple
import com.dbit.nannashikshana.ui.theme.NannaPurpleSoft
import com.dbit.nannashikshana.ui.theme.NannaSky
import com.dbit.nannashikshana.ui.theme.NannaSkyDark
import com.dbit.nannashikshana.ui.theme.NannaSkySoft
import com.dbit.nannashikshana.ui.theme.NannaSlate400
import com.dbit.nannashikshana.ui.theme.NannaSlate500
import com.dbit.nannashikshana.ui.theme.NannaSlate600
import com.dbit.nannashikshana.ui.theme.NannaSlate800
import com.dbit.nannashikshana.ui.theme.NannaSlate900
import com.dbit.nannashikshana.ui.theme.NannaSurface
import com.dbit.nannashikshana.ui.theme.NannaSurfaceMuted

@Composable
fun HallOfFameScreen(
    leaderboard: List<LeaderboardEntry>,
    badges: List<BadgeItem>,
    language: AppLanguage,
    selectedBadge: BadgeItem?,
    onSelectBadge: (BadgeItem) -> Unit,
    onDismissBadge: () -> Unit
) {
    val unlockedCount = badges.count { it.unlocked }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 92.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(NannaSlate900, NannaSlate800, Color(0xFF2E1065))
                        )
                    )
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.EmojiEvents,
                            contentDescription = null,
                            tint = NannaAmber,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = stringResource(R.string.leaderboard_title),
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Text(
                        text = stringResource(R.string.leaderboard_subtitle),
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Leaderboard Table Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NannaSurface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, NannaBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.rank_student_header),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NannaSlate500,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = stringResource(R.string.streak_col),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NannaSlate500,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        Text(
                            text = stringResource(R.string.xp_col),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NannaSlate500,
                            textAlign = TextAlign.End
                        )
                    }
                    HorizontalDivider(color = NannaSurfaceMuted)

                    leaderboard.forEach { entry ->
                        Surface(
                            color = if (entry.isUser) NannaSkySoft else Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(
                                if (entry.isUser) 1.5.dp else 1.dp,
                                if (entry.isUser) NannaSkyDark else NannaBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    val medal = when (entry.rank) {
                                        1 -> "🥇"
                                        2 -> "🥈"
                                        3 -> "🥉"
                                        else -> "#${entry.rank}"
                                    }
                                    Text(
                                        text = medal,
                                        fontSize = if (entry.rank <= 3) 18.sp else 13.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Black,
                                        color = NannaSlate600
                                    )
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = entry.name,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = NannaSlate900,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            if (entry.isUser) {
                                                Surface(
                                                    color = NannaSkyDark,
                                                    shape = RoundedCornerShape(6.dp)
                                                ) {
                                                    Text(
                                                        text = stringResource(R.string.you_tag),
                                                        color = Color.White,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Black,
                                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = entry.village,
                                            fontSize = 11.sp,
                                            color = NannaSlate500,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Surface(
                                    color = NannaAmberSoft,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                ) {
                                    Text(
                                        text = "🔥 ${stringResource(R.string.streak_days_short, entry.streak)}",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFB45309),
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                    )
                                }

                                Text(
                                    text = stringResource(R.string.xp_unit, entry.totalXp),
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    color = if (entry.isUser) NannaSkyDark else NannaSlate800
                                )
                            }
                        }
                    }
                }
            }
        }

        // Achievement Badges & Rewards Hall
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NannaSurface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, NannaBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.badges_title),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NannaSlate900,
                            modifier = Modifier.weight(1f)
                        )
                        Surface(
                            color = NannaPurpleSoft,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.badges_progress, unlockedCount, badges.size),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NannaPurple,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    val rows = badges.chunked(2)
                    rows.forEach { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            pair.forEach { badge ->
                                BadgeGridCard(
                                    badge = badge,
                                    language = language,
                                    onClick = { onSelectBadge(badge) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (pair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedBadge != null) {
        BadgeDetailDialog(
            badge = selectedBadge,
            language = language,
            onDismiss = onDismissBadge
        )
    }
}

@Composable
private fun BadgeGridCard(
    badge: BadgeItem,
    language: AppLanguage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rarityColors = badgeRarityColors(badge.rarity)
    Surface(
        color = if (badge.unlocked) rarityColors.first else NannaSurfaceMuted,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (badge.unlocked) rarityColors.second.copy(alpha = 0.45f) else NannaBorder),
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .alpha(if (badge.unlocked) 1f else 0.72f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (badge.unlocked) rarityColors.second.copy(alpha = 0.16f) else NannaBorder),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = badgeIconFor(badge.icon),
                    contentDescription = null,
                    tint = if (badge.unlocked) rarityColors.second else NannaSlate400,
                    modifier = Modifier.size(22.dp)
                )
            }
            Text(
                text = if (language == AppLanguage.KN) badge.nameKn else badge.nameEn,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = NannaSlate900,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = if (badge.unlocked) Icons.Rounded.CheckCircle else Icons.Rounded.Lock,
                    contentDescription = null,
                    tint = if (badge.unlocked) NannaEmeraldDark else NannaSlate500,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = stringResource(if (badge.unlocked) R.string.unlocked_badge else R.string.locked_badge),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (badge.unlocked) NannaEmeraldDark else NannaSlate500
                )
            }
        }
    }
}

@Composable
private fun BadgeDetailDialog(
    badge: BadgeItem,
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    val rarityColors = badgeRarityColors(badge.rarity)
    val rarityLabel = stringResource(
        when (badge.rarity.lowercase()) {
            "bronze" -> R.string.rarity_bronze
            "silver" -> R.string.rarity_silver
            "gold" -> R.string.rarity_gold
            else -> R.string.rarity_platinum
        }
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = NannaSurface,
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, NannaBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = stringResource(R.string.close_cd),
                            tint = NannaSlate600
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(rarityColors.first),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = badgeIconFor(badge.icon),
                        contentDescription = null,
                        tint = rarityColors.second,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Text(
                    text = if (language == AppLanguage.KN) badge.nameKn else badge.nameEn,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = NannaSlate900,
                    textAlign = TextAlign.Center
                )

                Surface(
                    color = rarityColors.first,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = stringResource(R.string.rarity_badge, rarityLabel),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = rarityColors.second,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = if (language == AppLanguage.KN) badge.descriptionKn else badge.descriptionEn,
                    fontSize = 13.sp,
                    color = NannaSlate600,
                    textAlign = TextAlign.Center
                )

                Surface(
                    color = if (badge.unlocked) NannaEmeraldSoft else NannaSurfaceMuted,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (badge.unlocked) Color(0xFFA7F3D0) else NannaBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (badge.unlocked) Icons.Rounded.CheckCircle else Icons.Rounded.Lock,
                            contentDescription = null,
                            tint = if (badge.unlocked) NannaEmeraldDark else NannaSlate500,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = stringResource(if (badge.unlocked) R.string.unlocked_badge else R.string.locked_badge),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (badge.unlocked) NannaEmeraldDark else NannaSlate500
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

private fun badgeIconFor(iconKey: String): ImageVector = when (iconKey) {
    "BookOpen" -> Icons.Rounded.MenuBook
    "Flame" -> Icons.Rounded.LocalFireDepartment
    "Radio" -> Icons.Rounded.WifiTethering
    "Share2" -> Icons.Rounded.Share
    "Divide" -> Icons.Rounded.Calculate
    "Zap" -> Icons.Rounded.Bolt
    else -> Icons.Rounded.AutoAwesome
}

private fun badgeRarityColors(rarity: String): Pair<Color, Color> = when (rarity.lowercase()) {
    "bronze" -> Color(0xFFFFF7ED) to Color(0xFFC2410C)
    "silver" -> Color(0xFFF1F5F9) to Color(0xFF475569)
    "gold" -> NannaAmberSoft to Color(0xFFB45309)
    else -> NannaPurpleSoft to NannaPurple
}
