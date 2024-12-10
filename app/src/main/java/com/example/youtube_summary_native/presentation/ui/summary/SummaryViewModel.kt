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
import com.example.youtube_summary_native.core.domain.usecase.summary.ProcessSummaryUseCase
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
    private val processSummaryUseCase: ProcessSummaryUseCase  // UseCase 주입
) : ViewModel() {

    private val _uiState = MutableStateFlow(SummaryScreenState())
    val uiState = _uiState.asStateFlow()

    private val videoId: String = checkNotNull(savedStateHandle["videoId"])
    private var youtubePlayer: YouTubePlayer? = null

    private val _currentVideoId = MutableStateFlow(videoId)
    private val currentVideoId = _currentVideoId.asStateFlow()



    init {
        Log.d(TAG, "Initializing SummaryViewModel with videoId: $videoId")
        _uiState.update { it.copy(videoId = videoId) }
        _currentVideoId.value = videoId  // 초기 videoId 설정

        // 요약 처리 시작 및 웹소켓 메시지 수신
        initializeSummaryProcess()
        loadSummaryData(videoId)
        loadAllSummaries()
    }

    private fun initializeSummaryProcess() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val url = "https://www.youtube.com/watch?v=$videoId"

                // 요약 프로세스를 별도의 코루틴에서 실행
                launch {
                    processSummaryUseCase.webSocketMessages.collect { (type, data) ->
                        when (type) {
                            "summary" -> {
                                _uiState.update { it.copy(
                                    summaryContent = data,
                                    isLoading = true
                                ) }
                            }
                            "complete" -> {
                                _uiState.update { it.copy(isLoading = false) }
                                loadSummaryData(videoId)
                            }
                        }
                    }
                }

                // 요약 처리 시작
                when (val result = processSummaryUseCase(url, videoId)) {
                    is ProcessSummaryUseCase.Result.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                videoId = videoId,
                                title = result.summaryResponse.summaryInfo.title,
                                summaryContent = "요약을 준비하고 있습니다..." // 초기 메시지
                            )
                        }
                    }
                    is ProcessSummaryUseCase.Result.Error -> {
                        _uiState.update { it.copy(
                            isLoading = false,
                            error = result.exception.message
                        ) }
                    }
                    ProcessSummaryUseCase.Result.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in summary process", e)
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message
                ) }
            }
        }
    }

    private fun loadSummaryData(targetVideoId: String = videoId) {
        viewModelScope.launch {
            Log.d(TAG, "Starting to load summary data for video: $targetVideoId")
            _uiState.update { it.copy(isLoading = true) }
            try {
                when (val result = getSummaryUseCase.getSummaryById(targetVideoId)) {
                    is GetSummaryUseCase.DetailResult.Success -> {
                        val summaryInfo = result.summaryResponse.summaryInfo
                        val formattedScript = parseAndFormatScript(summaryInfo.rawScript)
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                videoId = targetVideoId,
                                title = summaryInfo.title,
                                summaryContent = if (summaryInfo.summary.startsWith("Error:")) {
                                    Log.w(TAG, "Summary contains error: ${summaryInfo.summary}")
                                    ""
                                } else summaryInfo.summary,
                                scriptContent = formattedScript,
                                error = null
                            )
                        }
                    }
                    is GetSummaryUseCase.DetailResult.Error -> {
                        _uiState.update { it.copy(
                            isLoading = false,
                            error = result.exception.message
                        ) }
                    }
                    GetSummaryUseCase.DetailResult.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
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
                when (val result = getSummaryUseCase()) {
                    is GetSummaryUseCase.Result.Success -> {
                        _uiState.update { it.copy(
                            summaryList = result.summaries.summaryList
                        ) }
                    }
                    is GetSummaryUseCase.Result.Error -> {
                        Log.e(TAG, "Error loading all summaries", result.exception)
                    }
                    GetSummaryUseCase.Result.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading all summaries", e)
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

    override fun onCleared() {
        super.onCleared()
        processSummaryUseCase.closeWebSocket()
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

