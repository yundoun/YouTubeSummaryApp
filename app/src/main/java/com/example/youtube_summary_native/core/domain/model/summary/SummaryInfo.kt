package com.example.youtube_summary_native.core.domain.model.summary


data class SummaryInfo(
    val videoId: String,
    val title: String,
    val summary: String,
    val rawScript: String,
    val thumbnailUrl: String,
    val status: String,
    val createdAt: String
)