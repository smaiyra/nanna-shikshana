package com.dbit.nannashikshana.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.dbit.nannashikshana.R

val NannaBg = Color(0xFFF8FAFC)
val NannaSurface = Color(0xFFFFFFFF)
val NannaSurfaceMuted = Color(0xFFF1F5F9)
val NannaBorder = Color(0xFFE2E8F0)
val NannaSky = Color(0xFF0EA5E9)
val NannaSkyDark = Color(0xFF0284C7)
val NannaSkySoft = Color(0xFFE0F2FE)
val NannaSlate950 = Color(0xFF020617)
val NannaSlate900 = Color(0xFF0F172A)
val NannaSlate800 = Color(0xFF1E293B)
val NannaSlate600 = Color(0xFF475569)
val NannaSlate500 = Color(0xFF64748B)
val NannaSlate400 = Color(0xFF94A3B8)
val NannaPurple = Color(0xFF8B5CF6)
val NannaPurpleDark = Color(0xFF7C3AED)
val NannaPurpleSoft = Color(0xFFF3E8FF)
val NannaEmerald = Color(0xFF10B981)
val NannaEmeraldDark = Color(0xFF059669)
val NannaEmeraldSoft = Color(0xFFECFDF5)
val NannaAmber = Color(0xFFF59E0B)
val NannaAmberSoft = Color(0xFFFEF3C7)
val NannaRose = Color(0xFFE11D48)
val NannaRoseSoft = Color(0xFFFFF1F2)

val KannadaFontFamily = FontFamily(
    Font(R.font.noto_sans_kannada_regular, FontWeight.Normal),
    Font(R.font.noto_sans_kannada_bold, FontWeight.Bold),
    Font(R.font.noto_sans_kannada_bold, FontWeight.Black)
)

private val NannaColorScheme = lightColorScheme(
    primary = NannaSkyDark,
    onPrimary = Color.White,
    primaryContainer = NannaSkySoft,
    onPrimaryContainer = NannaSlate900,
    secondary = NannaPurple,
    onSecondary = Color.White,
    secondaryContainer = NannaPurpleSoft,
    onSecondaryContainer = NannaSlate900,
    tertiary = NannaEmeraldDark,
    onTertiary = Color.White,
    background = NannaBg,
    onBackground = NannaSlate900,
    surface = NannaSurface,
    onSurface = NannaSlate900,
    surfaceVariant = NannaSurfaceMuted,
    onSurfaceVariant = NannaSlate600,
    outline = NannaBorder,
    error = NannaRose,
    onError = Color.White
)

private val NannaTypography = Typography(
    titleLarge = TextStyle(
        fontFamily = KannadaFontFamily,
        fontWeight = FontWeight.Black,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    titleMedium = TextStyle(
        fontFamily = KannadaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    titleSmall = TextStyle(
        fontFamily = KannadaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = KannadaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 19.sp
    ),
    bodySmall = TextStyle(
        fontFamily = KannadaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 17.sp
    ),
    labelMedium = TextStyle(
        fontFamily = KannadaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = KannadaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 14.sp
    )
)

@Composable
fun NannaShikshanaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NannaColorScheme,
        typography = NannaTypography,
        content = content
    )
}
