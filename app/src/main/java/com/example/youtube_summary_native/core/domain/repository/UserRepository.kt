package com.example.youtube_summary_native.core.domain.repository

import com.example.youtube_summary_native.core.domain.model.user.*

interface UserRepository {
    suspend fun getUser(username: String): UserResponse

    suspend fun getAllUsers(): AllUsers

    suspend fun createUser(username: String, password: String): UserResponse

    suspend fun deleteUser(userId: Int): UserResponse

    suspend fun setAdmin(userId: Int): UserResponse
}