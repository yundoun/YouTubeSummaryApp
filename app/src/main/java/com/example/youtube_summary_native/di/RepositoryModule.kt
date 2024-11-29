package com.example.youtube_summary_native.di


import com.example.youtube_summary_native.core.data.repository.AuthRepositoryImpl
import com.example.youtube_summary_native.core.data.repository.UserRepositoryImpl
import com.example.youtube_summary_native.core.data.repository.SummaryRepositoryImpl
import com.example.youtube_summary_native.core.domain.repository.AuthRepository
import com.example.youtube_summary_native.core.domain.repository.UserRepository
import com.example.youtube_summary_native.core.domain.repository.SummaryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindSummaryRepository(
        summaryRepositoryImpl: SummaryRepositoryImpl
    ): SummaryRepository
}