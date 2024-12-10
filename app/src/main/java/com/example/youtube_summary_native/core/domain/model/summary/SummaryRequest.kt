package com.example.youtube_summary_native.core.domain.model.summary

data class SummaryRequest(
    val url: String,
    val username: String? = null
)