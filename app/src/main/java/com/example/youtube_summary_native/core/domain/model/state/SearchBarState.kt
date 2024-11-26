package com.example.youtube_summary_native.core.domain.model.state

data class SearchBarState(
    val isEmpty: Boolean = true,
    val isFocused: Boolean = false
)