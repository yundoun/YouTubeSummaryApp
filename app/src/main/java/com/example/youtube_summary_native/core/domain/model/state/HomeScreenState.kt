package com.example.youtube_summary_native.core.domain.model.state

import com.example.youtube_summary_native.core.domain.model.summary.SummaryInfo
import com.example.youtube_summary_native.core.domain.model.user.UserInfo

data class HomeScreenState(
    val videoId: String = "",
    val clipboardData: String = "",
    val homeScrollState: Boolean = false,
    val searchBarState: SearchBarState = SearchBarState(),
    val recentSummaryState: RecentSummaryState = RecentSummaryState(),
    val summaries: List<SummaryInfo> = emptyList(),
    val isLoading: Boolean = false,
    val isAdmin: Boolean = false,
    val isLoginUser: Boolean = false,
    val userInfo: UserInfo? = null
)