package com.example.youtube_summary_native.core.domain.usecase.summary

import com.example.youtube_summary_native.core.data.local.TokenManager
import com.example.youtube_summary_native.core.data.remote.websocket.WebSocketMessage
import com.example.youtube_summary_native.core.domain.model.summary.AllSummaries
import com.example.youtube_summary_native.core.domain.model.summary.SummaryResponse
import com.example.youtube_summary_native.core.domain.repository.SummaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetSummaryUseCase @Inject constructor(
    private val summaryRepository: SummaryRepository,
    private val tokenManager: TokenManager
) {
    // 전체 목록 조회를 위한 Result
    sealed class Result {
        data class Success(val summaries: AllSummaries) : Result()
        data class Error(val exception: Exception) : Result()
        data object Loading : Result()
    }

    // 단일 조회를 위한 Result
    sealed class DetailResult {
        data class Success(val summaryResponse: SummaryResponse) : DetailResult()
        data class Progress(val content: String) : DetailResult()  // 추가
        data class Error(val exception: Exception) : DetailResult()
        object Loading : DetailResult()
    }

    // 전체 목록 조회
    suspend operator fun invoke(username: String? = null): Result {
        return try {
            val token = tokenManager.getAccessToken()
            val effectiveUsername = if (token != null) username else null
            val summaries = summaryRepository.getSummaryInfoAll(effectiveUsername)
            Result.Success(summaries)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // 전체 스크립트만 가져오는 함수 추가
    suspend fun getFullScript(videoId: String): DetailResult {
        return try {
            val summary = summaryRepository.getSummaryInfo(videoId)
            DetailResult.Success(summary)
        } catch (e: Exception) {
            DetailResult.Error(e)
        }
    }

    // 요약 스크립트를 실시간으로 가져오는 함수
    suspend fun getSummaryById(videoId: String): Flow<DetailResult> = flow {
        emit(DetailResult.Loading)

        try {
            // 초기 요약 정보 가져오기
            val initialSummary = summaryRepository.getSummaryInfo(videoId)

            // 초기 전체 스크립트 emit (summary는 비어있을 수 있음)
            emit(DetailResult.Success(initialSummary))

            // WebSocket 연결
            summaryRepository.connectToWebSocket(videoId)

            // WebSocket 메시지 구독 - 요약 스크립트만 처리
            summaryRepository.webSocketMessages
                .collect { message ->
                    when (message) {
                        is WebSocketMessage.Summary -> {
                            emit(DetailResult.Progress(message.content))
                        }
                        is WebSocketMessage.Complete -> {
                            val finalSummary = summaryRepository.getSummaryInfo(videoId)
                            emit(DetailResult.Success(finalSummary))
                        }
                        is WebSocketMessage.Error -> {
                            emit(DetailResult.Error(Exception(message.message)))
                        }
                        else -> {}
                    }
                }
        } catch (e: Exception) {
            emit(DetailResult.Error(e))
        }
    }
}
