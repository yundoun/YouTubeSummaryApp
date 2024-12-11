package com.example.youtube_summary_native.core.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.example.youtube_summary_native.core.data.local.TokenManager
import com.example.youtube_summary_native.core.data.mapper.toDomain
import com.example.youtube_summary_native.core.data.remote.api.SummaryApi
import com.example.youtube_summary_native.core.data.remote.websocket.WebSocketManager
import com.example.youtube_summary_native.core.data.remote.websocket.WebSocketMessage
import com.example.youtube_summary_native.core.domain.model.summary.AllSummaries
import com.example.youtube_summary_native.core.domain.model.summary.SummaryResponse
import com.example.youtube_summary_native.core.domain.repository.SummaryRepository
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.serialization.Serializable
import javax.inject.Inject

class SummaryRepositoryImpl @Inject constructor(
    private val summaryApi: SummaryApi,
    private val webSocketManager: WebSocketManager,
    private val tokenManager: TokenManager
) : SummaryRepository {

    private val _webSocketMessages = MutableSharedFlow<WebSocketMessage>()  // 타입 변경
    override val webSocketMessages: Flow<WebSocketMessage> = _webSocketMessages

    private suspend fun getAuthorizationHeader(): String? {
        val token = tokenManager.getAccessToken()
        return if (token != null) "Bearer $token" else null
    }

    override suspend fun getSummaryInfoAll(username: String?): AllSummaries {
        return try {
            // 토큰이 있을 때만 authorization 헤더 추가
            val auth = if (username != null) getAuthorizationHeader() else null
            val response = summaryApi.getSummaryInfoAll(
                username = username,
                authorization = auth
            )
            response.toDomain()
        } catch (e: Exception) {
            Log.e("SummaryRepositoryImpl", "Error in getSummaryInfoAll", e)
            throw Exception("Failed to load all summaries: ${e.message}")
        }
    }

    override suspend fun getSummaryInfo(videoId: String): SummaryResponse {
        return try {
            summaryApi.getSummaryInfo(
                videoId = videoId,
                authorization = getAuthorizationHeader()
            ).toDomain()
        } catch (e: Exception) {
            throw Exception("Failed to load summary info: ${e.message}")
        }
    }

    override suspend fun postSummaryInfo(keyUrl: String, username: String?): SummaryResponse {
        return try {
            Log.d(TAG, "Starting POST request with URL: $keyUrl")
            val request = SummaryRequest(keyUrl, username)
            Log.d(TAG, "Request body: $request")

            val response = summaryApi.postSummaryInfo(
                request = request,
                authorization = getAuthorizationHeader()
            )
            Log.d(TAG, "POST request successful")

            response.toDomain()
        } catch (e: Exception) {
            Log.e(TAG, "Error in postSummaryInfo", e)
            throw Exception("Failed to post summary info: ${e.message}")
        }
    }

    override suspend fun deleteSummaryInfo(videoId: String, username: String?): String {
        return try {
            val response = summaryApi.deleteSummaryInfo(
                videoId = videoId,
                username = username,
                authorization = getAuthorizationHeader()
            )
            response.message  // 응답의 message 필드만 반환
        } catch (e: Exception) {
            throw Exception("Failed to delete summary info: ${e.message}")
        }
    }

    override suspend fun deleteSummaryInfoAll(): String {
        return try {
            summaryApi.deleteSummaryInfoAll(
                authorization = getAuthorizationHeader()
            )
        } catch (e: Exception) {
            throw Exception("Failed to delete all summaries: ${e.message}")
        }
    }

    override fun connectToWebSocket(videoId: String) {
        webSocketManager.connect(videoId) { message ->
            _webSocketMessages.tryEmit(message)
        }
    }

    override fun sendWebSocketMessage(message: String) {
        webSocketManager.sendMessage(message)
    }

    override fun closeWebSocket() {
        webSocketManager.close()
    }
}

@Serializable
data class SummaryRequest(
    val url: String,
    val username: String?
)