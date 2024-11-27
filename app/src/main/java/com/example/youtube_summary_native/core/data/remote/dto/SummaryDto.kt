package com.example.youtube_summary_native.core.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SummaryResponseDto(
    @SerialName("summary_info")
    val summaryInfo: SummaryInfoDto,

    @SerialName("status")
    val status: String,

    @SerialName("order_number")
    val orderNumber: Int,

    @SerialName("error_code")
    val errorCode: String,

    @SerialName("message")
    val message: String
)

@Serializable
data class AllSummariesDto(
    @SerialName("summary_list")
    val summaryList: List<SummaryInfoDto>? = null,

    @SerialName("status")
    val status: String,

    @SerialName("error_code")
    val errorCode: String? = null,

    @SerialName("message")
    val message: String
)

@Serializable
data class SummaryInfoDto(
    @SerialName("videoId")
    val videoId: String,

    @SerialName("title")
    val title: String,

    @SerialName("summary")
    val summary: String,

    @SerialName("script")
    val rawScript: String,

    @SerialName("thumbnailUrl")
    val thumbnailUrl: String,

    @SerialName("status")
    val status: String,

    @SerialName("created_at")
    val createdAt: String
)

@Serializable
data class ScriptItemDto(
    @SerialName("text")
    val text: String,

    @SerialName("start")
    val start: Double,

    @SerialName("duration")
    val duration: Double
)