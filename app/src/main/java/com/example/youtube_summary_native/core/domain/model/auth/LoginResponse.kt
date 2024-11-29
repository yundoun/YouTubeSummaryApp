package com.example.youtube_summary_native.core.domain.model.auth

import com.example.youtube_summary_native.core.domain.model.user.UserInfo

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val user: UserInfo
)