package com.example.youtube_summary_native.core.domain.model.summary

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AllSummaries(
    @SerialName("summary_list")
    val summaryList: List<SummaryInfo>,

    @SerialName("status")
    val status: String,

    @SerialName("error_code")
    val errorCode: String,

    @SerialName("message")
    val message: String
)