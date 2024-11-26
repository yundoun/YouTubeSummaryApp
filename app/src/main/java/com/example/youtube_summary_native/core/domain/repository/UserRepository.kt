package com.example.youtube_summary_native.core.domain.repository

import com.example.youtube_summary_native.core.domain.model.auth.LoginResponse
import com.example.youtube_summary_native.core.domain.model.auth.TokenResponse
import com.example.youtube_summary_native.core.domain.model.user.AllUsers
import com.example.youtube_summary_native.core.domain.model.user.UserResponse

interface UserRepository {
    suspend fun postUserInfo(username: String, password: String): UserResponse
    suspend fun getUserInfoAll(): AllUsers
    suspend fun getUserInfo(username: String): UserResponse
    suspend fun deleteUserInfo(id: Int): UserResponse

    suspend fun login(username: String, password: String): LoginResponse
    suspend fun refreshToken(refreshToken: String): TokenResponse
    suspend fun logout(userId: Int)
    suspend fun isLoggedIn(): Boolean
}