package com.example.youtube_summary_native.di

import android.content.Context
import com.example.youtube_summary_native.presentation.ui.auth.state.AuthState

import com.example.youtube_summary_native.util.NetworkMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideContext(
        @ApplicationContext context: Context
    ): Context = context

    @Provides
    @Singleton
    fun provideNetworkMonitor(
        context: Context
    ): NetworkMonitor = NetworkMonitor(context)

    @Provides
    @Singleton
    @Named("authMutableStateFlow")
    fun provideAuthMutableStateFlow(): MutableStateFlow<AuthState> {
        return MutableStateFlow(AuthState.Initial)
    }
}