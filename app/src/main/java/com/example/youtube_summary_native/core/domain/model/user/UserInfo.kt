package com.example.youtube_summary_native.core.domain.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    @SerialName("id")
    val id: Int,

    @SerialName("username")
    val username: String,

    @SerialName("password")
    val password: String? = null,

    @SerialName("is_admin")
    val isAdmin: Boolean
)