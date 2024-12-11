package com.example.youtube_summary_native.core.domain.usecase.summary

import android.content.ContentValues.TAG
import android.util.Log
import com.example.youtube_summary_native.core.data.remote.websocket.WebSocketMessage
import com.example.youtube_summary_native.core.domain.model.summary.SummaryResponse
import com.example.youtube_summary_native.core.domain.repository.SummaryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        Log.d(TAG, "Starting summary request for video: $videoId")
        emit(Result.Loading)

        try {
            // 1. 웹소켓 연결
            Log.d(TAG, "Connecting WebSocket...")
            summaryRepository.connectToWebSocket()
            Log.d(TAG, "Sending WebSocket message: $videoId")
            summaryRepository.sendWebSocketMessage(videoId)

            // 2. HTTP 요청과 웹소켓 메시지를 병렬로 처리
            coroutineScope {
                launch {
                    // HTTP 요약 요청
                    Log.d(TAG, "Sending HTTP request...")
                    val response = summaryRepository.postSummaryInfo(url, username)
                    Log.d(TAG, "HTTP request successful")
                    emit(Result.Success(response))
                }

                launch {
                    // 웹소켓 메시지 수신
                    Log.d(TAG, "Starting WebSocket message collection")
                    webSocketMessages.collect { message ->
                        Log.d(TAG, "Received message: $message")
                        when (message) {
                            is WebSocketMessage.Summary -> {
                                Log.d(TAG, "Processing summary: ${message.content}")
                                emit(Result.Progress(message.content))
                            }
                            is WebSocketMessage.Complete -> {
                                Log.d(TAG, "Summary complete")
                                emit(Result.Complete)
                                withContext(NonCancellable) {
                                    summaryRepository.closeWebSocket()
                                }
                            }
                            is WebSocketMessage.Error -> {
                                Log.e(TAG, "WebSocket error: ${message.message}")
                                emit(Result.Error(Exception(message.message)))
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in summary request", e)
            emit(Result.Error(e))
            withContext(NonCancellable) {
                summaryRepository.closeWebSocket()
            }
        }
    }.flowOn(Dispatchers.IO)

    fun closeWebSocket() {
        summaryRepository.closeWebSocket()
    }
}