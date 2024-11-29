package com.example.youtube_summary_native.core.domain.model.user

data class UserInfoState(
    val status: String? = null,
    val message: String? = null,
    val user: UserInfo? = null,
    val users: List<UserInfo>? = null,
    val isLoggedIn: Boolean,
    val accessToken: String? = null,
    val refreshToken: String? = null
)