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

    suspend operator fun invoke(
        url: String,
        username: String? = null,
        videoId: String
    ): Result {
        return try {
            summaryRepository.connectToWebSocket()
            summaryRepository.sendWebSocketMessage(videoId)

            val response = summaryRepository.postSummaryInfo(url, username)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e)
        } finally {
            summaryRepository.closeWebSocket()
        }
    }

    fun closeWebSocket() {
        summaryRepository.closeWebSocket()
    }
}