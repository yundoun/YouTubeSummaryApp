package com.example.youtube_summary_native.core.domain.model.summary

data class SummaryInfoState(
    val summary: AllSummaries? = null,
    val content: SummaryResponse? = null,
    val scriptTexts: List<String>? = null,
    val lastVideoId: String? = null,
    val videoIdList: List<String>? = null,
    val isWebSocketConnected: Boolean,
    val isLoading: Boolean
)