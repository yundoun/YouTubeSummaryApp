package com.example.youtube_summary_native.presentation.ui.auth.state

import com.example.youtube_summary_native.core.domain.model.user.UserInfo

sealed class AuthState {
    data object Initial : AuthState()
    data object Loading : AuthState()

    data class Authenticated(
        val userInfo: UserInfo,
        val accessToken: String,
        val refreshToken: String
    ) : AuthState()

    data class Unauthenticated(
        val message: String? = null
    ) : AuthState()

    data class Error(
        val message: String
    ) : AuthState()

    data class RegistrationSuccess(
        val message: String
    ) : AuthState()
}

data class AuthUiState(
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)