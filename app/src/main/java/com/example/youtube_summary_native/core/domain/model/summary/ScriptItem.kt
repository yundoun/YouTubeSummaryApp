package com.example.youtube_summary_native.core.domain.model.summary

import kotlinx.serialization.Serializable

@Serializable
data class ScriptItem(
    val text: String,
    val start: Double,
    val duration: Double
)