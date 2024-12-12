package com.example.youtube_summary_native.presentation.ui.summary

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import com.example.youtube_summary_native.core.domain.usecase.summary.GetSummaryUseCase
import com.example.youtube_summary_native.core.domain.usecase.summary.RequestSummaryUseCase
import com.example.youtube_summary_native.presentation.ui.summary.state.ShareState
import com.example.youtube_summary_native.presentation.ui.summary.state.SummaryScreenState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val getSummaryUseCase: GetSummaryUseCase,
    private val requestSummaryUseCase: RequestSummaryUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(SummaryScreenState())
    val uiState: StateFlow<SummaryScreenState> = _uiState.asStateFlow()

    private var summaryJob: Job? = null
    private var youtubePlayer: YouTubePlayer? = null

    private val videoId: String = checkNotNull(savedStateHandle["videoId"])
    private val _currentVideoId = MutableStateFlow(videoId)
    val currentVideoId = _currentVideoId.asStateFlow()

    init {
        Log.d(TAG, "Initializing SummaryViewModel with videoId: $videoId")
        if (videoId.isNotEmpty()) {
            _uiState.update { it.copy(videoId = videoId) }
            // 전체 스크립트와 요약 스크립트를 동시에 로드
            loadFullScript(videoId)
            loadSummaryData(videoId)
        }
    }

    // 전체 스크립트 로드
    private fun loadFullScript(videoId: String) {
        viewModelScope.launch {
            try {
                when (val result = getSummaryUseCase.getFullScript(videoId)) {
                    is GetSummaryUseCase.DetailResult.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                title = result.summaryResponse.summaryInfo.title,
                                scriptContent = result.summaryResponse.summaryInfo.rawScript
                            )
                        }
                    }
                    is GetSummaryUseCase.DetailResult.Error -> {
                        _uiState.update { state ->
                            state.copy(error = result.exception.message)
                        }
                    }
                    else -> {} // Loading, Progress 상태는 무시
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    // 요약 스크립트 실시간 로드
    private fun loadSummaryData(videoId: String) {
        summaryJob?.cancel()

        summaryJob = viewModelScope.launch {
            try {
                getSummaryUseCase.getSummaryById(videoId)
                    .collect { result ->
                        when (result) {
                            is GetSummaryUseCase.DetailResult.Loading -> {
                                _uiState.update { it.copy(isLoading = true) }
                            }
                            is GetSummaryUseCase.DetailResult.Progress -> {
                                _uiState.update { state ->
                                    state.copy(
                                        isLoading = false,
                                        isProgressing = true,
                                        progressContent = result.content,
                                        summaryContent = result.content
                                    )
                                }
                            }
                            is GetSummaryUseCase.DetailResult.Success -> {
                                _uiState.update { state ->
                                    state.copy(
                                        isLoading = false,
                                        isProgressing = false,
                                        summaryContent = result.summaryResponse.summaryInfo.summary
                                    )
                                }
                            }
                            is GetSummaryUseCase.DetailResult.Error -> {
                                _uiState.update { state ->
                                    state.copy(
                                        isLoading = false,
                                        isProgressing = false,
                                        error = result.exception.message
                                    )
                                }
                            }
                        }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    isProgressing = false,
                    error = e.message
                )}
            }
        }
    }



    override fun onCleared() {
        super.onCleared()
        summaryJob?.cancel()
        requestSummaryUseCase.closeWebSocket()
    }


    fun onVideoSelect(newVideoId: String) {
        if (newVideoId != currentVideoId.value) {
            _currentVideoId.value = newVideoId
            _uiState.update {
                it.copy(
                    isDrawerOpen = false,
                    isLoading = true,
                    title = "",
                    summaryContent = "",
                    scriptContent = "",
                    error = null
                )
            }
            youtubePlayer?.loadVideo(newVideoId, 0f)
            loadSummaryData(newVideoId)
        }
    }

    fun onPlayerReady(player: YouTubePlayer) {
        try {
            youtubePlayer = player
            Log.d(TAG, "YouTube player initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing YouTube player", e)
        }
    }

    fun seekTo(timeInSeconds: Long) {
        try {
            youtubePlayer?.let { player ->
                Log.d(TAG, "Seeking to time: $timeInSeconds seconds")
                player.seekTo(timeInSeconds.toFloat())
            } ?: Log.w(TAG, "YouTube player is not initialized")
        } catch (e: Exception) {
            Log.e(TAG, "Error seeking to time: $timeInSeconds", e)
        }
    }

    // Drawer 관련 기능
    fun toggleDrawer() {
        _uiState.update { it.copy(isDrawerOpen = !it.isDrawerOpen) }
    }


    companion object {
        private const val TAG = "SummaryViewModel"
    }
}

