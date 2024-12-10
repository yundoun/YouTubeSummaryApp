package com.example.youtube_summary_native.core.domain.usecase.summary

import com.example.youtube_summary_native.core.data.remote.websocket.WebSocketManager
import com.example.youtube_summary_native.core.domain.model.summary.SummaryResponse
import com.example.youtube_summary_native.core.domain.repository.SummaryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RequestSummaryUseCase @Inject constructor(
    private val summaryRepository: SummaryRepository
) {
    sealed class Result {
        data class Success(
            val summaryResponse: SummaryResponse,
            val isNewSummary: Boolean
        ) : Result()
        data class Error(val exception: Exception) : Result()
        object Loading : Result()
    }

    // WebSocket 메시지 및 상태 관련 Flow
    val webSocketMessages: Flow<Pair<String, String>> = summaryRepository.webSocketMessages
    val connectionState: Flow<WebSocketManager.ConnectionState> = summaryRepository.connectionState

    suspend operator fun invoke(url: String, videoId: String, username: String? = null): Result {
        return try {
            Result.Loading

            // 1. POST 요청으로 요약 처리 시작
            val response = summaryRepository.postSummaryInfo(url, username)

            // 2. 응답 상태 확인 및 웹소켓 연결
            if (response.status != "failed") {
                val isNewSummary = response.summaryInfo.summary.isEmpty()

                if (isNewSummary) {
                    // 새로운 요약이 필요한 경우만 웹소켓 연결
                    summaryRepository.connectToWebSocket(videoId)
                    summaryRepository.sendWebSocketMessage(videoId)
                }

                Result.Success(response, isNewSummary)
            } else {
                Result.Error(Exception("Failed to request summary: ${response.message}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    fun closeWebSocket() {
        try {
            summaryRepository.closeWebSocket()
        } catch (e: Exception) {
            // 웹소켓 종료 실패는 무시하되 로깅
            e.printStackTrace()
        }
    }

    // 웹소켓 재연결이 필요한 경우를 위한 메서드
    fun reconnectWebSocket(videoId: String) {
        summaryRepository.connectToWebSocket(videoId)
        summaryRepository.sendWebSocketMessage(videoId)
    }
}