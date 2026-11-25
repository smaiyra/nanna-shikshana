package com.dbit.nannashikshana.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dbit.nannashikshana.R
import com.dbit.nannashikshana.domain.gamification.GamificationEngine
import com.dbit.nannashikshana.domain.model.AppLanguage
import com.dbit.nannashikshana.ui.theme.NannaAmber
import com.dbit.nannashikshana.ui.theme.NannaAmberSoft
import com.dbit.nannashikshana.ui.theme.NannaBorder
import com.dbit.nannashikshana.ui.theme.NannaEmeraldDark
import com.dbit.nannashikshana.ui.theme.NannaEmeraldSoft
import com.dbit.nannashikshana.ui.theme.NannaPurpleDark
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
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun FractionDiscModel(
    language: AppLanguage,
    onChime: () -> Unit
) {
    var denominator by remember { mutableIntStateOf(4) }
    var shadedSlices by remember { mutableStateOf(setOf(0, 1, 2)) }
    val numerator = shadedSlices.size
    val pct = ((numerator.toFloat() / denominator.toFloat()) * 100f).roundToInt()

    Card(
        colors = CardDefaults.cardColors(containerColor = NannaSurface),
        shape = RoundedCornerShape(16.dp),
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.fraction_title),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NannaSlate800
                    )
                    Text(
                        text = stringResource(R.string.fraction_sub),
                        fontSize = 12.sp,
                        color = NannaSlate500
                    )
                }
                Surface(
                    color = NannaSkySoft,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFBAE6FD))
                ) {
                    Text(
                        text = stringResource(R.string.fraction_filled, pct),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = NannaSkyDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(170.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(denominator, shadedSlices) {
                                detectTapGestures { offset ->
                                    val cx = size.width / 2f
                                    val cy = size.height / 2f
                                    val dx = offset.x - cx
                                    val dy = offset.y - cy
                                    val radius = min(size.width, size.height) * 0.44f
                                    if (hypot(dx, dy) <= radius) {
                                        var deg = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())) + 90.0
                                        if (deg < 0) deg += 360.0
                                        val sliceIndex = ((deg / (360.0 / denominator)).toInt())
                                            .coerceIn(0, denominator - 1)
                                        shadedSlices = if (shadedSlices.contains(sliceIndex)) {
                                            shadedSlices - sliceIndex
                                        } else {
                                            shadedSlices + sliceIndex
                                        }
                                        onChime()
                                    }
                                }
                            }
                    ) {
                        val r = size.minDimension * 0.44f
                        val topLeft = Offset(center.x - r, center.y - r)
                        val arcSize = Size(r * 2f, r * 2f)
                        val sweep = 360f / denominator

                        drawCircle(color = Color(0xFFF1F5F9), radius = r, center = center)
                        for (i in 0 until denominator) {
                            val start = i * sweep - 90f
                            val isShaded = shadedSlices.contains(i)
                            drawArc(
                                color = if (isShaded) NannaSky else Color(0xFFF8FAFC),
                                startAngle = start,
                                sweepAngle = sweep,
                                useCenter = true,
                                topLeft = topLeft,
                                size = arcSize
                            )
                            drawArc(
                                color = Color.White,
                                startAngle = start,
                                sweepAngle = sweep,
                                useCenter = true,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(width = 5f)
                            )
                        }
                        drawCircle(color = Color.White, radius = r * 0.16f, center = center)
                        drawCircle(
                            color = NannaSky,
                            radius = r * 0.16f,
                            center = center,
                            style = Stroke(width = 4f)
                        )
                        drawCircle(color = NannaSky, radius = 6f, center = center)
                    }
                }

                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, NannaBorder),
                    modifier = Modifier.width(122.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.fraction_value),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NannaSlate500
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = if (language == AppLanguage.KN) GamificationEngine.toKannadaDigits(numerator) else "$numerator",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = NannaSkyDark
                        )
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(2.dp)
                                .background(NannaSlate400)
                        )
                        Text(
                            text = if (language == AppLanguage.KN) GamificationEngine.toKannadaDigits(denominator) else "$denominator",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = NannaSlate800
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.fraction_formula),
                            fontSize = 10.sp,
                            color = NannaSlate400
                        )
                    }
                }
            }

            HorizontalDivider(color = NannaSurfaceMuted)

            Text(
                text = stringResource(R.string.fraction_set_slices),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = NannaSlate600
            )
            Surface(
                color = NannaSurfaceMuted,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(2, 3, 4, 6, 8).forEach { slices ->
                        val active = denominator == slices
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (active) NannaSkyDark else NannaSurface)
                                .clickable {
                                    denominator = slices
                                    val half = ((slices + 1) / 2).coerceAtMost(slices)
                                    shadedSlices = (0 until half).toSet()
                                    onChime()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (language == AppLanguage.KN) {
                                    GamificationEngine.toKannadaDigits(slices)
                                } else {
                                    "$slices"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (active) Color.White else NannaSlate800
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PhotosynthesisModel(
    onChime: () -> Unit,
    onBuzz: () -> Unit
) {
    var sunlightOn by remember { mutableStateOf(true) }
    var waterOn by remember { mutableStateOf(true) }
    val active = sunlightOn && waterOn

    Card(
        colors = CardDefaults.cardColors(containerColor = NannaSurface),
        shape = RoundedCornerShape(16.dp),
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.photo_title),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NannaSlate800
                    )
                    Text(
                        text = stringResource(R.string.photo_sub),
                        fontSize = 12.sp,
                        color = NannaSlate500
                    )
                }
                Surface(
                    color = if (active) NannaEmeraldSoft else NannaAmberSoft,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, if (active) Color(0xFFA7F3D0) else Color(0xFFFDE68A))
                ) {
                    Text(
                        text = stringResource(if (active) R.string.photo_active else R.string.photo_halted),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (active) NannaEmeraldDark else Color(0xFFB45309),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, NannaBorder, RoundedCornerShape(12.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFF0F9FF), Color(0xFFECFDF5), Color(0x1A78350F))
                        )
                    )
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Sun
                    val sunAlpha = if (sunlightOn) 1f else 0.2f
                    val sunCenter = Offset(w * 0.16f, h * 0.2f)
                    drawCircle(color = NannaAmber.copy(alpha = sunAlpha), radius = 26f, center = sunCenter)
                    drawCircle(color = Color(0xFFFBBF24).copy(alpha = sunAlpha), radius = 19f, center = sunCenter)
                    for (deg in 0 until 360 step 45) {
                        val rad = deg * PI / 180.0
                        drawLine(
                            color = NannaAmber.copy(alpha = sunAlpha),
                            start = Offset(
                                (sunCenter.x + 32f * cos(rad)).toFloat(),
                                (sunCenter.y + 32f * sin(rad)).toFloat()
                            ),
                            end = Offset(
                                (sunCenter.x + 44f * cos(rad)).toFloat(),
                                (sunCenter.y + 44f * sin(rad)).toFloat()
                            ),
                            strokeWidth = 4f
                        )
                    }
                    if (sunlightOn) {
                        drawLine(
                            color = NannaAmber,
                            start = Offset(w * 0.22f, h * 0.26f),
                            end = Offset(w * 0.42f, h * 0.45f),
                            strokeWidth = 3f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f))
                        )
                    }

                    // Soil
                    val soilY = h * 0.80f
                    drawRect(
                        color = Color(0x4D78350F),
                        topLeft = Offset(0f, soilY),
                        size = Size(w, h - soilY)
                    )
                    drawLine(
                        color = Color(0xFF92400E),
                        start = Offset(0f, soilY),
                        end = Offset(w, soilY),
                        strokeWidth = 5f
                    )

                    // Roots
                    val stemX = w * 0.52f
                    drawLine(Color(0xFF78350F), Offset(stemX, soilY), Offset(stemX - 40f, h * 0.94f), strokeWidth = 5f)
                    drawLine(Color(0xFF78350F), Offset(stemX, soilY), Offset(stemX + 12f, h * 0.96f), strokeWidth = 5f)
                    drawLine(Color(0xFF78350F), Offset(stemX, soilY), Offset(stemX + 48f, h * 0.93f), strokeWidth = 5f)

                    if (waterOn) {
                        listOf(0.88f, 0.84f, 0.72f, 0.62f).forEachIndexed { idx, fy ->
                            drawCircle(
                                color = NannaSky,
                                radius = 6f,
                                center = Offset(stemX + (idx % 2) * 8f - 4f, h * fy)
                            )
                        }
                    }

                    // Stem & Leaves
                    drawLine(
                        color = Color(0xFF16A34A),
                        start = Offset(stemX, soilY),
                        end = Offset(stemX, h * 0.38f),
                        strokeWidth = 10f
                    )
                    val leafColor = if (active) Color(0xFF22C55E) else Color(0xFF86EFAC)
                    val leftLeaf = Path().apply {
                        moveTo(stemX, h * 0.55f)
                        cubicTo(stemX - 110f, h * 0.44f, stemX - 120f, h * 0.25f, stemX - 55f, h * 0.36f)
                        close()
                    }
                    drawPath(leftLeaf, color = leafColor)
                    drawPath(leftLeaf, color = Color(0xFF15803D), style = Stroke(width = 4f))

                    val rightLeaf = Path().apply {
                        moveTo(stemX, h * 0.48f)
                        cubicTo(stemX + 100f, h * 0.40f, stemX + 115f, h * 0.22f, stemX + 55f, h * 0.33f)
                        close()
                    }
                    drawPath(rightLeaf, color = if (active) Color(0xFF16A34A) else Color(0xFF86EFAC))
                    drawPath(rightLeaf, color = Color(0xFF15803D), style = Stroke(width = 4f))

                    // Output Glucose + O2 indicators
                    val outAlpha = if (active) 1f else 0.22f
                    drawCircle(color = NannaSkySoft.copy(alpha = outAlpha), radius = 24f, center = Offset(w * 0.76f, h * 0.26f))
                    drawCircle(
                        color = NannaSkyDark.copy(alpha = outAlpha),
                        radius = 24f,
                        center = Offset(w * 0.76f, h * 0.26f),
                        style = Stroke(width = 3f)
                    )
                    drawRoundRect(
                        color = Color(0xFFFEF08A).copy(alpha = outAlpha),
                        topLeft = Offset(w * 0.64f, h * 0.52f),
                        size = Size(w * 0.28f, 38f),
                        cornerRadius = CornerRadius(10f, 10f)
                    )
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 38.dp, end = 22.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "O₂",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = NannaSkyDark.copy(alpha = if (active) 1f else 0.25f)
                    )
                }
                Text(
                    text = "C₆H₁₂O₆",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF854D0E).copy(alpha = if (active) 1f else 0.3f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 28.dp, bottom = 68.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        sunlightOn = !sunlightOn
                        if (sunlightOn) onChime() else onBuzz()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (sunlightOn) NannaAmber else NannaSurfaceMuted,
                        contentColor = if (sunlightOn) Color.White else NannaSlate600
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Icon(Icons.Rounded.WbSunny, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = stringResource(if (sunlightOn) R.string.sunlight_on else R.string.sunlight_off),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Button(
                    onClick = {
                        waterOn = !waterOn
                        if (waterOn) onChime() else onBuzz()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (waterOn) NannaSkyDark else NannaSurfaceMuted,
                        contentColor = if (waterOn) Color.White else NannaSlate600
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Icon(Icons.Rounded.WaterDrop, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = stringResource(if (waterOn) R.string.water_on else R.string.water_off),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Surface(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, NannaBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.reaction_label),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NannaSlate800
                    )
                    Text(
                        text = stringResource(R.string.reaction_formula),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        color = NannaSkyDark
                    )
                }
            }
        }
    }
}

