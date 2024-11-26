package com.example.youtube_summary_native.core.data.mapper

import com.example.youtube_summary_native.core.data.remote.dto.LoginResponseDto
import com.example.youtube_summary_native.core.data.remote.dto.TokenResponseDto
import com.example.youtube_summary_native.core.data.remote.dto.UserInfoDto
import com.example.youtube_summary_native.core.domain.model.auth.LoginResponse
import com.example.youtube_summary_native.core.domain.model.auth.TokenResponse
import com.example.youtube_summary_native.core.domain.model.user.UserInfo

fun LoginResponseDto.toDomain(): LoginResponse {
    return LoginResponse(
        accessToken = accessToken,
        refreshToken = refreshToken,
        tokenType = tokenType,
        user = user.toDomain()
    )
}

fun TokenResponseDto.toDomain(): TokenResponse {
    return TokenResponse(
        accessToken = accessToken,
        refreshToken = refreshToken,
        tokenType = tokenType
    )
}

fun UserInfoDto.toDomain(): UserInfo {
    return UserInfo(
        id = id,
        username = username,
        password = password,
        isAdmin = isAdmin
    )
}