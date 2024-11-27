package com.example.youtube_summary_native.core.domain.model.summary

data class SummaryResponse(
    val summaryInfo: SummaryInfo,
    val status: String,
    val orderNumber: Int,
    val errorCode: String,
    val message: String
)