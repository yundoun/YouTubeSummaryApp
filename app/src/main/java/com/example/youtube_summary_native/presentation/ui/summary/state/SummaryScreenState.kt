package com.example.youtube_summary_native.presentation.ui.summary.state

data class SummaryScreenState(
    val videoId: String = "",
    val title: String = "",
    val isLoading: Boolean = false,
    val summaryContent: String? = null,
    val scriptContent: String? = null,
    val isDrawerOpen: Boolean = false,
    val currentTab: Int = 0,
    val captionState: CaptionState = CaptionState(),
    val shareState: ShareState = ShareState(),
    val error: String? = null
)