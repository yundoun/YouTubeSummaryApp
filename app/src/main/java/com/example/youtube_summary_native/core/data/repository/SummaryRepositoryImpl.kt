package com.example.youtube_summary_native.core.data.repository

import android.util.Log
import com.example.youtube_summary_native.core.data.local.TokenManager
import com.example.youtube_summary_native.core.data.mapper.toDomain
import com.example.youtube_summary_native.core.data.mapper.toDto
import com.example.youtube_summary_native.core.data.remote.api.SummaryApi
import com.example.youtube_summary_native.core.data.remote.websocket.WebSocketManager
import com.example.youtube_summary_native.core.domain.model.summary.AllSummaries
import com.example.youtube_summary_native.core.domain.model.summary.SummaryRequest
import com.example.youtube_summary_native.core.domain.model.summary.SummaryResponse
import com.example.youtube_summary_native.core.domain.repository.SummaryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SummaryRepositoryImpl @Inject constructor(
    private val summaryApi: SummaryApi,
    private val webSocketManager: WebSocketManager,
    private val tokenManager: TokenManager
) : SummaryRepository {
    private val _webSocketMessages = MutableSharedFlow<Pair<String, String>>()
    override val webSocketMessages: Flow<Pair<String, String>> = _webSocketMessages

    private val _connectionState = MutableStateFlow<WebSocketManager.ConnectionState>(
        WebSocketManager.ConnectionState.Disconnected
    )
    override val connectionState: StateFlow<WebSocketManager.ConnectionState> = _connectionState

    init {
        // WebSocketManager의 상태 모니터링
        scope.launch {
            webSocketManager.connectionState.collect { state ->
                _connectionState.value = state
            }
        }
    }

    private suspend fun getAuthorizationHeader(): String? {
        val token = tokenManager.getAccessToken()
        return if (token != null) "Bearer $token" else null
    }

    override suspend fun getSummaryInfoAll(username: String?): AllSummaries {
        return try {
            val auth = if (username != null) getAuthorizationHeader() else null
            val response = summaryApi.getSummaryInfoAll(
                username = username,
                authorization = auth
            )
            response.toDomain()
        } catch (e: Exception) {
            Log.e(TAG, "Error in getSummaryInfoAll", e)
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
            Log.e(TAG, "Failed to load summary info", e)
            throw Exception("Failed to load summary info: ${e.message}")
        }
    }

    override suspend fun postSummaryInfo(keyUrl: String, username: String?): SummaryResponse {
        return try {
            val requestDto = SummaryRequest(keyUrl, username).toDto()
            val response = summaryApi.postSummaryInfo(requestDto)

            if (response.summaryInfo.summary.isEmpty()) {
                val videoId = extractVideoId(keyUrl)
                initializeWebSocketConnection(videoId)
            }

            response.toDomain()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to post summary info", e)
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
            response.message
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete summary info", e)
            throw Exception("Failed to delete summary info: ${e.message}")
        }
    }

    override suspend fun deleteSummaryInfoAll(): String {
        return try {
            summaryApi.deleteSummaryInfoAll(
                authorization = getAuthorizationHeader()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete all summaries", e)
            throw Exception("Failed to delete all summaries: ${e.message}")
        }
    }

    private fun initializeWebSocketConnection(videoId: String) {
        webSocketManager.initialize { type, data ->
            scope.launch {
                _webSocketMessages.emit(Pair(type, data))
            }
        }
        connectToWebSocket(videoId)
    }

    override fun connectToWebSocket(videoId: String) {
        Log.d(TAG, "Connecting to WebSocket with videoId: $videoId")
        webSocketManager.connectSummaryWebSocket(videoId)
    }

    override fun sendWebSocketMessage(videoId: String) {
        Log.d(TAG, "Sending WebSocket message for videoId: $videoId")
        webSocketManager.sendSummaryMessage(videoId)
    }

    override fun closeWebSocket() {
        Log.d(TAG, "Closing WebSocket connection")
        webSocketManager.closeAll()
    }

    private fun extractVideoId(url: String): String {
        // URL에서 videoId 추출 로직
        val regex = "(?<=v=)[a-zA-Z0-9_-]+".toRegex()
        return regex.find(url)?.value ?: throw IllegalArgumentException("Invalid YouTube URL")
    }

    companion object {
        private const val TAG = "SummaryRepositoryImpl"
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
}