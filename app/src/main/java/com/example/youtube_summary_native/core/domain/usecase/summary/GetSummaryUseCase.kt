package com.example.youtube_summary_native.core.domain.usecase.summary

import com.example.youtube_summary_native.core.domain.model.summary.AllSummaries
import com.example.youtube_summary_native.core.domain.repository.SummaryRepository
import javax.inject.Inject

class GetSummaryUseCase @Inject constructor(
    private val summaryRepository: SummaryRepository
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
            val summaries = summaryRepository.getSummaryInfoAll(username)
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