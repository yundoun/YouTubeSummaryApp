package com.example.youtube_summary_native.core.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    @SerialName("username")
    val username: String,

    @SerialName("password")
    val password: String
)

@Serializable
data class LoginResponseDto(
    @SerialName("access_token")
    val accessToken: String,

    @SerialName("refresh_token")
    val refreshToken: String,

    @SerialName("token_type")
    val tokenType: String,

    @SerialName("user")
    val user: UserInfoDto
)

@Serializable
data class TokenResponseDto(
    @SerialName("access_token")
    val accessToken: String,

    @SerialName("refresh_token")
    val refreshToken: String,

    @SerialName("token_type")
    val tokenType: String
)

@Serializable
data class RefreshTokenRequestDto(
    @SerialName("refresh_token")
    val refreshToken: String
)

@Serializable
data class LogoutRequestDto(
    @SerialName("user_id")
    val userId: Int
)