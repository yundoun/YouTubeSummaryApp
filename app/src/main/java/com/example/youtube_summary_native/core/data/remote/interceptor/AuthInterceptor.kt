package com.example.youtube_summary_native.core.data.remote.interceptor

import com.example.youtube_summary_native.core.constants.ApiConstants
import com.example.youtube_summary_native.core.data.local.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    private val publicEndpoints = listOf(
        ApiConstants.SUMMARY_CONTENT_ALL_ENDPOINT,
        ApiConstants.SUMMARY_CONTENT_ENDPOINT
    )

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = request.newBuilder()
            .addHeader("Accept", "application/json")

        // 공개 엔드포인트가 아닌 경우에만 토큰 추가
        if (!isPublicEndpoint(request.url.encodedPath)) {
            val token = runBlocking {
                tokenManager.getAccessToken()
            }
            token?.let {
                requestBuilder.addHeader("Authorization", "Bearer $it")
            }
        }

        return chain.proceed(requestBuilder.build())
    }

    private fun isPublicEndpoint(path: String): Boolean {
        return publicEndpoints.any { endpoint ->
            path.endsWith(endpoint)
        }
    }
}