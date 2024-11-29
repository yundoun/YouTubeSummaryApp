package com.example.youtube_summary_native.core.domain.repository

import com.example.youtube_summary_native.core.domain.model.auth.LoginResponse
import com.example.youtube_summary_native.core.domain.model.auth.TokenResponse
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(username: String, password: String): LoginResponse

    suspend fun logout(userId: Int)

    suspend fun refreshToken(token: String): TokenResponse

    fun getAccessToken(): Flow<String?>

    fun getRefreshToken(): Flow<String?>
}