@Composable
fun AksharamaleSoundboardModel(
    language: AppLanguage,
    onSpeakVowel: (String, String) -> Unit
) {
    val vowels = GamificationEngine.aksharamaleVowels
    var selectedIdx by remember { mutableIntStateOf(0) }
    val selected = vowels[selectedIdx]

    Card(
        colors = CardDefaults.cardColors(containerColor = NannaSurface),
        shape = RoundedCornerShape(16.dp),
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.vowel_title),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NannaSlate800
                    )
                    Text(
                        text = stringResource(R.string.vowel_sub),
                        fontSize = 12.sp,
                        color = NannaSlate500
                    )
                }
                Surface(
                    color = NannaPurpleSoft,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSpeakVowel(selected.letter, selected.exampleKn) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                            contentDescription = null,
                            tint = NannaPurpleDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = stringResource(R.string.listen_short),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NannaPurpleDark
                        )
                    }
                }
            }

            // 5x3 grid of 15 Kannada vowels
            for (row in 0 until 3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (col in 0 until 5) {
                        val idx = row * 5 + col
                        val item = vowels[idx]
                        val isSelected = idx == selectedIdx
                        Surface(
                            color = if (isSelected) NannaPurpleDark else Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, if (isSelected) NannaPurpleDark else NannaBorder),
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    selectedIdx = idx
                                    onSpeakVowel(item.letter, item.exampleKn)
                                }
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = item.letter,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else NannaSlate900
                                )
                                Text(
                                    text = item.translit,
                                    fontSize = 10.sp,
                                    color = if (isSelected) NannaPurpleSoft else NannaSlate400
                                )
                            }
                        }
                    }
                }
            }

            Surface(
                color = NannaPurpleSoft.copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFE9D5FF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(NannaPurpleDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = selected.letter,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (language == AppLanguage.KN) selected.typeKn else selected.typeEn,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF581C87)
                        )
                        Text(
                            text = "${selected.exampleKn} (${selected.meaningEn})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NannaSlate800
                        )
                        Text(
                            text = stringResource(R.string.vowel_example_caption),
                            fontSize = 11.sp,
                            color = NannaSlate500
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HoysalaBlueprintModel(onChime: () -> Unit) {
    var layer by remember { mutableStateOf("platform") }

    Card(
        colors = CardDefaults.cardColors(containerColor = NannaSurface),
        shape = RoundedCornerShape(16.dp),
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.hoysala_title),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NannaSlate800
                    )
                    Text(
                        text = stringResource(R.string.hoysala_sub),
                        fontSize = 12.sp,
                        color = NannaSlate500
                    )
                }
                Surface(
                    color = NannaAmberSoft,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A))
                ) {
                    Text(
                        text = stringResource(R.string.hoysala_year),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF92400E),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NannaSlate900)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val step = 36f
                    var x = 0f
                    while (x < w) {
                        drawLine(NannaSlate800, Offset(x, 0f), Offset(x, h), strokeWidth = 1f)
                        x += step
                    }
                    var y = 0f
                    while (y < h) {
                        drawLine(NannaSlate800, Offset(0f, y), Offset(w, y), strokeWidth = 1f)
                        y += step
                    }

                    val sqSize = h * 0.56f
                    val sqTopLeft = Offset(center.x - sqSize / 2f, center.y - sqSize / 2f)
                    listOf(0f, 22.5f, 45f, 67.5f).forEach { deg ->
                        rotate(degrees = deg, pivot = center) {
                            if (layer == "platform") {
                                drawRect(
                                    color = Color(0x3378350F),
                                    topLeft = sqTopLeft,
                                    size = Size(sqSize, sqSize)
                                )
                            }
                            drawRect(
                                color = if (layer == "platform") NannaAmber else NannaAmber.copy(alpha = 0.45f),
                                topLeft = sqTopLeft,
                                size = Size(sqSize, sqSize),
                                style = Stroke(width = if (layer == "platform") 4f else 2f)
                            )
                        }
                    }

                    val pillarAlpha = if (layer == "pillars") 1f else 0.35f
                    val offsets = listOf(
                        Offset(-55f, -55f),
                        Offset(55f, -55f),
                        Offset(-82f, 0f),
                        Offset(0f, 0f),
                        Offset(82f, 0f),
                        Offset(-55f, 55f),
                        Offset(55f, 55f)
                    )
                    offsets.forEach { d ->
                        val pt = Offset(center.x + d.x, center.y + d.y)
                        drawCircle(color = NannaSky.copy(alpha = pillarAlpha), radius = 11f, center = pt)
                        drawCircle(color = Color.White.copy(alpha = pillarAlpha), radius = 5f, center = pt)
                    }

                    val sanctumSize = 54f
                    drawRoundRect(
                        color = NannaAmber,
                        topLeft = Offset(center.x - sanctumSize / 2f, center.y - sanctumSize / 2f),
                        size = Size(sanctumSize, sanctumSize),
                        cornerRadius = CornerRadius(6f, 6f)
                    )
                }

                Text(
                    text = stringResource(R.string.hoysala_sanctum),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF78350F),
                    modifier = Modifier.align(Alignment.Center)
                )
                Text(
                    text = "N ↑",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NannaSlate400,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                )

                Surface(
                    color = Color(0xCC020617),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, NannaSlate800),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    val captionRes = when (layer) {
                        "pillars" -> R.string.hoysala_desc_pillars
                        "sculpture" -> R.string.hoysala_desc_sculpture
                        else -> R.string.hoysala_desc_platform
                    }
                    Text(
                        text = stringResource(captionRes),
                        color = Color.White,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Surface(
                color = NannaSurfaceMuted,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val tabs = listOf(
                        Triple("platform", R.string.hoysala_tab_platform, Color(0xFFB45309)),
                        Triple("pillars", R.string.hoysala_tab_pillars, NannaSkyDark),
                        Triple("sculpture", R.string.hoysala_tab_sculpture, NannaPurpleDark)
                    )
                    tabs.forEach { (id, labelRes, activeColor) ->
                        val active = layer == id
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (active) NannaSurface else Color.Transparent)
                                .clickable {
                                    layer = id
                                    onChime()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(labelRes),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (active) activeColor else NannaSlate600,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
