package com.example.youtube_summary_native.core.domain.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    @SerialName("status")
    val status: String,

    @SerialName("message")
    val message: String,

    @SerialName("data")
    val data: UserInfo?
) {
    fun copyWith(
        status: String = this.status,
        message: String = this.message,
        data: UserInfo? = this.data
    ) = UserResponse(status, message, data)
}