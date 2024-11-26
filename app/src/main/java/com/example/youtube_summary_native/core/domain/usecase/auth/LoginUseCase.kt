package com.example.youtube_summary_native.core.domain.usecase.auth

import com.example.youtube_summary_native.core.domain.model.auth.LoginResponse
import com.example.youtube_summary_native.core.domain.repository.UserRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    sealed class Result {
        data class Success(
            val loginResponse: LoginResponse
        ) : Result()
        data class Error(val exception: Exception) : Result()
        object Loading : Result()
    }

    suspend operator fun invoke(
        username: String,
        password: String
    ): Result {
        return try {
            val response = userRepository.login(username, password)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}