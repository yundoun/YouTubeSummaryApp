package com.example.youtube_summary_native.core.constants

import androidx.compose.ui.unit.dp
import kotlin.time.Duration.Companion.milliseconds

object AppDimensions {
    // Basic Numbers
    const val ZERO = 0
    const val ONE = 1
    const val TWO = 2
    const val THREE = 3

    // Padding and Spacing
    val ExtraSmallPadding = 4.dp
    val SmallPadding = 8.dp
    val DefaultPadding = 16.dp
    val MediumPadding = 20.dp
    val LargePadding = 32.dp
    val ExtraLargePadding = 48.dp

    // Border Radius
    val DefaultRadius = 8.dp
    val CardRadius = 16.dp
    val SearchBarRadius = 20.dp

    // Icon Sizes
    val ListIconSize = 20.dp
    val IconSize = 24.dp
    val BigIconSize = 36.dp

    // Typography
    val HeadlineSmall = 24.dp
    val HeadlineMedium = 28.dp
    val HeadlineLarge = 32.dp

    // Layout Dimensions
    val DefaultMobileDeviceSize = 600.dp
    val ResponsiveWidth = 900.dp
    val SearchBarMaxWidth = 1600.dp

    // Search Bar Padding
    val HomeMobileSearchBarPadding = 20.dp
    val HomeWebSearchBarPadding = 120.dp

    // Item Dimensions
    val RecentItemWidth = 10.dp
    val RecentItemHeight = 500.dp
    val Dim300 = 300.dp

    // Animation Durations
    val DrawerDuration = 250.milliseconds
    val AnimationDuration = 300.milliseconds
    val ScrollDebounceTime = 100.milliseconds

    // Scroll Triggers
    const val SCREEN_HEIGHT_SCROLL_TRIGGER = 4f // 스크롤 트리거를 위한 화면 높이 분할 값
}