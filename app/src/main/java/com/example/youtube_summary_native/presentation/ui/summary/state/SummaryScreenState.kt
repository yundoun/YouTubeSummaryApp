package com.example.youtube_summary_native.presentation.ui.summary.state

import com.example.youtube_summary_native.core.domain.model.summary.SummaryInfo

data class SummaryScreenState(
    val isLoading: Boolean = false,
    val videoId: String = "",
    val title: String = "",
    val summaryContent: String = "",
    val scriptContent: String = "",
    val error: String? = null,
    val currentTab: Int = 0,
    val isDrawerOpen: Boolean = false,
    val captionState: CaptionState = CaptionState(),
    val shareState: ShareState = ShareState(),
    val summaryList: List<SummaryInfo> = emptyList() // 추가
)