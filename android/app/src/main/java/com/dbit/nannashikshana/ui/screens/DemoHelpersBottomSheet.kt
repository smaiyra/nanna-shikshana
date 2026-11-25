package com.dbit.nannashikshana.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dbit.nannashikshana.R
import com.dbit.nannashikshana.domain.model.StudentProfile
import com.dbit.nannashikshana.ui.theme.NannaAmber
import com.dbit.nannashikshana.ui.theme.NannaBorder
import com.dbit.nannashikshana.ui.theme.NannaEmeraldDark
import com.dbit.nannashikshana.ui.theme.NannaPurple
import com.dbit.nannashikshana.ui.theme.NannaRose
import com.dbit.nannashikshana.ui.theme.NannaSkyDark
import com.dbit.nannashikshana.ui.theme.NannaSlate500
import com.dbit.nannashikshana.ui.theme.NannaSlate600
import com.dbit.nannashikshana.ui.theme.NannaSlate900
import com.dbit.nannashikshana.ui.theme.NannaSurface
import com.dbit.nannashikshana.ui.theme.NannaSurfaceMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoHelpersBottomSheet(
    profile: StudentProfile,
    onSimulateStreak: (Int) -> Unit,
    onAddXp: () -> Unit,
    onResetDemoData: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = NannaSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.demo_helper_title),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = NannaSlate900
                    )
                    Text(
                        text = stringResource(R.string.demo_helper_sub),
                        fontSize = 11.sp,
                        color = NannaSlate500
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = stringResource(R.string.close_cd),
                        tint = NannaSlate600
                    )
                }
            }

            Surface(
                color = NannaSurfaceMuted,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, NannaBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    MetricRow(stringResource(R.string.demo_last_active), profile.lastActiveDate)
                    MetricRow(stringResource(R.string.demo_streak_count), "${profile.streakCount}")
                    MetricRow(stringResource(R.string.demo_streak_freezes), "${profile.streakFreezesLeft}")
                    MetricRow(stringResource(R.string.demo_total_xp), "${profile.totalXp} XP")
                }
            }

            HorizontalDivider(color = NannaSurfaceMuted)

            Text(
                text = stringResource(R.string.demo_streak_heading),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = NannaSlate600
            )

            Button(
                onClick = { onSimulateStreak(1) },
                colors = ButtonDefaults.buttonColors(containerColor = NannaEmeraldDark),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Icon(Icons.Rounded.LocalFireDepartment, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text(text = stringResource(R.string.sim_streak_yesterday), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { onSimulateStreak(2) },
                colors = ButtonDefaults.buttonColors(containerColor = NannaSkyDark),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Icon(Icons.Rounded.Shield, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text(text = stringResource(R.string.sim_streak_missed_1), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { onSimulateStreak(3) },
                colors = ButtonDefaults.buttonColors(containerColor = NannaAmber),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Icon(Icons.Rounded.Warning, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text(text = stringResource(R.string.sim_streak_missed_3), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onAddXp,
                colors = ButtonDefaults.buttonColors(containerColor = NannaPurple),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Icon(Icons.Rounded.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text(text = stringResource(R.string.sim_add_xp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = onResetDemoData,
                border = BorderStroke(1.dp, NannaRose),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Icon(Icons.Rounded.Refresh, contentDescription = null, tint = NannaRose, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.reset_data),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NannaRose
                )
            }
        }
    }
}

@Composable
private fun MetricRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = NannaSlate600)
        Text(
            text = value,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = NannaSlate900
        )
    }
}
