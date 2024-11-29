package com.example.youtube_summary_native.di

import com.example.youtube_summary_native.core.domain.repository.AuthRepository
import com.example.youtube_summary_native.core.domain.repository.SummaryRepository
import com.example.youtube_summary_native.core.domain.repository.UserRepository
import com.example.youtube_summary_native.core.domain.usecase.auth.*
import com.example.youtube_summary_native.core.domain.usecase.summary.*
import com.example.youtube_summary_native.core.domain.usecase.user.CreateUserUseCase
import com.example.youtube_summary_native.core.domain.usecase.user.DeleteUserUseCase
import com.example.youtube_summary_native.core.domain.usecase.user.GetAllUsersUseCase
import com.example.youtube_summary_native.core.domain.usecase.user.GetUserUseCase
import com.example.youtube_summary_native.core.domain.usecase.user.SetAdminUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    // 기존 UseCase
    @Provides
    @Singleton
    fun provideGetSummaryUseCase(
        summaryRepository: SummaryRepository
    ): GetSummaryUseCase = GetSummaryUseCase(summaryRepository)

    @Provides
    @Singleton
    fun provideDeleteSummaryUseCase(
        summaryRepository: SummaryRepository
    ): DeleteSummaryUseCase = DeleteSummaryUseCase(summaryRepository)

    @Provides
    @Singleton
    fun provideRequestSummaryUseCase(
        summaryRepository: SummaryRepository
    ): RequestSummaryUseCase = RequestSummaryUseCase(summaryRepository)

    // Auth UseCase
    @Provides
    @Singleton
    fun provideLoginUseCase(
        authRepository: AuthRepository
    ): LoginUseCase = LoginUseCase(authRepository)

    @Provides
    @Singleton
    fun provideLogoutUseCase(
        authRepository: AuthRepository
    ): LogoutUseCase = LogoutUseCase(authRepository)

    @Provides
    @Singleton
    fun provideRefreshTokenUseCase(
        authRepository: AuthRepository
    ): RefreshTokenUseCase = RefreshTokenUseCase(authRepository)

    @Provides
    @Singleton
    fun provideGetTokensUseCase(
        authRepository: AuthRepository
    ): GetTokensUseCase = GetTokensUseCase(authRepository)

    // User UseCase
    @Provides
    @Singleton
    fun provideCreateUserUseCase(
        userRepository: UserRepository
    ): CreateUserUseCase = CreateUserUseCase(userRepository)

    @Provides
    @Singleton
    fun provideGetUserUseCase(
        userRepository: UserRepository
    ): GetUserUseCase = GetUserUseCase(userRepository)

    @Provides
    @Singleton
    fun provideGetAllUsersUseCase(
        userRepository: UserRepository
    ): GetAllUsersUseCase = GetAllUsersUseCase(userRepository)

    @Provides
    @Singleton
    fun provideDeleteUserUseCase(
        userRepository: UserRepository
    ): DeleteUserUseCase = DeleteUserUseCase(userRepository)

    @Provides
    @Singleton
    fun provideSetAdminUseCase(
        userRepository: UserRepository
    ): SetAdminUseCase = SetAdminUseCase(userRepository)
}