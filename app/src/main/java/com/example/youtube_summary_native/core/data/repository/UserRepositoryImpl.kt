package com.example.youtube_summary_native.core.data.repository

import com.example.youtube_summary_native.core.data.local.TokenManager
import com.example.youtube_summary_native.core.data.mapper.*
import com.example.youtube_summary_native.core.data.remote.api.UserApi
import com.example.youtube_summary_native.core.domain.model.user.*
import com.example.youtube_summary_native.core.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: UserApi,
    private val tokenManager: TokenManager
) : UserRepository {

    override suspend fun getUser(username: String): UserResponse {
        val token = tokenManager.getAccessToken()
        return api.getUser(username, "Bearer $token").toUserResponse()
    }

    override suspend fun getAllUsers(): AllUsers {
        return api.getAllUsers().toAllUsers()
    }

    override suspend fun createUser(username: String, password: String): UserResponse {
        return api.createUser((username to password).toLoginRequestDto()).toUserResponse()
    }

    override suspend fun deleteUser(userId: Int): UserResponse {
        val token = tokenManager.getAccessToken()
        return api.deleteUser(userId, "Bearer $token").toUserResponse()
    }

    override suspend fun setAdmin(userId: Int): UserResponse {
        val token = tokenManager.getAccessToken()
        return api.setAdmin(userId, "Bearer $token").toUserResponse()
    }
}