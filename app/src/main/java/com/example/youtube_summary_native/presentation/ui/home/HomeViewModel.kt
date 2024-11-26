//package com.example.youtube_summary_native.presentation.ui.home
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.youtube_summary_native.core.domain.model.summary.SummaryInfo
//import com.example.youtube_summary_native.core.util.NetworkMonitor
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.update
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//data class HomeUiState(
//    val isOnline: Boolean = true,
//    val isLoggedIn: Boolean = false,
//    val username: String = "",
//    val searchQuery: String = "",
//    val videoId: String = "",
//    val recentSummaries: List<SummaryInfo> = emptyList()
//)
//
//@HiltViewModel
//class HomeViewModel @Inject constructor(
//    private val networkMonitor: NetworkMonitor
//) : ViewModel() {
//
//    private val _uiState = MutableStateFlow(HomeUiState())
//    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
//
//    init {
//        observeNetworkState()
//    }
//
////    private fun observeNetworkState() {
////        viewModelScope.launch {
////            networkMonitor.isOnline.collect { isOnline ->
////                _uiState.update { it.copy(isOnline = isOnline) }
////            }
////        }
////    }
//
//    fun retryConnection() {
//        viewModelScope.launch {
////            networkMonitor.retryConnection()
//        }
//    }
//
//    fun onSearchQueryChange(query: String) {
//        _uiState.update { it.copy(
//            searchQuery = query,
//            videoId = extractVideoId(query)
//        )}
//    }
//
//    fun onLoginClick() {
//        // 로그인 다이얼로그 표시 로직
//    }
//
//    fun onLogoutClick() {
//        // 로그아웃 처리 로직
//    }
//
//    fun onSummaryClick(summaryId: String) {
//        // 요약 상세 페이지로 이동 로직
//    }
//
//    fun onStartSummaryClick() {
//        // 새로운 요약 시작 로직
//    }
//
//    private fun extractVideoId(url: String): String {
//        // YouTube URL에서 videoId 추출 로직
//        return ""
//    }
//}