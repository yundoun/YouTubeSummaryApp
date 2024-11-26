package com.example.youtube_summary_native.core.domain.usecase.summary

import com.example.youtube_summary_native.core.domain.repository.SummaryRepository
import javax.inject.Inject

class DeleteSummaryUseCase @Inject constructor(
    private val summaryRepository: SummaryRepository
) {
    sealed class Result {
        data class Success(val message: String) : Result()
        data class Error(val exception: Exception) : Result()
    }

    suspend fun deleteSingle(videoId: String, username: String? = null): Result {
        return try {
            val response = summaryRepository.deleteSummaryInfo(videoId, username)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun deleteAll(): Result {
        return try {
            val response = summaryRepository.deleteSummaryInfoAll()
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}