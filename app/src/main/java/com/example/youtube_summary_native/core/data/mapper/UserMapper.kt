package com.example.youtube_summary_native.core.data.mapper

import com.example.youtube_summary_native.core.data.remote.dto.UserInfoDto
import com.example.youtube_summary_native.core.data.remote.dto.UserResponseDto
import com.example.youtube_summary_native.core.data.remote.dto.AllUsersResponseDto
import com.example.youtube_summary_native.core.domain.model.user.UserInfo
import com.example.youtube_summary_native.core.domain.model.user.UserResponse
import com.example.youtube_summary_native.core.domain.model.user.AllUsers

fun UserInfoDto.toUserInfo(): UserInfo {
    return UserInfo(
        id = id,
        username = username,
        isAdmin = isAdmin
    )
}

fun UserResponseDto.toUserResponse(): UserResponse {
    return UserResponse(
        status = status,
        message = message,
        data = data?.toUserInfo()
    )
}

fun AllUsersResponseDto.toAllUsers(): AllUsers {
    return AllUsers(
        status = status,
        message = message,
        data = data.map { it.toUserInfo() }
    )
}