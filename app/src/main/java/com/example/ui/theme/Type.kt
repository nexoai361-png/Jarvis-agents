package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val IbmPlexSansFontFamily = FontFamily(
  Font(R.font.ibm_plex_sans, FontWeight.Normal),
  Font(R.font.ibm_plex_sans, FontWeight.Medium),
  Font(R.font.ibm_plex_sans, FontWeight.SemiBold),
  Font(R.font.ibm_plex_sans, FontWeight.Bold),
)

private val DefaultTypography = Typography()

val Typography =
  Typography(
    displayLarge = DefaultTypography.displayLarge.copy(fontFamily = IbmPlexSansFontFamily),
    displayMedium = DefaultTypography.displayMedium.copy(fontFamily = IbmPlexSansFontFamily),
    displaySmall = DefaultTypography.displaySmall.copy(fontFamily = IbmPlexSansFontFamily),
    headlineLarge = DefaultTypography.headlineLarge.copy(fontFamily = IbmPlexSansFontFamily),
    headlineMedium = DefaultTypography.headlineMedium.copy(fontFamily = IbmPlexSansFontFamily),
    headlineSmall = DefaultTypography.headlineSmall.copy(fontFamily = IbmPlexSansFontFamily),
    titleLarge =
      TextStyle(
        fontFamily = IbmPlexSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = (-0.2).sp,
      ),
    titleMedium =
      TextStyle(
        fontFamily = IbmPlexSansFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = (-0.1).sp,
      ),
    titleSmall = DefaultTypography.titleSmall.copy(fontFamily = IbmPlexSansFontFamily),
    bodyLarge =
      TextStyle(
        fontFamily = IbmPlexSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp,
      ),
    bodyMedium =
      TextStyle(
        fontFamily = IbmPlexSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
      ),
    bodySmall = DefaultTypography.bodySmall.copy(fontFamily = IbmPlexSansFontFamily),
    labelLarge =
      TextStyle(
        fontFamily = IbmPlexSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.1.sp,
      ),
    labelMedium =
      TextStyle(
        fontFamily = IbmPlexSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.2.sp,
      ),
    labelSmall =
      TextStyle(
        fontFamily = IbmPlexSansFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.8.sp,
      ),
  )
