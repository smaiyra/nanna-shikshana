package com.dbit.nannashikshana.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material.icons.rounded.WifiTethering
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dbit.nannashikshana.R
import com.dbit.nannashikshana.ui.theme.NannaAmber
import com.dbit.nannashikshana.ui.theme.NannaAmberSoft
import com.dbit.nannashikshana.ui.theme.NannaBorder
import com.dbit.nannashikshana.ui.theme.NannaEmerald
import com.dbit.nannashikshana.ui.theme.NannaEmeraldDark
import com.dbit.nannashikshana.ui.theme.NannaPurple
import com.dbit.nannashikshana.ui.theme.NannaSky
import com.dbit.nannashikshana.ui.theme.NannaSkyDark
import com.dbit.nannashikshana.ui.theme.NannaSkySoft
import com.dbit.nannashikshana.ui.theme.NannaSlate500
import com.dbit.nannashikshana.ui.theme.NannaSlate600
import com.dbit.nannashikshana.ui.theme.NannaSlate800
import com.dbit.nannashikshana.ui.theme.NannaSlate900
import com.dbit.nannashikshana.ui.theme.NannaSurface

@Composable
fun CreditsScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 92.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Card
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
                    .padding(18.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.about_header),
                        color = Color.White,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = stringResource(R.string.about_tagline),
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp
                    )
                    Surface(
                        color = Color.White.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.academic_institution),
                                color = NannaSky,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = stringResource(R.string.college_name),
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = stringResource(R.string.autonomous_tag),
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Problem Statement Card
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.WarningAmber,
                            contentDescription = null,
                            tint = NannaAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = stringResource(R.string.problem_statement),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NannaSlate900
                        )
                    }
                    Text(
                        text = stringResource(R.string.problem_desc),
                        fontSize = 12.sp,
                        color = NannaSlate600
                    )
                }
            }
        }

        // Autonomous Architecture & 2x2 Specs Grid
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
                        text = stringResource(R.string.solution_title),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NannaSlate900
                    )
                    Text(
                        text = stringResource(R.string.solution_desc),
                        fontSize = 12.sp,
                        color = NannaSlate600
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SpecTile(
                            icon = Icons.Rounded.Memory,
                            tint = NannaSkyDark,
                            label = stringResource(R.string.spec_hardware_label),
                            value = stringResource(R.string.spec_hardware_val),
                            modifier = Modifier.weight(1f)
                        )
                        SpecTile(
                            icon = Icons.Rounded.Storage,
                            tint = NannaEmeraldDark,
                            label = stringResource(R.string.spec_storage_label),
                            value = stringResource(R.string.spec_storage_val),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SpecTile(
                            icon = Icons.Rounded.WifiTethering,
                            tint = NannaPurple,
                            label = stringResource(R.string.spec_p2p_label),
                            value = stringResource(R.string.spec_p2p_val),
                            modifier = Modifier.weight(1f)
                        )
                        SpecTile(
                            icon = Icons.Rounded.Code,
                            tint = NannaAmber,
                            label = stringResource(R.string.spec_weight_label),
                            value = stringResource(R.string.spec_weight_val),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Mentor Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NannaSurface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, NannaBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        color = NannaAmberSoft,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.School,
                                contentDescription = null,
                                tint = Color(0xFFB45309),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = stringResource(R.string.guide_label),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NannaSlate500
                        )
                        Text(
                            text = stringResource(R.string.guide_name),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = NannaSlate900
                        )
                        Text(
                            text = stringResource(R.string.guide_dept),
                            fontSize = 12.sp,
                            color = NannaSlate600
                        )
                    }
                }
            }
        }

        // Team Members Card
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Groups,
                            contentDescription = null,
                            tint = NannaSkyDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = stringResource(R.string.team_members_label),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NannaSlate900
                        )
                    }
                    val members = listOf(
                        "01" to R.string.team_member_1,
                        "02" to R.string.team_member_2,
                        "03" to R.string.team_member_3,
                        "04" to R.string.team_member_4
                    )
                    members.forEach { (num, nameRes) ->
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, NannaBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    color = NannaSkySoft,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = num,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Black,
                                        color = NannaSkyDark,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = stringResource(nameRes),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NannaSlate900
                                    )
                                    Text(
                                        text = stringResource(R.string.team_role),
                                        fontSize = 11.sp,
                                        color = NannaSlate500
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecTile(
    icon: ImageVector,
    tint: Color,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, NannaBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NannaSlate500)
            Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NannaSlate900)
        }
    }
}
