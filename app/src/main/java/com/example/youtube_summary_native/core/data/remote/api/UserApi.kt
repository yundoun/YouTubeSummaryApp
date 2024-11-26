package com.example.youtube_summary_native.core.data.remote.api

import com.example.youtube_summary_native.core.constants.ApiConstants
import com.example.youtube_summary_native.core.domain.model.auth.LoginResponse
import com.example.youtube_summary_native.core.domain.model.auth.TokenResponse
import com.example.youtube_summary_native.core.domain.model.user.AllUsers
import com.example.youtube_summary_native.core.domain.model.user.UserResponse
import retrofit2.http.*

interface UserApi {
    @POST(ApiConstants.SUMMARY_USERS_ENDPOINT)
    suspend fun postUserInfo(
        @Body request: Map<String, String>
    ): UserResponse

    @GET(ApiConstants.SUMMARY_USERS_ALL_ENDPOINT)
    suspend fun getUserInfoAll(): AllUsers

    @GET(ApiConstants.SUMMARY_USERS_ENDPOINT)
    suspend fun getUserInfo(
        @Query("username") username: String
    ): UserResponse

    @DELETE(ApiConstants.SUMMARY_USERS_ENDPOINT)
    suspend fun deleteUserInfo(
        @Query("id") userId: Int
    ): UserResponse

    @POST(ApiConstants.AUTH_LOGIN_ENDPOINT)
    suspend fun login(
        @Body request: Map<String, String>
    ): LoginResponse

    @POST(ApiConstants.AUTH_REFRESH_ENDPOINT)
    suspend fun refreshToken(
        @Body request: Map<String, String>
    ): TokenResponse

    @POST(ApiConstants.AUTH_LOGOUT_ENDPOINT)
    suspend fun logout(
        @Body request: Map<String, Int>
    )
}