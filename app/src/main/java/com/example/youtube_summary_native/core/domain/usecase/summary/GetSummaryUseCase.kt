package com.example.youtube_summary_native.core.domain.usecase.summary

import com.example.youtube_summary_native.core.data.local.TokenManager
import com.example.youtube_summary_native.core.domain.model.summary.AllSummaries
import com.example.youtube_summary_native.core.domain.repository.SummaryRepository
import javax.inject.Inject

class GetSummaryUseCase @Inject constructor(
    private val summaryRepository: SummaryRepository,
    private val tokenManager: TokenManager
) {
    sealed class Result {
        data class Success(
            val summaries: AllSummaries
        ) : Result()
        data class Error(val exception: Exception) : Result()
        data object Loading : Result()
    }

    suspend operator fun invoke(username: String? = null): Result {
        return try {
            // 토큰이 있는 경우에만 username 전달
            val token = tokenManager.getAccessToken()
            val effectiveUsername = if (token != null) username else null
            val summaries = summaryRepository.getSummaryInfoAll(effectiveUsername)
            Result.Success(summaries)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getSummaryById(videoId: String): Result {
        return try {
            val summary = summaryRepository.getSummaryInfo(videoId)
            Result.Success(AllSummaries(
                summaryList = listOf(summary.summaryInfo),
                status = summary.status,
                errorCode = summary.errorCode,
                message = summary.message
            ))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}