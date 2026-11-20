package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val SourceCodeProFontFamily = FontFamily(
  Font(R.font.source_code_pro, FontWeight.Normal),
  Font(R.font.source_code_pro, FontWeight.Medium),
  Font(R.font.source_code_pro, FontWeight.SemiBold),
)

private val DefaultTypography = Typography()

val Typography =
  Typography(
    displayLarge = DefaultTypography.displayLarge.copy(fontFamily = FontFamily.Default),
    displayMedium = DefaultTypography.displayMedium.copy(fontFamily = FontFamily.Default),
    displaySmall = DefaultTypography.displaySmall.copy(fontFamily = FontFamily.Default),
    headlineLarge = DefaultTypography.headlineLarge.copy(fontFamily = FontFamily.Default),
    headlineMedium = DefaultTypography.headlineMedium.copy(fontFamily = FontFamily.Default),
    headlineSmall = DefaultTypography.headlineSmall.copy(fontFamily = FontFamily.Default),
    titleLarge =
      TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = (-0.2).sp,
      ),
    titleMedium =
      TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = (-0.1).sp,
      ),
    titleSmall = DefaultTypography.titleSmall.copy(fontFamily = FontFamily.Default),
    bodyLarge =
      TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp,
      ),
    bodyMedium =
      TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
      ),
    bodySmall = DefaultTypography.bodySmall.copy(fontFamily = FontFamily.Default),
    labelLarge =
      TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.1.sp,
      ),
    labelMedium =
      TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.2.sp,
      ),
    labelSmall =
      TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.8.sp,
      ),
  )
