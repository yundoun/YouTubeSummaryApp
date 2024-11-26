package com.example.youtube_summary_native.core.data.repository

import android.util.Log
import com.example.youtube_summary_native.core.data.local.TokenManager
import com.example.youtube_summary_native.core.data.remote.api.UserApi
import com.example.youtube_summary_native.core.domain.model.auth.LoginResponse
import com.example.youtube_summary_native.core.domain.model.auth.TokenResponse
import com.example.youtube_summary_native.core.domain.model.user.AllUsers
import com.example.youtube_summary_native.core.domain.model.user.UserResponse
import com.example.youtube_summary_native.core.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val tokenManager: TokenManager
) : UserRepository {

    override suspend fun postUserInfo(username: String, password: String): UserResponse {
        return try {
            Log.d(TAG, "Posting user info: username=$username")
            userApi.postUserInfo(mapOf(
                "username" to username,
                "password" to password
            ))
        } catch (e: Exception) {
            throw Exception("Failed to post user info: ${e.message}")
        }
    }

    override suspend fun getUserInfoAll(): AllUsers {
        return try {
            userApi.getUserInfoAll()
        } catch (e: Exception) {
            throw Exception("Failed to get all users: ${e.message}")
        }
    }

    override suspend fun getUserInfo(username: String): UserResponse {
        return try {
            Log.d(TAG, "Getting user info for username=$username")
            userApi.getUserInfo(username)
        } catch (e: Exception) {
            throw Exception("Failed to get user info: ${e.message}")
        }
    }

    override suspend fun deleteUserInfo(id: Int): UserResponse {
        return try {
            userApi.deleteUserInfo(id)
        } catch (e: Exception) {
            throw Exception("Failed to delete user: ${e.message}")
        }
    }

    override suspend fun login(username: String, password: String): LoginResponse {
        return try {
            Log.d(TAG, "Login attempt for username=$username")
            val response = userApi.login(mapOf(
                "username" to username,
                "password" to password
            ))

            // TokenManager를 통해 토큰 저장
            tokenManager.saveTokens(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken
            )

            response
        } catch (e: Exception) {
            Log.e(TAG, "Login failed: ${e.message}")
            throw Exception("Failed to login: ${e.message}")
        }
    }

    override suspend fun refreshToken(refreshToken: String): TokenResponse {
        return try {
            val response = userApi.refreshToken(mapOf(
                "refresh_token" to refreshToken
            ))

            tokenManager.saveTokens(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken
            )

            response
        } catch (e: Exception) {
            throw Exception("Failed to refresh token: ${e.message}")
        }
    }

    override suspend fun logout(userId: Int) {
        try {
            userApi.logout(mapOf("user_id" to userId))
            tokenManager.clearTokens()
        } catch (e: Exception) {
            throw Exception("Failed to logout: ${e.message}")
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        return tokenManager.getAccessToken() != null
    }

    companion object {
        private const val TAG = "UserRepository"
    }
}