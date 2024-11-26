package com.example.youtube_summary_native.core.domain.repository


import com.example.youtube_summary_native.core.domain.model.summary.AllSummaries
import com.example.youtube_summary_native.core.domain.model.summary.SummaryResponse
import kotlinx.coroutines.flow.Flow

interface SummaryRepository {
    val webSocketMessages: Flow<Pair<String, String>>

    suspend fun getSummaryInfoAll(username: String? = null): AllSummaries
    suspend fun getSummaryInfo(videoId: String): SummaryResponse
    suspend fun postSummaryInfo(keyUrl: String, username: String? = null): SummaryResponse
    suspend fun deleteSummaryInfo(videoId: String, username: String? = null): String
    suspend fun deleteSummaryInfoAll(): String

    fun connectToWebSocket()
    fun sendWebSocketMessage(message: String)
    fun closeWebSocket()
}