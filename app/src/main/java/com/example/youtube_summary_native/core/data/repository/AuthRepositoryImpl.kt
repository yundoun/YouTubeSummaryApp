package com.example.youtube_summary_native.core.data.repository

import com.example.youtube_summary_native.core.data.local.TokenManager
import com.example.youtube_summary_native.core.data.mapper.*
import com.example.youtube_summary_native.core.data.remote.api.AuthApi
import com.example.youtube_summary_native.core.data.remote.dto.RefreshTokenRequestDto
import com.example.youtube_summary_native.core.domain.model.auth.LoginResponse
import com.example.youtube_summary_native.core.domain.model.auth.TokenResponse
import com.example.youtube_summary_native.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(username: String, password: String): LoginResponse {
        val response = api.login((username to password).toLoginRequestDto())
        // 토큰 저장
        tokenManager.saveTokens(response.accessToken, response.refreshToken)
        return response.toLoginResponse()
    }

    override suspend fun logout(userId: Int) {
        val token = tokenManager.getAccessToken()
        api.logout(
            userId.toLogoutRequestDto(),
            "Bearer $token"
        )
        tokenManager.clearTokens()
    }

    override suspend fun refreshToken(token: String): TokenResponse {
        val response = api.refreshToken(
            RefreshTokenRequestDto(refreshToken = token)
        )
        tokenManager.saveTokens(response.accessToken, response.refreshToken)
        return response.toTokenResponse()
    }

    override fun getAccessToken(): Flow<String?> = tokenManager.accessToken

    override fun getRefreshToken(): Flow<String?> = tokenManager.refreshToken
}