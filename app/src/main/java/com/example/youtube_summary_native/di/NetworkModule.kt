package com.example.youtube_summary_native.core.di

import android.util.Log
import com.example.youtube_summary_native.config.env.AppConfig
import com.example.youtube_summary_native.core.data.remote.api.SummaryApi
import com.example.youtube_summary_native.core.data.remote.api.UserApi
import com.example.youtube_summary_native.core.data.remote.interceptor.AuthInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val TIMEOUT_SECONDS = 10L

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
//            level = HttpLoggingInterceptor.Level.BODY
            level = HttpLoggingInterceptor.Level.BASIC // 중요한 정보만 출력
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                val response = chain.proceed(request)
                Log.d("NetworkModule", "Request URL: ${request.url}")
                Log.d("NetworkModule", "Response Code: ${response.code}")
                Log.d("NetworkModule", "Response Body: ${response.peekBody(Long.MAX_VALUE).string()}")
                response
            }
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
            coerceInputValues = true
            prettyPrint = true
        }
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AppConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideSummaryApi(retrofit: Retrofit): SummaryApi {
        return retrofit.create(SummaryApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }
}