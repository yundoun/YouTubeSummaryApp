package com.example.youtube_summary_native.core.domain.usecase.summary

import android.content.ContentValues.TAG
import android.util.Log
import com.example.youtube_summary_native.core.data.remote.websocket.WebSocketMessage
import com.example.youtube_summary_native.core.domain.model.summary.SummaryResponse
import com.example.youtube_summary_native.core.domain.repository.SummaryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

class RequestSummaryUseCase @Inject constructor(
    private val summaryRepository: SummaryRepository
) {
    sealed class Result {
        data class Success(val summaryResponse: SummaryResponse) : Result()
        data class Progress(val content: String) : Result()
        object Complete : Result()
        data class Error(val exception: Exception) : Result()
        object Loading : Result()
    }

    // Flow를 WebSocketMessage 타입으로 변경
    val webSocketMessages: Flow<WebSocketMessage> = summaryRepository.webSocketMessages

    suspend operator fun invoke(
        url: String,
        videoId: String,
        username: String? = null
    ): Flow<Result> = flow {
        emit(Result.Loading)

        try {
            // 1. WebSocket 연결
            Log.d(TAG, "Starting WebSocket connection")
            summaryRepository.connectToWebSocket(videoId)

            // 2. Connected 메시지 대기
            var isWebSocketConnected = false
            withTimeout(5000) { // 5초 타임아웃
                webSocketMessages.collect { message ->
                    when (message) {
                        is WebSocketMessage.Connected -> {
                            Log.d(TAG, "WebSocket Connected message received")
                            isWebSocketConnected = true
                            return@collect  // Connected 메시지를 받으면 collect 종료
                        }
                        else -> { }
                    }
                }
            }

            if (!isWebSocketConnected) {
                throw Exception("WebSocket connection timeout")
            }

            // 3. HTTP POST 요청 전송
            Log.d(TAG, "Starting HTTP POST request")
            val response = summaryRepository.postSummaryInfo(url, username)
            Log.d(TAG, "HTTP POST request completed")

            // 4. WebSocket으로 진행상황 모니터링
            Log.d(TAG, "Starting progress monitoring")
            webSocketMessages.collect { message ->
                when (message) {
                    is WebSocketMessage.Summary -> {
                        Log.d(TAG, "Received summary progress")
                        emit(Result.Progress(message.content))
                    }
                    is WebSocketMessage.Complete -> {
                        Log.d(TAG, "Summary complete")
                        emit(Result.Success(response))
                        emit(Result.Complete)
                        return@collect
                    }
                    is WebSocketMessage.Error -> {
                        throw Exception(message.message)
                    }
                    else -> { }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in summary request", e)
            emit(Result.Error(e))
        } finally {
            withContext(NonCancellable) {
                summaryRepository.closeWebSocket()
            }
        }
    }

    fun closeWebSocket() {
        summaryRepository.closeWebSocket()
    }
}