package com.example.youtube_summary_native.presentation.theme

import androidx.compose.ui.graphics.Color

// Common Colors
object AppColors {
    val Primary = Color(0xFFFF4E50)
    val Tertiary = Color(0xFFFFC371)

    val SideBarColor = Color(0x76FFFAF3)
    val BorderColor12 = Color(0x1F000000)
    val BorderColor26 = Color(0x42000000)

    // Snackbar Colors
    val SnackbarSuccess = Color(0xFF4CAF50)
    val SnackbarInfo = Color(0xFF2196F3)
    val SnackbarWarning = Color(0xFFFFA726)
    val SnackbarError = Color(0xFFEF5350)

    // Gradients - represented as pairs of colors
    val PrimaryGradient = listOf(Color(0xFFFF5F6D), Color(0xFFFFC371))
    val OfflineGradient = listOf(Color(0xFF999999), Color(0xFF999999))
}

// Light Theme Colors
object LightColors {
    val Primary = Color(0xFFFF4E50)
    val PrimaryFixed = Color(0xFFE04648)
    val Secondary = Color(0xFFFFEEDD)
    val Surface = Color(0xFFFFFFFF)
    val OnPrimary = Color(0xFFFFFFFF)
    val OnSecondary = Color(0xFF333333)
    val OnSurface = Color(0xFF000000)
    val Error = Color(0xFFD32F2F)
    val OnError = Color(0xFFFFFFFF)
}

// Dark Theme Colors
object DarkColors {
    val Primary = Color(0xFFFF4E50)
    val Secondary = Color(0xFF1E1E1E)
    val Surface = Color(0xFF121212)
    val OnPrimary = Color(0xFFFFFFFF)
    val OnSecondary = Color(0xFFE0E0E0)
    val OnSurface = Color(0xFFFFFFFF)
    val Error = Color(0xFFD32F2F)
    val OnError = Color(0xFFFFFFFF)
}