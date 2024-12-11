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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import com.example.youtube_summary_native.presentation.ui.auth.state.AuthState
import com.example.youtube_summary_native.util.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getSummaryUseCase: GetSummaryUseCase,
    private val deleteSummaryUseCase: DeleteSummaryUseCase,
    private val requestSummaryUseCase: RequestSummaryUseCase,
    private val networkMonitor: NetworkMonitor,
    @Named("authMutableStateFlow") private val authStateFlow: MutableStateFlow<AuthState>
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

                val isConnected = networkMonitor.isOnline()
                _isOfflineMode.value = !isConnected

                if (isConnected) {
                    // 현재 인증 상태 확인
                    val currentAuthState = authStateFlow.value

                    // 인증 상태와 관계없이 getSummaryUseCase 호출
                    getSummaryUseCase(
                        username = if (currentAuthState is AuthState.Authenticated)
                            currentAuthState.userInfo.username
                        else null
                    ).let { result ->
                        when (result) {
                            is GetSummaryUseCase.Result.Success -> {
                                _uiState.update { currentState ->
                                    currentState.copy(
                                        summaries = result.summaries.summaryList,
                                        isLoading = false
                                    )
                                }
                            }
                            is GetSummaryUseCase.Result.Error -> {
                                _uiState.update { it.copy(isLoading = false) }
                                Log.e("HomeViewModel", "Failed to load summaries", result.exception)
                            }
                            GetSummaryUseCase.Result.Loading -> {
                                _uiState.update { it.copy(isLoading = true) }
                            }
                        }
                    }
                } else {
                    _uiState.update { currentState ->
                        currentState.copy(
                            summaries = emptyList(),
                            isLoading = false
                        )
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
            authStateFlow.collect { authState ->
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

    private fun updateLoginState(isLoginUser: Boolean, isAdmin: Boolean, userInfo: UserInfo?) {
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

                requestSummaryUseCase(
                    url = url,
                    videoId = videoId,
                    username = uiState.value.userInfo?.username
                ).flowOn(Dispatchers.IO)
                    .catch { e ->
                        _uiState.update { it.copy(isLoading = false) }
                        Log.e("HomeViewModel", "Summary request failed", e)
                    }
                    .collect { result ->
                        when (result) {
                            is RequestSummaryUseCase.Result.Loading -> {
                                _uiState.update { it.copy(isLoading = true) }
                            }
                            is RequestSummaryUseCase.Result.Success -> {
                                _uiState.update { it.copy(isLoading = true) }
                                Log.d("HomeViewModel", "Summary request successful")
                            }
                            is RequestSummaryUseCase.Result.Progress -> {
                                // 진행 상태일 때는 loading 상태만 유지
                                _uiState.update { it.copy(isLoading = true) }
                            }
                            is RequestSummaryUseCase.Result.Complete -> {
                                _uiState.update { it.copy(isLoading = false) }
                                // 요약이 완료되면 전체 데이터를 새로고침
                                initializeHomeScreen()
                            }
                            is RequestSummaryUseCase.Result.Error -> {
                                _uiState.update { it.copy(isLoading = false) }
                                Log.e("HomeViewModel", "Summary request failed", result.exception)
                            }
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
            val username = uiState.value.userInfo?.username  // 현재 로그인된 사용자의 username 가져오기
            when (val result = deleteSummaryUseCase.deleteSingle(videoId, username)) {
                is DeleteSummaryUseCase.Result.Success -> {
                    initializeHomeScreen()
                }
                is DeleteSummaryUseCase.Result.Error -> {
                    // Handle error
                    Log.e("HomeViewModel", "Failed to delete summary", result.exception)
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