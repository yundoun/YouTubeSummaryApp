package com.example.youtube_summary_native.core.domain.usecase.auth

import com.example.youtube_summary_native.core.domain.repository.AuthRepository
import javax.inject.Inject

class RefreshTokenUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(token: String) = repository.refreshToken(token)
}