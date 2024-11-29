package com.example.youtube_summary_native.presentation.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youtube_summary_native.core.domain.model.state.HomeScreenState
import com.example.youtube_summary_native.core.domain.model.user.UserInfo
import com.example.youtube_summary_native.core.domain.usecase.auth.LoginUseCase
import com.example.youtube_summary_native.core.domain.usecase.summary.DeleteSummaryUseCase
import com.example.youtube_summary_native.core.domain.usecase.summary.GetSummaryUseCase
import com.example.youtube_summary_native.core.domain.usecase.summary.RequestSummaryUseCase
import com.example.youtube_summary_native.core.presentation.auth.AuthViewModel
import com.example.youtube_summary_native.presentation.ui.auth.state.AuthState
import com.example.youtube_summary_native.util.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getSummaryUseCase: GetSummaryUseCase,
    private val deleteSummaryUseCase: DeleteSummaryUseCase,
    private val requestSummaryUseCase: RequestSummaryUseCase,
    private val networkMonitor: NetworkMonitor,
    private val authViewModel: AuthViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeScreenState())
    val uiState: StateFlow<HomeScreenState> = _uiState

    private val _isOfflineMode = MutableStateFlow(false)
    val isOfflineMode: StateFlow<Boolean> = _isOfflineMode

    init {
        viewModelScope.launch {
            // 네트워크 모니터 초기화
            networkMonitor.initialize()
            // 초기 상태 설정
            _isOfflineMode.value = !networkMonitor.isOnline()
            // 최초 데이터 로드
            initializeHomeScreen()
            // 네트워크 상태 관찰 시작
            observeNetworkState()
            // Auth 상태 관찰 시작
            observeAuthState()
        }
    }

    private fun initializeHomeScreen() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                // 네트워크 모니터 초기화
                networkMonitor.initialize()
                val isConnected = networkMonitor.isOnline()
                _isOfflineMode.value = !isConnected

                if (isConnected) {
                    getSummaryUseCase(
                        username = uiState.value.userInfo?.username
                    ).let { result ->
                        when (result) {
                            is GetSummaryUseCase.Result.Success -> {
                                _uiState.update { currentState ->
                                    currentState.copy(
                                        homeScrollState = true,
                                        summaries = result.summaries.summaryList ?: emptyList(),
                                        isLoading = false
                                    )
                                }
                            }
                            is GetSummaryUseCase.Result.Error -> {
                                _uiState.update { it.copy(isLoading = false) }
                                Log.e("HomeViewModel", "Failed to get summaries", result.exception)
                            }
                            GetSummaryUseCase.Result.Loading -> {
                                _uiState.update { it.copy(isLoading = true) }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                Log.e("HomeViewModel", "Failed to initialize home screen", e)
            }
        }
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authViewModel.authState.collect { authState ->
                when (authState) {
                    is AuthState.Authenticated -> {
                        updateLoginState(
                            isLoginUser = true,
                            isAdmin = authState.userInfo.isAdmin,
                            userInfo = authState.userInfo
                        )
                        // 로그인 상태에서 데이터 새로고침
                        initializeHomeScreen()
                    }
                    is AuthState.Unauthenticated -> {
                        updateLoginState(
                            isLoginUser = false,
                            isAdmin = false,
                            userInfo = null
                        )
                        // 로그아웃 상태에서 데이터 새로고침
                        initializeHomeScreen()
                    }
                    else -> {
                        // 다른 상태 처리
                    }
                }
            }
        }
    }

    fun updateLoginState(isLoginUser: Boolean, isAdmin: Boolean, userInfo: UserInfo?) {
        _uiState.update { currentState ->
            currentState.copy(
                isLoginUser = isLoginUser,
                isAdmin = isAdmin,
                userInfo = userInfo
            )
        }
    }

    fun requestSummary(url: String, videoId: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                when (val result = requestSummaryUseCase(
                    url = url,
                    videoId = videoId,
                    username = uiState.value.userInfo?.username // 로그인된 사용자의 username 전달
                )) {
                    is RequestSummaryUseCase.Result.Success -> {
                        _uiState.update { it.copy(isLoading = false) }
                        Log.d("HomeViewModel", "Summary request successful")
                        initializeHomeScreen()
                    }
                    is RequestSummaryUseCase.Result.Error -> {
                        _uiState.update { it.copy(isLoading = false) }
                        Log.e("HomeViewModel", "Summary request failed", result.exception)
                    }
                    is RequestSummaryUseCase.Result.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                Log.e("HomeViewModel", "Failed to request summary", e)
            }
        }
    }


    private fun observeNetworkState() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isOnline ->
                val wasOffline = _isOfflineMode.value
                _isOfflineMode.value = !isOnline
                // 오프라인에서 온라인으로 전환된 경우에만 데이터 새로고침
                if (isOnline && wasOffline) {
                    initializeHomeScreen()
                }
            }
        }
    }

    fun deleteSummary(videoId: String) {
        viewModelScope.launch {
            when (val result = deleteSummaryUseCase.deleteSingle(videoId)) {
                is DeleteSummaryUseCase.Result.Success -> {
                    initializeHomeScreen()
                }
                is DeleteSummaryUseCase.Result.Error -> {
                    // Handle error
                }
            }
        }
    }

    fun deleteAllSummaries() {
        viewModelScope.launch {
            when (val result = deleteSummaryUseCase.deleteAll()) {
                is DeleteSummaryUseCase.Result.Success -> {
                    initializeHomeScreen()
                }
                is DeleteSummaryUseCase.Result.Error -> {
                    // Handle error
                }
            }
        }
    }

    fun setSearchText(text: String) {
        val videoId = extractVideoId(text)
        _uiState.update { currentState ->
            currentState.copy(
                videoId = videoId,
                searchBarState = currentState.searchBarState.copy(
                    isEmpty = text.isEmpty()
                )
            )
        }
    }

    fun setSearchFocus(focused: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                searchBarState = currentState.searchBarState.copy(
                    isFocused = focused
                )
            )
        }
    }

    fun toggleEditMode() {
        _uiState.update { currentState ->
            currentState.copy(
                recentSummaryState = currentState.recentSummaryState.copy(
                    isEditMode = !currentState.recentSummaryState.isEditMode
                )
            )
        }
    }

    fun switchGridSortState() {
        _uiState.update { currentState ->
            currentState.copy(
                recentSummaryState = currentState.recentSummaryState.copy(
                    gridSortState = !currentState.recentSummaryState.gridSortState
                )
            )
        }
    }

    fun setGridScrollState(scrolling: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                recentSummaryState = currentState.recentSummaryState.copy(
                    gridScrollState = scrolling
                )
            )
        }
    }

    fun setHomeScrollState(scrolling: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                homeScrollState = scrolling
            )
        }
    }

    fun retryConnection() {
        initializeHomeScreen()
    }

    override fun onCleared() {
        super.onCleared()
        requestSummaryUseCase.closeWebSocket()
    }

    private fun extractVideoId(url: String): String {
        // Simple YouTube URL parsing logic
        return when {
            url.contains("youtu.be/") -> url.substringAfter("youtu.be/").substringBefore("?")
            url.contains("youtube.com/watch?v=") -> url.substringAfter("v=").substringBefore("&")
            url.contains("youtube.com/v/") -> url.substringAfter("v/").substringBefore("?")
            url.contains("youtube.com/embed/") -> url.substringAfter("embed/").substringBefore("?")
            else -> ""
        }
    }
}