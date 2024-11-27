package com.example.youtube_summary_native.core.domain.model.summary

data class AllSummaries(
    val summaryList: List<SummaryInfo> = emptyList(),  // 기본값 설정
    val status: String,
    val errorCode: String? = null,
    val message: String
)