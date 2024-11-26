package com.example.youtube_summary_native.core.domain.model.state

data class HomeScreenState(
    val videoId: String = "",
    val clipboardData: String = "",
    val homeScrollState: Boolean = false,
    val searchBarState: SearchBarState = SearchBarState(),
    val recentSummaryState: RecentSummaryState = RecentSummaryState()
)