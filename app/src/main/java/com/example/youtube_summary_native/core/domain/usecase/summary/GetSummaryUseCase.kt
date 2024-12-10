package com.example.youtube_summary_native.core.domain.usecase.summary

import com.example.youtube_summary_native.core.data.local.TokenManager
import com.example.youtube_summary_native.core.domain.model.summary.AllSummaries
import com.example.youtube_summary_native.core.domain.model.summary.SummaryResponse
import com.example.youtube_summary_native.core.domain.repository.SummaryRepository
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
        data class Error(val exception: Exception) : DetailResult()
        data object Loading : DetailResult()
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

    // 단일 조회
    suspend fun getSummaryById(videoId: String): DetailResult {
        return try {
            val summary = summaryRepository.getSummaryInfo(videoId)
            DetailResult.Success(summary)
        } catch (e: Exception) {
            DetailResult.Error(e)
        }
    }
}