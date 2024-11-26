package com.example.youtube_summary_native.core.domain.usecase.auth

import com.example.youtube_summary_native.core.domain.model.auth.TokenResponse
import com.example.youtube_summary_native.core.domain.repository.UserRepository
import javax.inject.Inject

class RefreshTokenUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    sealed class Result {
        data class Success(
            val tokenResponse: TokenResponse
        ) : Result()
        data class Error(val exception: Exception) : Result()
        object Loading : Result()
    }

    suspend operator fun invoke(refreshToken: String): Result {
        return try {
            val response = userRepository.refreshToken(refreshToken)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}