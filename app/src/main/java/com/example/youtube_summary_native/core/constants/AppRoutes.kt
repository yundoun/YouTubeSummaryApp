package com.example.youtube_summary_native.core.constants

object AppRoutes {
    // Routes (앞의 슬래시 제거)
    const val HOME_ROUTE = "home"  // "/home" -> "home"
    const val SUMMARY_ROUTE = "summary/{videoId}"  // "/summary/{videoId}" -> "summary/{videoId}"
    const val SETTING_ROUTE = "setting"  // "/setting" -> "setting"

    // Route names
    const val HOME = "home"
    const val SUMMARY = "summary"
    const val SETTING = "setting"

    // Create route with parameters
    fun createSummaryRoute(videoId: String) = "summary/$videoId"
}