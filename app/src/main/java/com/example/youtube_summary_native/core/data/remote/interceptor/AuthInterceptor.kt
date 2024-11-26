package com.example.youtube_summary_native.core.data.remote.interceptor

import android.util.Log
import com.example.youtube_summary_native.core.constants.ApiConstants
import com.example.youtube_summary_native.core.data.local.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response = chain.proceed(addAuthHeader(request))

        if (response.code == 401 && !isRefreshTokenRequest(request)) {
            response.close()
            runBlocking {
                tokenManager.refreshToken()?.let {
                    return@runBlocking chain.proceed(addAuthHeader(request))
                }
            }
        }

        return response
    }

    private fun addAuthHeader(request: Request): Request {
        val token = runBlocking { tokenManager.getAccessToken() }
        return if (token != null) {
            request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }
    }

    private fun isRefreshTokenRequest(request: Request): Boolean {
        return request.url.toString().contains(ApiConstants.AUTH_REFRESH_ENDPOINT)
    }

    companion object {
        private const val TAG = "AuthInterceptor"
    }
}