package com.example.youtube_summary_native.core.presentation.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youtube_summary_native.core.domain.model.state.HomeScreenState
import com.example.youtube_summary_native.core.domain.usecase.auth.LoginUseCase
import com.example.youtube_summary_native.core.domain.usecase.summary.DeleteSummaryUseCase
import com.example.youtube_summary_native.core.domain.usecase.summary.GetSummaryUseCase
import com.example.youtube_summary_native.util.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val getSummaryUseCase: GetSummaryUseCase,
    private val deleteSummaryUseCase: DeleteSummaryUseCase,
    private val networkMonitor: NetworkMonitor
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
                    getSummaryUseCase().let { result ->
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