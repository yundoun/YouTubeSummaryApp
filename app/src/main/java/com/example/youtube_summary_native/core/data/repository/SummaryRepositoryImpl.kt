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
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import javax.inject.Inject

class SummaryRepositoryImpl @Inject constructor(
    private val summaryApi: SummaryApi,
    private val webSocketManager: WebSocketManager,
    private val tokenManager: TokenManager
) : SummaryRepository {
    private val _webSocketMessages = MutableSharedFlow<Pair<String, String>>()
    override val webSocketMessages: Flow<Pair<String, String>> = _webSocketMessages

    val isConnected: Flow<Boolean> = webSocketManager.isConnected

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
            val requestDto = SummaryRequest(keyUrl, username).toDto()  // Domain 모델을 DTO로 변환
            summaryApi.postSummaryInfo(
                request = requestDto,
                authorization = getAuthorizationHeader()
            ).toDomain()
        } catch (e: Exception) {
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

    // WebSocket 관련 메서드 수정
    override fun connectToWebSocket() {
        webSocketManager.connectSummaryWebSocket()
        // 웹소켓 메시지 콜백 설정
        webSocketManager.onDataReceived { type, data ->
            scope.launch {
                _webSocketMessages.emit(Pair(type, data))
            }
        }
    }

    override fun sendWebSocketMessage(videoId: String) {
        webSocketManager.sendSummaryMessage(videoId)
    }

    override fun closeWebSocket() {
        webSocketManager.closeSummaryWebSocket()
    }

    companion object {
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
}