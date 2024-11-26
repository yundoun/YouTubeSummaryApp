package com.example.youtube_summary_native.core.data.remote.api

import com.example.youtube_summary_native.core.data.remote.dto.LoginResponseDto
import com.example.youtube_summary_native.core.data.remote.dto.TokenResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(
        @Body request: Map<String, String>
    ): LoginResponseDto

    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body request: Map<String, String>
    ): TokenResponseDto
}