package com.example.youtube_summary_native.core.data.remote.interceptor

import com.example.youtube_summary_native.core.data.local.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            tokenManager.getAccessToken()
        }

        val request = chain.request().newBuilder()
        request.addHeader("Accept", "application/json")

        // 토큰이 있는 경우에만 Authorization 헤더 추가
        token?.let {
            request.addHeader("Authorization", "Bearer $it")
        }

        return chain.proceed(request.build())
    }
}