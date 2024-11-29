package com.example.youtube_summary_native.core.domain.model.auth

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String
)