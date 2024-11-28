package com.example.youtube_summary_native.core.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

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
    private val _script: JsonElement, // JsonElement로 받아서 처리

    @SerialName("thumbnailUrl")
    val thumbnailUrl: String,

    @SerialName("status")
    val status: String,

    @SerialName("created_at")
    val createdAt: String
) {
    val rawScript: String
        get() = when (_script) {
            is JsonArray -> Json.encodeToString(_script) // 직접 배열이 왔을 경우
            is JsonPrimitive -> _script.content // 문자열로 된 JSON이 왔을 경우
            else -> "[]"
        }
}

@Serializable
data class ScriptItemDto(
    @SerialName("text")
    val text: String,

    @SerialName("start")
    val start: Double,

    @SerialName("duration")
    val duration: Double
)

