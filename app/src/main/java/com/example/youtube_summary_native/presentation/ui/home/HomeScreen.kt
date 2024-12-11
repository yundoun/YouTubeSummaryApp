package com.example.youtube_summary_native.presentation.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.youtube_summary_native.core.constants.ApiConstants
import com.example.youtube_summary_native.core.constants.AppDimensions
import com.example.youtube_summary_native.core.presentation.auth.AuthViewModel
import com.example.youtube_summary_native.core.presentation.ui.common.LoginButton
import com.example.youtube_summary_native.core.presentation.ui.common.dialog.DeleteDialog
import com.example.youtube_summary_native.core.presentation.ui.home.components.HomeRecentSummary
import com.example.youtube_summary_native.presentation.ui.home.widget.HomeSearchBar
import com.example.youtube_summary_native.core.presentation.ui.home.components.HomeStartSummary
import com.example.youtube_summary_native.presentation.ui.auth.state.AuthState

@Composable
fun HomeScreen(
    onNavigateToSummary: (String) -> Unit,
    onNavigateToAuth: () -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel() // AuthViewModel 가져옴
) {
    // 상태를 각 ViewModel에서 관찰합니다.
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val isOfflineMode by homeViewModel.isOfflineMode.collectAsStateWithLifecycle()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // LazyListState for Recent Summary Grid
    val listState = rememberLazyListState()

    // Dialog state
    var showDeleteDialog by remember { mutableStateOf(false) }
    var videoIdToDelete by remember { mutableStateOf<String?>(null) }

    // Monitor scroll state
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                homeViewModel.setGridScrollState(index > 0)
            }
    }

    // 로그인 상태를 화면에서 처리하고 HomeViewModel에 상태 전달
    val isLoginUser = authState is AuthState.Authenticated
    val isAdmin = if (authState is AuthState.Authenticated) {
        (authState as AuthState.Authenticated).userInfo.isAdmin
    } else false

    // Main scaffold
    Scaffold(
        floatingActionButton = {
            if (!uiState.homeScrollState && uiState.recentSummaryState.gridScrollState) {
                FloatingActionButton(
                    onClick = {
                        homeViewModel.setHomeScrollState(true)
                        homeViewModel.setGridScrollState(false)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Scroll to top"
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Login section
                if (!isOfflineMode) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppDimensions.SmallPadding),
                        horizontalArrangement = Arrangement.End
                    ) {
                        LoginButton(
                            onLoginClick = onNavigateToAuth,
                            isOfflineMode = isOfflineMode
                        )
                    }
                }

                // Main content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = if (screenWidth > AppDimensions.ResponsiveWidth) {
                                (screenWidth / 7).coerceAtMost(AppDimensions.Dim300)
                            } else {
                                AppDimensions.DefaultPadding
                            }
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Search Bar
                    HomeSearchBar(
                        homeScreenState = uiState,
                        onTextChanged = homeViewModel::setSearchText,
                        onTextClear = { homeViewModel.setSearchText("") },
                        onPaste = { /* Implement clipboard paste */ },
                        onFocusChanged = homeViewModel::setSearchFocus,
                        isOfflineMode = isOfflineMode,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Offline mode retry button
                    if (isOfflineMode) {
                        Button(
                            onClick = homeViewModel::retryConnection,
                            modifier = Modifier.padding(top = AppDimensions.LargePadding)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Retry connection"
                            )
                            Spacer(modifier = Modifier.width(AppDimensions.SmallPadding))
                            Text("서버 연결 다시 시도")
                        }
                    }

                    Spacer(modifier = Modifier.height(AppDimensions.DefaultPadding))

                    // Main content area with animation
                    AnimatedVisibility(
                        visible = uiState.videoId.isNotEmpty() ||
                                (!uiState.searchBarState.isFocused && uiState.videoId.isEmpty())
                    ) {
                        if (uiState.videoId.isNotEmpty()) {
                            HomeStartSummary(
                                videoId = uiState.videoId,
                                isOfflineMode = isOfflineMode,
                                onStartSummaryClick = {
                                    homeViewModel.requestSummary(
                                        url = "${ApiConstants.DEFAULT_YOUTUBE_URL}/watch?v=${uiState.videoId}",
                                        videoId = uiState.videoId
                                    )
                                    onNavigateToSummary(uiState.videoId)
                                    homeViewModel.setSearchText("")
                                }
                            )
                        } else {
                            // Show recent summaries section
                            HomeRecentSummary(
                                homeScreenState = uiState,
                                summaries = uiState.summaries,
                                isLoading = uiState.isLoading,
                                isAdmin = isAdmin,
                                isLoginUser = isLoginUser,
                                isOfflineMode = isOfflineMode,
                                onEditClick = homeViewModel::toggleEditMode,
                                onDeleteAllClick = {
                                    videoIdToDelete = null
                                    showDeleteDialog = true
                                },
                                onSortClick = homeViewModel::switchGridSortState,
                                onItemClick = { videoId ->
                                    homeViewModel.setSearchText("")
                                    onNavigateToSummary(videoId)
                                },
                                onDeleteClick = { videoId ->
                                    videoIdToDelete = videoId
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        DeleteDialog(
            isDeleteAll = videoIdToDelete == null,
            videoId = videoIdToDelete,
            onConfirm = {
                if (videoIdToDelete != null) {
                    homeViewModel.deleteSummary(videoIdToDelete!!)
                } else {
                    homeViewModel.deleteAllSummaries()
                }
                showDeleteDialog = false
                videoIdToDelete = null
            },
            onDismiss = {
                showDeleteDialog = false
                videoIdToDelete = null
            }
        )
    }
}

