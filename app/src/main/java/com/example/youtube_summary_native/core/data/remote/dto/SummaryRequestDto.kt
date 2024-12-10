package com.example.youtube_summary_native.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SummaryRequestDto(
    val url: String,
    val username: String? = null
)