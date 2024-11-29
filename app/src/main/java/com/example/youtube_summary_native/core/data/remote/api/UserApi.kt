package com.example.youtube_summary_native.core.data.remote.api

import com.example.youtube_summary_native.core.constants.ApiConstants
import com.example.youtube_summary_native.core.data.remote.dto.*
import retrofit2.http.*

interface UserApi {
    @GET(ApiConstants.SUMMARY_USERS_ENDPOINT)
    suspend fun getUser(
        @Query("username") username: String,
        @Header("Authorization") token: String
    ): UserResponseDto

    @GET(ApiConstants.SUMMARY_USERS_ALL_ENDPOINT)
    suspend fun getAllUsers(): AllUsersResponseDto

    @POST(ApiConstants.SUMMARY_USERS_ENDPOINT)
    suspend fun createUser(
        @Body userRequest: LoginRequestDto
    ): UserResponseDto

    @DELETE(ApiConstants.SUMMARY_USERS_ENDPOINT)
    suspend fun deleteUser(
        @Query("user_id") userId: Int,
        @Header("Authorization") token: String
    ): UserResponseDto

    @PUT("${ApiConstants.SUMMARY_USERS_ENDPOINT}/{userId}/set_admin")
    suspend fun setAdmin(
        @Path("userId") userId: Int,
        @Header("Authorization") token: String
    ): UserResponseDto
}