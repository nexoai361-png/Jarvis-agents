package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val LatoFontFamily = FontFamily(
  Font(R.font.lato, FontWeight.Normal),
  Font(R.font.lato, FontWeight.Medium),
  Font(R.font.lato, FontWeight.SemiBold),
  Font(R.font.lato, FontWeight.Bold),
)

val Typography =
  Typography(
    titleLarge =
      TextStyle(
        fontFamily = LatoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = (-0.2).sp,
      ),
    titleMedium =
      TextStyle(
        fontFamily = LatoFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = (-0.1).sp,
      ),
    bodyLarge =
      TextStyle(
        fontFamily = LatoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp,
      ),
    bodyMedium =
      TextStyle(
        fontFamily = LatoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
      ),
    labelLarge =
      TextStyle(
        fontFamily = LatoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.1.sp,
      ),
    labelMedium =
      TextStyle(
        fontFamily = LatoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.2.sp,
      ),
    labelSmall =
      TextStyle(
        fontFamily = LatoFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.8.sp,
      ),
  )
