package com.example.youtube_summary_native.core.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youtube_summary_native.core.domain.usecase.auth.GetTokensUseCase
import com.example.youtube_summary_native.core.domain.usecase.auth.LoginUseCase
import com.example.youtube_summary_native.core.domain.usecase.auth.LogoutUseCase
import com.example.youtube_summary_native.core.domain.usecase.auth.RefreshTokenUseCase
import com.example.youtube_summary_native.core.domain.usecase.user.CreateUserUseCase
import com.example.youtube_summary_native.presentation.ui.auth.state.AuthEvent
import com.example.youtube_summary_native.presentation.ui.auth.state.AuthState
import com.example.youtube_summary_native.presentation.ui.auth.state.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase,
    private val getTokensUseCase: GetTokensUseCase,
    private val createUserUseCase: CreateUserUseCase,
    @Named("authMutableStateFlow") private val _authState: MutableStateFlow<AuthState>
) : ViewModel() {
    // authState를 외부에 노출
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            getTokensUseCase.getAccessToken().collect { token ->
                if (token == null) {
                    _authState.value = AuthState.Unauthenticated()
                }
            }
        }
    }

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.OnUsernameChange -> {
                _uiState.update { it.copy(username = event.username) }
            }
            is AuthEvent.OnPasswordChange -> {
                _uiState.update { it.copy(password = event.password) }
            }
            is AuthEvent.OnConfirmPasswordChange -> {
                _uiState.update { it.copy(confirmPassword = event.confirmPassword) }
            }
            AuthEvent.OnLogin -> login()
            AuthEvent.OnRegister -> register()
            AuthEvent.OnLogout -> logout()
            AuthEvent.OnDismissError -> dismissError()
        }
    }

    private fun register() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }

                // 비밀번호 확인 검증
                if (uiState.value.password != uiState.value.confirmPassword) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "비밀번호가 일치하지 않습니다."
                        )
                    }
                    return@launch
                }

                // 회원가입 요청
                val result = createUserUseCase(
                    username = uiState.value.username,
                    password = uiState.value.password
                )

                _authState.value = AuthState.RegistrationSuccess("회원가입이 완료되었습니다.")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        username = "",
                        password = "",
                        confirmPassword = ""
                    )
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "회원가입에 실패했습니다.")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                _authState.value = AuthState.Loading

                val response = loginUseCase(
                    username = uiState.value.username,
                    password = uiState.value.password
                )

                _authState.value = AuthState.Authenticated(
                    userInfo = response.user,
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        username = "",
                        password = ""
                    )
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error occurred")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            try {
                val currentState = authState.value
                if (currentState is AuthState.Authenticated) {
                    _uiState.update { it.copy(isLoading = true) }
                    logoutUseCase(currentState.userInfo.id)
                    _authState.value = AuthState.Unauthenticated("로그아웃되었습니다.")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "로그아웃 실패")
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun refreshToken(token: String) {
        viewModelScope.launch {
            try {
                val response = refreshTokenUseCase(token)
                val currentState = _authState.value
                if (currentState is AuthState.Authenticated) {
                    _authState.value = currentState.copy(
                        accessToken = response.accessToken,
                        refreshToken = response.refreshToken
                    )
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Token refresh failed")
            }
        }
    }
}