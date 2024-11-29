package com.example.youtube_summary_native.core.data.remote.api

import com.example.youtube_summary_native.core.constants.ApiConstants
import com.example.youtube_summary_native.core.data.remote.dto.*
import retrofit2.http.*

interface AuthApi {
    @POST(ApiConstants.AUTH_LOGIN_ENDPOINT)
    suspend fun login(
        @Body loginRequest: LoginRequestDto
    ): LoginResponseDto

    @POST(ApiConstants.AUTH_REFRESH_ENDPOINT)
    suspend fun refreshToken(
        @Body refreshTokenRequest: RefreshTokenRequestDto
    ): TokenResponseDto

    @POST(ApiConstants.AUTH_LOGOUT_ENDPOINT)
    suspend fun logout(
        @Body logoutRequest: LogoutRequestDto,
        @Header("Authorization") token: String
    )
}