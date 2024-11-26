package com.example.youtube_summary_native.core.domain.model.user

import kotlinx.serialization.Serializable

@Serializable
data class UserInfoState(
    val status: String? = null,
    val message: String? = null,
    val user: UserInfo? = null,
    val users: List<UserInfo>? = null,
    val isLoggedIn: Boolean,
    val accessToken: String? = null,
    val refreshToken: String? = null
) {
    fun copyWith(
        status: String? = this.status,
        message: String? = this.message,
        user: UserInfo? = this.user,
        users: List<UserInfo>? = this.users,
        isLoggedIn: Boolean = this.isLoggedIn,
        accessToken: String? = this.accessToken,
        refreshToken: String? = this.refreshToken
    ) = UserInfoState(
        status = status,
        message = message,
        user = user,
        users = users,
        isLoggedIn = isLoggedIn,
        accessToken = accessToken,
        refreshToken = refreshToken
    )
}