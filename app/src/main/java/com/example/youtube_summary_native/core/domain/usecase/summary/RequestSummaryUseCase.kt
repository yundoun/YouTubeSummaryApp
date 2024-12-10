package com.example.youtube_summary_native.core.domain.usecase.summary

import com.example.youtube_summary_native.core.domain.model.summary.SummaryResponse
import com.example.youtube_summary_native.core.domain.repository.SummaryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RequestSummaryUseCase @Inject constructor(
    private val summaryRepository: SummaryRepository
) {
    sealed class Result {
        data class Success(
            val summaryResponse: SummaryResponse
        ) : Result()
        data class Error(val exception: Exception) : Result()
        object Loading : Result()
    }

    val webSocketMessages: Flow<Pair<String, String>> = summaryRepository.webSocketMessages

    suspend operator fun invoke(url: String, videoId: String, username: String? = null): Result {
        return try {
            // 1. POST 요청으로 요약 처리 시작
            val response = summaryRepository.postSummaryInfo(url, username)

            // 2. 요약용 웹소켓 연결
            if (response.status != "failed") {
                summaryRepository.connectToWebSocket()
                summaryRepository.sendWebSocketMessage(videoId)
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