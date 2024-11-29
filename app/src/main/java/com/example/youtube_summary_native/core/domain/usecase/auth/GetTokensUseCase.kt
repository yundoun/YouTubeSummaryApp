package com.example.youtube_summary_native.core.domain.usecase.auth

import com.example.youtube_summary_native.core.domain.repository.AuthRepository
import javax.inject.Inject

class GetTokensUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    fun getAccessToken() = repository.getAccessToken()
    fun getRefreshToken() = repository.getRefreshToken()
}