package com.example.youtube_summary_native.core.presentation.ui.home

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
import com.example.youtube_summary_native.core.constants.AppDimensions
import com.example.youtube_summary_native.core.presentation.ui.common.LoginButton
import com.example.youtube_summary_native.core.presentation.ui.common.dialog.DeleteDialog
import com.example.youtube_summary_native.core.presentation.ui.home.components.HomeRecentSummary
import com.example.youtube_summary_native.core.presentation.ui.home.components.HomeSearchBar
import com.example.youtube_summary_native.core.presentation.ui.home.components.HomeStartSummary

@Composable
fun HomeScreen(
    onNavigateToSummary: () -> Unit,
    onNavigateToAuth: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isOfflineMode by viewModel.isOfflineMode.collectAsStateWithLifecycle()
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
                viewModel.setGridScrollState(index > 0)
            }
    }

    // Main scaffold
    Scaffold(
        floatingActionButton = {
            if (!uiState.homeScrollState && uiState.recentSummaryState.gridScrollState) {
                FloatingActionButton(
                    onClick = {
                        viewModel.setHomeScrollState(true)
                        viewModel.setGridScrollState(false)
//                        listState.animateScrollToItem(0)
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
                        onTextChanged = viewModel::setSearchText,
                        onTextClear = { viewModel.setSearchText("") },
                        onPaste = { /* Implement clipboard paste */ },
                        onFocusChanged = viewModel::setSearchFocus,
                        isOfflineMode = isOfflineMode,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Offline mode retry button
                    if (isOfflineMode) {
                        Button(
                            onClick = viewModel::retryConnection,
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
                            // Show start summary section
                            HomeStartSummary(
                                videoId = uiState.videoId,
                                isOfflineMode = isOfflineMode,
                                onStartSummaryClick = {
                                    onNavigateToSummary()
                                    viewModel.setSearchText("")
                                }
                            )
                        } else {
                            // Show recent summaries section
//                            HomeRecentSummary(
//                                homeScreenState = uiState,
//                                summaries = uiState.summaries, // summaries 전달 추가
//                                isLoading = uiState.isLoading, // ViewModel의 로딩 상태를 이용하도록 수정
//                                isAdmin = uiState.isAdmin, // Admin 상태 추가
//                                isLoginUser = uiState.isLoginUser, // 로그인 상태 추가
//                                isOfflineMode = isOfflineMode,
//                                onEditClick = viewModel::toggleEditMode,
//                                onDeleteAllClick = {
//                                    videoIdToDelete = null
//                                    showDeleteDialog = true
//                                },
//                                onSortClick = viewModel::switchGridSortState,
//                                onItemClick = { videoId ->
//                                    viewModel.setSearchText("")
//                                    onNavigateToSummary()
//                                },
//                                onDeleteClick = { videoId ->
//                                    videoIdToDelete = videoId
//                                    showDeleteDialog = true
//                                }
//                            )

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
                    viewModel.deleteSummary(videoIdToDelete!!)
                } else {
                    viewModel.deleteAllSummaries()
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