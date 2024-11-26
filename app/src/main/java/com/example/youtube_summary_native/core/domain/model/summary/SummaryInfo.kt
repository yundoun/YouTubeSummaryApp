package com.example.youtube_summary_native.core.domain.model.summary

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SummaryInfo(
    @SerialName("videoId")
    val videoId: String,

    @SerialName("title")
    val title: String,

    @SerialName("summary")
    val summary: String,

    @SerialName("script")
    val script: List<ScriptItem>,

    @SerialName("thumbnailUrl")
    val thumbnailUrl: String,

    @SerialName("status")
    val status: String,

    @SerialName("created_at")
    val createdAt: String
) {
    companion object {
        fun fromDb(
            videoId: String,
            title: String,
            summary: String,
            script: List<ScriptItem>,
            thumbnailUrl: String,
            status: String,
            createdAt: String
        ): SummaryInfo {
            return SummaryInfo(
                videoId = videoId,
                title = title,
                summary = summary,
                script = script,
                thumbnailUrl = thumbnailUrl,
                status = status,
                createdAt = createdAt
            )
        }
    }
}