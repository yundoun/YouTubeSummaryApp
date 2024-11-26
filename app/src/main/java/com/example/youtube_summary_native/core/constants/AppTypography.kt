package com.example.youtube_summary_native.core.constants

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object AppTypography {
    val typography = Typography(
        // Display styles
        displayLarge = TextStyle(
            fontSize = 57.sp,
            fontWeight = FontWeight.Bold,
        ),
        displayMedium = TextStyle(
            fontSize = 45.sp,
            fontWeight = FontWeight.Bold,
        ),
        displaySmall = TextStyle(
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
        ),

        // Headline styles
        headlineLarge = TextStyle(
            fontSize = 32.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        headlineMedium = TextStyle(
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        headlineSmall = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
        ),

        // Title styles
        titleLarge = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
        ),
        titleMedium = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
        ),
        titleSmall = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
        ),

        // Body styles
        bodyLarge = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
        ),
        bodyMedium = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
        ),
        bodySmall = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
        ),

        // Label styles
        labelLarge = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
        ),
        labelMedium = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
        ),
        labelSmall = TextStyle(
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
        )
    )
}