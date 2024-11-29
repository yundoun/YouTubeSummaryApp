package com.example.youtube_summary_native.core.data.mapper

import com.example.youtube_summary_native.core.data.remote.dto.LoginRequestDto
import com.example.youtube_summary_native.core.data.remote.dto.LoginResponseDto
import com.example.youtube_summary_native.core.data.remote.dto.TokenResponseDto
import com.example.youtube_summary_native.core.data.remote.dto.LogoutRequestDto
import com.example.youtube_summary_native.core.domain.model.auth.LoginResponse
import com.example.youtube_summary_native.core.domain.model.auth.TokenResponse

fun LoginResponseDto.toLoginResponse(): LoginResponse {
    return LoginResponse(
        accessToken = accessToken,
        refreshToken = refreshToken,
        tokenType = tokenType,
        user = user.toUserInfo()
    )
}

fun TokenResponseDto.toTokenResponse(): TokenResponse {
    return TokenResponse(
        accessToken = accessToken,
        refreshToken = refreshToken,
        tokenType = tokenType
    )
}

fun Pair<String, String>.toLoginRequestDto(): LoginRequestDto {
    return LoginRequestDto(
        username = first,
        password = second
    )
}

fun Int.toLogoutRequestDto(): LogoutRequestDto {
    return LogoutRequestDto(userId = this)
}