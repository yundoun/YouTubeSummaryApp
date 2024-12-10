package com.example.youtube_summary_native.core.domain.usecase.summary

import com.example.youtube_summary_native.core.domain.model.summary.SummaryRequest
import com.example.youtube_summary_native.core.domain.model.summary.SummaryResponse
import com.example.youtube_summary_native.core.domain.repository.SummaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

class ProcessSummaryUseCase @Inject constructor(
    private val summaryRepository: SummaryRepository
) {
    sealed class Result {
        data class Success(val summaryResponse: SummaryResponse) : Result()
        data class Error(val exception: Exception) : Result()
        object Loading : Result()
    }

    private val _webSocketMessages = MutableSharedFlow<Pair<String, String>>()
    val webSocketMessages = _webSocketMessages.asSharedFlow()

    suspend operator fun invoke(url: String, videoId: String, username: String? = null): Result {
        return try {
            // 1. 요약용 웹소켓 연결
            summaryRepository.connectToWebSocket()

            // 2. video_id 전송
            summaryRepository.sendWebSocketMessage(videoId)

            // 3. POST 요청으로 요약 처리 시작
            val response = summaryRepository.postSummaryInfo(url, username)

            // 4. 웹소켓 메시지를 Flow로 변환해서 방출
            summaryRepository.webSocketMessages.collect { message ->
                _webSocketMessages.emit(message)
            }

            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    fun closeWebSocket() {
        summaryRepository.closeWebSocket()
    }
}