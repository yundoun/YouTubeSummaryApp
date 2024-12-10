package com.example.youtube_summary_native.core.data.repository

import android.util.Log
import com.example.youtube_summary_native.core.data.local.TokenManager
import com.example.youtube_summary_native.core.data.mapper.toDomain
import com.example.youtube_summary_native.core.data.remote.api.SummaryApi
import com.example.youtube_summary_native.core.data.remote.websocket.WebSocketManager
import com.example.youtube_summary_native.core.domain.model.summary.AllSummaries
import com.example.youtube_summary_native.core.domain.model.summary.SummaryResponse
import com.example.youtube_summary_native.core.domain.repository.SummaryRepository
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.Serializable
import javax.inject.Inject

class SummaryRepositoryImpl @Inject constructor(
    private val summaryApi: SummaryApi,
    private val webSocketManager: WebSocketManager,
    private val tokenManager: TokenManager
) : SummaryRepository {

    // Flutter에서 _onDataReceived 콜백을 통해 데이터를 받았던 부분을
    // MutableSharedFlow로 변환하여 외부로 알림
    private val _webSocketMessages = MutableSharedFlow<Pair<String, String>>()
    override val webSocketMessages: Flow<Pair<String, String>> = _webSocketMessages

    // 만약 연결 상태도 외부로 알리고 싶다면, WebSocketManager의 isConnected StateFlow를 노출할 수 있음
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
            val request = SummaryRequest(keyUrl, username)
            summaryApi.postSummaryInfo(
                request = request,
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

    // Flutter 코드에서 connectToWebSocket()과 유사한 기능
    // WebSocket 연결 및 메시지 콜백 설정
    override fun connectToWebSocket() {
        // WebSocketManager 초기화(연결) 및 메시지 수신 콜백 등록
        webSocketManager.initialize { type, data ->
            _webSocketMessages.tryEmit(Pair(type, data))
        }
    }

    // Flutter 코드의 sendWebSocketMessage
    override fun sendWebSocketMessage(message: String) {
        webSocketManager.sendMessage(message)
    }

    // Flutter 코드의 closeWebSocket
    override fun closeWebSocket() {
        webSocketManager.close()
    }

    // Flutter 코드에서 retryConnection에 해당하는 로직이 필요하다면 추가
    fun retryConnection() {
        webSocketManager.retryConnection()
    }
}

@Serializable
data class SummaryRequest(
    val url: String,
    val username: String?
)
