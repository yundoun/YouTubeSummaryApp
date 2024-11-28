package com.example.youtube_summary_native.core.constants

object AppRoutes {
    const val HOME_ROUTE = "home"  // "/home" -> "home"
    const val SUMMARY_ROUTE = "summary/{videoId}"  // "/summary/{videoId}" -> "summary/{videoId}"
    const val SETTING_ROUTE = "setting"  // "/setting" -> "setting"

    // Route names
    const val HOME = "home"
    const val SUMMARY = "summary"
    const val SETTING = "setting"

    // 내비게이션 할 때
    // SummaryScreen으로 이동할 때 사용하는 전체 경로 문자열 생성
    // summary/{videoId} 이런 식으로 경로 생성됨
    fun createSummaryRoute(videoId: String) = "summary/$videoId"
}