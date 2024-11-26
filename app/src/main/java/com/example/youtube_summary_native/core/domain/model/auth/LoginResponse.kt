package com.example.youtube_summary_native.core.domain.model.auth

import com.example.youtube_summary_native.core.domain.model.user.UserInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("access_token")
    val accessToken: String,

    @SerialName("refresh_token")
    val refreshToken: String,

    @SerialName("token_type")
    val tokenType: String,

    @SerialName("user")
    val user: UserInfo
)