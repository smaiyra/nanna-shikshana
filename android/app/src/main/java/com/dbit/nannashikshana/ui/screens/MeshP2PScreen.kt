package com.dbit.nannashikshana.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.material.icons.rounded.FolderZip
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.SignalCellularAlt
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dbit.nannashikshana.R
import com.dbit.nannashikshana.domain.model.AppLanguage
import com.dbit.nannashikshana.domain.model.LessonModule
import com.dbit.nannashikshana.ui.theme.NannaAmber
import com.dbit.nannashikshana.ui.theme.NannaAmberSoft
import com.dbit.nannashikshana.ui.theme.NannaBorder
import com.dbit.nannashikshana.ui.theme.NannaEmerald
import com.dbit.nannashikshana.ui.theme.NannaEmeraldDark
import com.dbit.nannashikshana.ui.theme.NannaEmeraldSoft
import com.dbit.nannashikshana.ui.theme.NannaPurple
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
import com.dbit.nannashikshana.ui.viewmodel.P2PUiState

@Composable
fun MeshP2PScreen(
    lessons: List<LessonModule>,
    language: AppLanguage,
    p2pState: P2PUiState,
    onSelectLesson: (String) -> Unit,
    onStartScan: () -> Unit,
    onSelectPeer: (String) -> Unit,
    onStartTransfer: () -> Unit
) {
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
                            colors = listOf(NannaSlate900, NannaSlate800, Color(0xFF082F49))
                        )
                    )
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = NannaSky.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Wifi,
                                contentDescription = null,
                                tint = NannaSky,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(20.dp)
                            )
                        }
                        Text(
                            text = stringResource(R.string.p2p_title),
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Text(
                        text = stringResource(R.string.p2p_subtitle),
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp
                    )
                    Surface(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Speed,
                                contentDescription = null,
                                tint = NannaEmerald,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = stringResource(R.string.transfer_speed),
                                color = NannaEmerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Step 1: Select Lesson Package
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
                    Text(
                        text = stringResource(R.string.step_1),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NannaSlate900
                    )
                    lessons.forEach { lesson ->
                        val selected = p2pState.selectedLessonId == lesson.id
                        Surface(
                            color = if (selected) NannaSkySoft else Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.5.dp, if (selected) NannaSkyDark else NannaBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onSelectLesson(lesson.id) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.FolderZip,
                                        contentDescription = null,
                                        tint = if (selected) NannaSkyDark else NannaSlate500,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Column {
                                        Text(
                                            text = if (language == AppLanguage.KN) lesson.titleKn else lesson.titleEn,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NannaSlate900,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = lesson.packageName,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = NannaSlate500,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                                Surface(
                                    color = NannaSurface,
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, NannaBorder)
                                ) {
                                    Text(
                                        text = "${lesson.fileSizeMb} MB",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = NannaSlate600,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Step 2: Discover Nearby Peer Phones
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
                    Text(
                        text = stringResource(R.string.step_2),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NannaSlate900
                    )

                    if (p2pState.isScanning) {
                        RadarWavePanel()
                    }

                    if (p2pState.discoveredPeers.isNotEmpty()) {
                        Text(
                            text = stringResource(R.string.found_peers, p2pState.discoveredPeers.size),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NannaEmeraldDark
                        )
                        p2pState.discoveredPeers.forEach { peer ->
                            val isSelected = p2pState.selectedPeerId == peer.id
                            Surface(
                                color = if (isSelected) NannaEmeraldSoft else Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(
                                    1.5.dp,
                                    if (isSelected) NannaEmeraldDark else NannaBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { onSelectPeer(peer.id) }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) NannaEmerald else NannaSkySoft),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Devices,
                                                contentDescription = null,
                                                tint = if (isSelected) Color.White else NannaSkyDark,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                            Text(
                                                text = "${peer.deviceName} · ${peer.deviceModel}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = NannaSlate900,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Text(
                                                    text = stringResource(
                                                        R.string.p2p_distance_battery,
                                                        peer.distanceM,
                                                        peer.batteryPct
                                                    ),
                                                    fontSize = 11.sp,
                                                    color = NannaSlate500
                                                )
                                                Surface(
                                                    color = if (peer.isRealWifiDirect) NannaSkySoft else NannaSurfaceMuted,
                                                    shape = RoundedCornerShape(6.dp)
                                                ) {
                                                    Text(
                                                        text = stringResource(
                                                            if (peer.isRealWifiDirect) R.string.p2p_real_badge else R.string.p2p_mesh_peer_badge
                                                        ),
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (peer.isRealWifiDirect) NannaSkyDark else NannaSlate600,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.SignalCellularAlt,
                                            contentDescription = null,
                                            tint = NannaEmeraldDark,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Icon(
                                            imageVector = Icons.Rounded.BatteryChargingFull,
                                            contentDescription = null,
                                            tint = NannaSlate500,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (p2pState.discoveredPeers.isEmpty()) {
                        Button(
                            onClick = onStartScan,
                            enabled = !p2pState.isScanning,
                            colors = ButtonDefaults.buttonColors(containerColor = NannaSkyDark),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Icon(Icons.Rounded.Radar, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.scan_radar_btn),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        OutlinedButton(
                            onClick = onStartScan,
                            enabled = !p2pState.isScanning,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.rescan_radar_btn),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Step 3: Transfer via High-Speed Wi-Fi Direct
        if (p2pState.selectedPeerId != null) {
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
                        Text(
                            text = stringResource(R.string.step_3),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NannaSlate900
                        )

                        if (p2pState.isTransferring) {
                            Surface(
                                color = NannaSkySoft,
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, Color(0xFFBAE6FD)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = stringResource(R.string.transferring),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NannaSkyDark
                                        )
                                        Text(
                                            text = "${p2pState.transferProgress}%",
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Black,
                                            color = NannaSkyDark
                                        )
                                    }
                                    LinearProgressIndicator(
                                        progress = { p2pState.transferProgress / 100f },
                                        color = NannaSkyDark,
                                        trackColor = Color.White,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(10.dp)
                                            .clip(RoundedCornerShape(999.dp))
                                    )
                                    Text(
                                        text = stringResource(R.string.p2p_speed_time),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = NannaSlate600
                                    )
                                }
                            }
                        }

                        if (p2pState.transferDone) {
                            Surface(
                                color = NannaEmeraldSoft,
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, Color(0xFFA7F3D0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
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
                                                imageVector = Icons.Rounded.CheckCircle,
                                                contentDescription = null,
                                                tint = NannaEmeraldDark,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text = stringResource(R.string.transfer_success),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = NannaEmeraldDark
                                            )
                                        }
                                        Surface(
                                            color = NannaAmberSoft,
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = stringResource(R.string.p2p_quest_bonus),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFB45309),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.VerifiedUser,
                                            contentDescription = null,
                                            tint = NannaEmeraldDark,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = stringResource(R.string.hash_verified),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = NannaSlate800
                                        )
                                    }
                                    Surface(
                                        color = Color.White,
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, NannaBorder),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(10.dp),
                                            verticalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.Lock,
                                                    contentDescription = null,
                                                    tint = NannaSlate500,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Text(
                                                    text = stringResource(R.string.checksum_label),
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = NannaSlate500
                                                )
                                            }
                                            Text(
                                                text = p2pState.verifiedSha256,
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = NannaSlate800
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = onStartTransfer,
                            enabled = !p2pState.isTransferring,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NannaPurple,
                                disabledContainerColor = NannaSurfaceMuted
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Icon(Icons.Rounded.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.transfer_btn),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RadarWavePanel() {
    val transition = rememberInfiniteTransition(label = "radar_pulse")
    val pulse1 by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse1"
    )
    val pulse2 by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, delayMillis = 700, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse2"
    )

    Surface(
        color = NannaSlate900,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier.size(96.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val maxRadius = size.minDimension / 2f
                    drawCircle(
                        color = NannaSky.copy(alpha = (1f - pulse1) * 0.65f),
                        radius = maxRadius * pulse1,
                        style = Stroke(width = 3.dp.toPx())
                    )
                    drawCircle(
                        color = NannaEmerald.copy(alpha = (1f - pulse2) * 0.65f),
                        radius = maxRadius * pulse2,
                        style = Stroke(width = 2.dp.toPx())
                    )
                    drawCircle(
                        color = NannaSky,
                        radius = 12.dp.toPx()
                    )
                }
                Icon(
                    imageVector = Icons.Rounded.Wifi,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = stringResource(R.string.scanning),
                color = NannaSky,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
