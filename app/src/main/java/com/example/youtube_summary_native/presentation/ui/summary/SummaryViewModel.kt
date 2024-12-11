package com.example.youtube_summary_native.presentation.ui.summary

import android.annotation.SuppressLint
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
            loadSummaryData(videoId)
        }
    }

    private fun loadSummaryData(videoId: String) {
        Log.d(TAG, "Starting to load summary data for video: $videoId")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                when (val result = getSummaryUseCase.getSummaryById(videoId)) {
                    is GetSummaryUseCase.DetailResult.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                title = result.summaryResponse.summaryInfo.title,
                                summaryContent = result.summaryResponse.summaryInfo.summary,
                                scriptContent = result.summaryResponse.summaryInfo.rawScript
                            )
                        }
                    }
                    is GetSummaryUseCase.DetailResult.Error -> {
                        _uiState.update { it.copy(
                            isLoading = false,
                            error = result.exception.message
                        )}
                    }
                    is GetSummaryUseCase.DetailResult.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message
                )}
            }
        }
    }

    fun requestSummary(url: String) {
        summaryJob?.cancel()
        summaryJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                requestSummaryUseCase(
                    url = url,
                    videoId = _uiState.value.videoId
                ).flowOn(Dispatchers.IO)
                    .catch { e ->
                        _uiState.update { it.copy(
                            isLoading = false,
                            error = e.message
                        )}
                    }
                    .collect { result ->
                        when (result) {
                            is RequestSummaryUseCase.Result.Loading -> {
                                _uiState.update { it.copy(isLoading = true) }
                            }
                            is RequestSummaryUseCase.Result.Success -> {
                                _uiState.update { state ->
                                    state.copy(
                                        title = result.summaryResponse.summaryInfo.title
                                    )
                                }
                            }
                            is RequestSummaryUseCase.Result.Progress -> {
                                _uiState.update { state ->
                                    state.copy(
                                        summaryContent = result.content,
                                        isLoading = true
                                    )
                                }
                            }
                            is RequestSummaryUseCase.Result.Complete -> {
                                _uiState.update { it.copy(isLoading = false) }
                                loadSummaryData(_uiState.value.videoId)
                            }
                            is RequestSummaryUseCase.Result.Error -> {
                                _uiState.update { it.copy(
                                    isLoading = false,
                                    error = result.exception.message
                                )}
                            }
                        }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
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

    @SuppressLint("DefaultLocale")
    private fun formatSeconds(seconds: Double): String {
        val hours = seconds.toInt() / 3600
        val minutes = (seconds.toInt() % 3600) / 60
        val secs = seconds.toInt() % 60
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
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

