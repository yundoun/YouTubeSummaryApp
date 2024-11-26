package com.example.youtube_summary_native.core.domain.model.state

import androidx.compose.foundation.lazy.LazyListState

data class RecentSummaryState(
    val listState: LazyListState = LazyListState(),
    val gridScrollState: Boolean = false,
    val gridSortState: Boolean = false,
    val isEditMode: Boolean = false,
    val offsetY: Float = 0f
)