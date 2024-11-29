package com.example.youtube_summary_native.core.data.remote.api

import com.example.youtube_summary_native.core.constants.ApiConstants
import com.example.youtube_summary_native.core.data.remote.dto.AllSummariesDto
import com.example.youtube_summary_native.core.data.remote.dto.SummaryResponseDto
import com.example.youtube_summary_native.core.data.repository.SummaryRequest
import retrofit2.http.*

interface SummaryApi {
    @GET(ApiConstants.SUMMARY_CONTENT_ALL_ENDPOINT)
    suspend fun getSummaryInfoAll(
        @Query("username") username: String?,
        @Header("Authorization") authorization: String? = null
    ): AllSummariesDto

    @GET(ApiConstants.SUMMARY_CONTENT_ENDPOINT)
    suspend fun getSummaryInfo(
        @Query("video_id") videoId: String,
        @Header("Authorization") authorization: String? = null
    ): SummaryResponseDto

    @POST(ApiConstants.SUMMARY_CONTENT_ENDPOINT)
    suspend fun postSummaryInfo(
        @Body request: SummaryRequest,
        @Header("Authorization") authorization: String? = null
    ): SummaryResponseDto

    @DELETE(ApiConstants.SUMMARY_CONTENT_ENDPOINT)
    suspend fun deleteSummaryInfo(
        @Query("video_id") videoId: String,
        @Query("username") username: String?,
        @Header("Authorization") authorization: String? = null
    ): String

    @DELETE(ApiConstants.SUMMARY_CONTENT_ALL_ENDPOINT)
    suspend fun deleteSummaryInfoAll(
        @Header("Authorization") authorization: String? = null
    ): String
}