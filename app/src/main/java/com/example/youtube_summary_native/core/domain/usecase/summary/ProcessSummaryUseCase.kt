package com.example.youtube_summary_native.core.domain.usecase.summary

import com.example.youtube_summary_native.core.domain.model.summary.SummaryRequest
import com.example.youtube_summary_native.core.domain.model.summary.SummaryResponse
import com.example.youtube_summary_native.core.domain.repository.SummaryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

class ProcessSummaryUseCase @Inject constructor(
    private val summaryRepository: SummaryRepository
) {
    companion object {
        private const val CLIENT_TIMEOUT = 180_000L // 1분 30초
    }

    sealed class Result {
        data class Success(val summaryResponse: SummaryResponse) : Result()
        data class Error(val exception: Exception) : Result()
        object Loading : Result()
        object Timeout : Result()
    }

    private val _webSocketMessages = MutableSharedFlow<Pair<String, String>>()
    val webSocketMessages = _webSocketMessages.asSharedFlow()

    suspend operator fun invoke(url: String, videoId: String, username: String? = null): Flow<Result> = flow {
        try {
            emit(Result.Loading)

            // 1. POST 요청으로 요약 처리 시작
            val response = summaryRepository.postSummaryInfo(url, username)

            // 2. summary가 비어있으면 요약 대기
            if (response.summaryInfo.summary.isEmpty()) {
                // 웹소켓 연결 (videoId 전달)
                summaryRepository.connectToWebSocket(videoId)
                summaryRepository.sendWebSocketMessage(videoId)

                withTimeout(CLIENT_TIMEOUT) {
                    var latestSummary = ""
                    summaryRepository.webSocketMessages.collect { message ->
                        _webSocketMessages.emit(message)
                        val (type, data) = message
                        when (type) {
                            "summary" -> {
                                latestSummary = data
                                emit(Result.Success(response.copy(
                                    summaryInfo = response.summaryInfo.copy(
                                        summary = latestSummary
                                    )
                                )))
                            }
                            "complete" -> {
                                // 최종 데이터 로드
                                val finalResponse = summaryRepository.getSummaryInfo(videoId)
                                emit(Result.Success(finalResponse))
                                return@collect
                            }
                            "error" -> {
                                throw Exception(data)
                            }
                        }
                    }
                }
            } else {
                // 이미 요약이 있는 경우
                emit(Result.Success(response))
            }
        } catch (e: TimeoutCancellationException) {
            emit(Result.Error(Exception("요약 처리 시간이 초과되었습니다. 잠시 후 다시 시도해주세요.")))
        } catch (e: Exception) {
            emit(Result.Error(e))
        } finally {
            summaryRepository.closeWebSocket()
        }
    }

    private fun extractVideoId(url: String): String {
        val regex = "(?:v=|youtu.be/)([a-zA-Z0-9_-]{11})".toRegex()
        return regex.find(url)?.groupValues?.get(1)
            ?: throw IllegalArgumentException("올바른 YouTube URL이 아닙니다.")
    }
}