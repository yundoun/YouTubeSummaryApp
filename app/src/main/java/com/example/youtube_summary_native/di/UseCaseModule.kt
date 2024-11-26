package com.example.youtube_summary_native.core.di

import com.example.youtube_summary_native.core.domain.repository.SummaryRepository
import com.example.youtube_summary_native.core.domain.repository.UserRepository
import com.example.youtube_summary_native.core.domain.usecase.auth.LoginUseCase
import com.example.youtube_summary_native.core.domain.usecase.auth.RefreshTokenUseCase
import com.example.youtube_summary_native.core.domain.usecase.summary.DeleteSummaryUseCase
import com.example.youtube_summary_native.core.domain.usecase.summary.GetSummaryUseCase
import com.example.youtube_summary_native.core.domain.usecase.summary.RequestSummaryUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideLoginUseCase(
        userRepository: UserRepository
    ): LoginUseCase {
        return LoginUseCase(userRepository)
    }

    @Provides
    @Singleton
    fun provideRefreshTokenUseCase(
        userRepository: UserRepository
    ): RefreshTokenUseCase {
        return RefreshTokenUseCase(userRepository)
    }

    @Provides
    @Singleton
    fun provideGetSummaryUseCase(
        summaryRepository: SummaryRepository
    ): GetSummaryUseCase {
        return GetSummaryUseCase(summaryRepository)
    }

    @Provides
    @Singleton
    fun provideRequestSummaryUseCase(
        summaryRepository: SummaryRepository
    ): RequestSummaryUseCase {
        return RequestSummaryUseCase(summaryRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteSummaryUseCase(
        summaryRepository: SummaryRepository
    ): DeleteSummaryUseCase {
        return DeleteSummaryUseCase(summaryRepository)
    }
}