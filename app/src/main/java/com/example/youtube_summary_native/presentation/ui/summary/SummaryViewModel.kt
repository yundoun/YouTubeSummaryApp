package com.example.youtube_summary_native.presentation.ui.summary

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youtube_summary_native.core.data.remote.dto.ScriptItemDto
import com.example.youtube_summary_native.core.domain.model.summary.ScriptItem
import com.example.youtube_summary_native.core.domain.repository.SummaryRepository
import com.example.youtube_summary_native.core.domain.usecase.summary.GetSummaryUseCase
import com.example.youtube_summary_native.presentation.ui.summary.state.ShareState
import com.example.youtube_summary_native.presentation.ui.summary.state.SummaryScreenState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getSummaryUseCase: GetSummaryUseCase,
    private val summaryRepository: SummaryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SummaryScreenState())
    val uiState = _uiState.asStateFlow()

    private val videoId: String = checkNotNull(savedStateHandle["videoId"])
    private var youtubePlayer: YouTubePlayer? = null

    private val _currentVideoId = MutableStateFlow(videoId)
    val currentVideoId = _currentVideoId.asStateFlow()

    init {
        Log.d(TAG, "Initializing SummaryViewModel with videoId: $videoId")
        _uiState.update { it.copy(videoId = videoId) }
        _currentVideoId.value = videoId  // 초기 videoId 설정
        loadSummaryData(videoId)
        loadAllSummaries()
    }

    private fun loadSummaryData(targetVideoId: String = videoId) {
        viewModelScope.launch {
            Log.d(TAG, "Starting to load summary data for video: $targetVideoId")
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = summaryRepository.getSummaryInfo(targetVideoId)
//                Log.d(TAG, "Received response: $response")

                val formattedScript = response.summaryInfo.rawScript.let { script ->
                    Log.d(TAG, "Processing raw script: $script")
                    parseAndFormatScript(script)
                }

                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        videoId = targetVideoId,  // videoId 업데이트 추가
                        title = response.summaryInfo.title,
                        summaryContent = if (response.summaryInfo.summary.startsWith("Error:")) {
                            Log.w(TAG, "Summary contains error: ${response.summaryInfo.summary}")
                            ""
                        } else response.summaryInfo.summary,
                        scriptContent = formattedScript,
                        error = null
                    )
                }
                Log.d(TAG, "Updated UI state successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading summary data", e)
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message
                ) }
            }
        }
    }

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true // 추가: 입력값 강제 변환 허용
    }

    private fun loadAllSummaries() {
        viewModelScope.launch {
            try {
                val allSummaries = summaryRepository.getSummaryInfoAll(null)
                _uiState.update { it.copy(
                    summaryList = allSummaries.summaryList
                ) }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading all summaries", e)
                // 에러 처리는 현재 상태를 유지하고 로그만 남깁니다
            }
        }
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

    private fun parseAndFormatScript(rawScript: String): String {
        return try {
            if (rawScript.isBlank()) {
                Log.w(TAG, "Raw script is blank")
                return ""
            }

            Log.d(TAG, "Attempting to parse script: $rawScript")

            val scriptItems = try {
                // rawScript가 이미 JSON 배열 문자열이므로 직접 파싱
                json.decodeFromString<List<ScriptItemDto>>(rawScript)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse script", e)
                emptyList()
            }

            if (scriptItems.isEmpty()) {
                Log.w(TAG, "No script items parsed")
                return ""
            }

            Log.d(TAG, "Successfully parsed ${scriptItems.size} script items")

            scriptItems.joinToString("\n") { script ->
                "[${formatSeconds(script.start)}] ${script.text}"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Script parsing error", e)
            e.printStackTrace()
            "스크립트 파싱 중 오류가 발생했습니다."
        }
    }

    @SuppressLint("DefaultLocale")
    private fun formatSeconds(seconds: Double): String {
        val hours = seconds.toInt() / 3600
        val minutes = (seconds.toInt() % 3600) / 60
        val secs = seconds.toInt() % 60
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }

    fun onTimeClicked(timeStr: String) {
        // 나중에 YouTube Player 구현 시 사용
        val parts = timeStr.split(":")
        if (parts.size == 3) {
            val seconds = parts[0].toInt() * 3600 +
                    parts[1].toInt() * 60 +
                    parts[2].toInt()
            // TODO: Player seekTo(seconds)
        }
    }


    fun onPlayerReady(player: YouTubePlayer) {
        youtubePlayer = player
    }

    fun seekTo(seconds: Float) {
        youtubePlayer?.seekTo(seconds)
    }

    // Caption 관련 기능
    fun toggleCaptionVisibility() {
        _uiState.update {
            it.copy(
                captionState = it.captionState.copy(
                    isVisible = !it.captionState.isVisible
                )
            )
        }
    }

    fun updateCaptionPosition(position: Long) {
        _uiState.update {
            it.copy(
                captionState = it.captionState.copy(
                    currentPosition = position
                )
            )
        }
    }

    // Tab 관련 기능
    fun setCurrentTab(index: Int) {
        _uiState.update { it.copy(currentTab = index) }
    }

    // Drawer 관련 기능
    fun toggleDrawer() {
        _uiState.update { it.copy(isDrawerOpen = !it.isDrawerOpen) }
    }

    // Share 관련 기능
    fun updateShareData(title: String, summaryText: String) {
        _uiState.update {
            it.copy(
                shareState = ShareState(
                    title = title,
                    videoId = videoId,
                    summaryText = summaryText
                )
            )
        }
    }

    companion object {
        private const val TAG = "SummaryViewModel"
    }
}