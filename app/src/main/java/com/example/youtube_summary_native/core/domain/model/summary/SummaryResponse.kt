package com.example.youtube_summary_native.core.domain.model.summary

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SummaryResponse(
    @SerialName("summary_info")
    val summaryInfo: SummaryInfo,

    @SerialName("status")
    val status: String,

    @SerialName("order_number")
    val orderNumber: Int,

    @SerialName("error_code")
    val errorCode: String,

    @SerialName("message")
    val message: String
)