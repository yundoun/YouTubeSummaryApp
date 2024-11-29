package com.example.youtube_summary_native.core.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfoDto(
    @SerialName("id")
    val id: Int,

    @SerialName("username")
    val username: String,

    @SerialName("is_admin")
    val isAdmin: Boolean
)

@Serializable
data class UserResponseDto(
    @SerialName("status")
    val status: String,

    @SerialName("message")
    val message: String,

    @SerialName("data")
    val data: UserInfoDto?
)

@Serializable
data class AllUsersResponseDto(
    @SerialName("status")
    val status: String,

    @SerialName("message")
    val message: String,

    @SerialName("data")
    val data: List<UserInfoDto>
)