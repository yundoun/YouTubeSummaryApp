package com.example.youtube_summary_native.core.domain.model.user

data class AllUsers(
    val status: String,
    val message: String,
    val data: List<UserInfo>
)