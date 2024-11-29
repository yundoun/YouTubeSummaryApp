package com.example.youtube_summary_native.core.domain.model.user

data class UserResponse(
    val status: String,
    val message: String,
    val data: UserInfo?
)