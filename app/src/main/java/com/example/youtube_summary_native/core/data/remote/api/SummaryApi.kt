package com.example.youtube_summary_native.core.data.remote.api

import com.example.youtube_summary_native.core.constants.ApiConstants
import com.example.youtube_summary_native.core.data.remote.dto.AllSummariesDto
import com.example.youtube_summary_native.core.data.remote.dto.SummaryResponseDto
import com.example.youtube_summary_native.core.data.repository.SummaryRequest
import com.example.youtube_summary_native.core.domain.model.summary.AllSummaries
import com.example.youtube_summary_native.core.domain.model.summary.SummaryResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SummaryApi {
    @GET(ApiConstants.SUMMARY_CONTENT_ALL_ENDPOINT)
    suspend fun getSummaryInfoAll(
        @Query("username") username: String?
    ): AllSummariesDto

    @GET(ApiConstants.SUMMARY_CONTENT_ENDPOINT)
    suspend fun getSummaryInfo(
        @Query("video_id") videoId: String
    ): SummaryResponseDto

    @POST(ApiConstants.SUMMARY_CONTENT_ENDPOINT)
    suspend fun postSummaryInfo(
        @Body request: SummaryRequest
    ): SummaryResponseDto

    @DELETE(ApiConstants.SUMMARY_CONTENT_ENDPOINT)
    suspend fun deleteSummaryInfo(
        @Query("video_id") videoId: String,
        @Query("username") username: String?
    ): String

    @DELETE(ApiConstants.SUMMARY_CONTENT_ALL_ENDPOINT)
    suspend fun deleteSummaryInfoAll(): String
}