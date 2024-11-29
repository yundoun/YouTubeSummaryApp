package com.example.youtube_summary_native.presentation.ui.auth.state

sealed class AuthEvent {
    data class OnUsernameChange(val username: String) : AuthEvent()
    data class OnPasswordChange(val password: String) : AuthEvent()
    data class OnConfirmPasswordChange(val confirmPassword: String) : AuthEvent()
    data object OnLogin : AuthEvent()
    data object OnRegister : AuthEvent()
    data object OnLogout : AuthEvent()
    data object OnDismissError : AuthEvent()
}