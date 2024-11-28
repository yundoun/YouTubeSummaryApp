package com.example.youtube_summary_native.presentation.ui.summary

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.youtube_summary_native.presentation.ui.summary.SummaryViewModel
import com.example.youtube_summary_native.presentation.ui.summary.widget.ScriptWidget
import com.example.youtube_summary_native.presentation.ui.summary.widget.SummaryDrawer
import com.example.youtube_summary_native.presentation.ui.summary.widget.YouTubePlayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
    videoId: String,
    onBackPress: () -> Unit,
    viewModel: SummaryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val isSmallWidth = screenWidth < 600.dp

    var isDrawerOpen by remember { mutableStateOf(uiState.isDrawerOpen) }

    // BackHandler to close drawer when it's open
    BackHandler(enabled = isDrawerOpen) {
        isDrawerOpen = false
        viewModel.toggleDrawer()
    }

    Scaffold(
        topBar = {
            if (!isDrawerOpen) {
                TopAppBar(
                    title = { Text(text = uiState.title) },
                    navigationIcon = {
                        IconButton(onClick = onBackPress) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* TODO: Download */ }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Download")
                        }
                        IconButton(onClick = { /* TODO: Share */ }) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                        IconButton(
                            onClick = {
                                isDrawerOpen = !isDrawerOpen
                                viewModel.toggleDrawer()
                            }
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                // Main Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    // Video Player placeholder
                    YouTubePlayer(
                        videoId = uiState.videoId,
                        modifier = Modifier.padding(16.dp),
                        onReady = { player ->
                            // ViewModel에서 player 인스턴스 저장하거나 필요한 처리를 할 수 있습니다
                            viewModel.onPlayerReady(player)
                        }
                    )

                    // Caption Controls placeholder
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Caption Controls") // Placeholder
                        }
                    }

                    // Tab Layout
                    var selectedTab by remember { mutableIntStateOf(0) }
                    Column {
                        TabRow(selectedTabIndex = selectedTab) {
                            Tab(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                text = { Text("요약 스크립트") },
                                icon = { Icon(Icons.Default.DateRange, contentDescription = null) }
                            )
                            Tab(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                text = { Text("전체 스크립트") },
                                icon = { Icon(Icons.Default.DateRange, contentDescription = null) }
                            )
                        }

                        // Tab Content
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(16.dp)
                        ) {
                            when (selectedTab) {
                                0 -> ScriptWidget(
                                    scriptData = uiState.summaryContent,
                                    isLoading = uiState.isLoading,
                                    hasError = uiState.error != null,
                                    onTimeClick = { timeInSeconds ->
                                        // TODO: Implement video seeking
                                    }
                                )
                                1 -> ScriptWidget(
                                    scriptData = uiState.scriptContent,
                                    isLoading = uiState.isLoading,
                                    hasError = uiState.error != null,
                                    onTimeClick = { timeInSeconds ->
                                        // TODO: Implement video seeking
                                    }
                                )
                            }
                        }
                    }
                }

                // Drawer for large screens
                if (!isSmallWidth && isDrawerOpen) {
                    SummaryDrawer(
                        summaries = uiState.summaryList,
                        currentVideoId = uiState.videoId,
                        onVideoSelect = {
                            viewModel.onVideoSelect(it)
                            viewModel.toggleDrawer()
                        },
                        onClose = {
                            isDrawerOpen = false
                            viewModel.toggleDrawer()
                        },
                        modifier = Modifier
                            .width(300.dp)
                            .fillMaxHeight()
                    )
                }
            }

            // Drawer for small screens
            if (isSmallWidth && isDrawerOpen) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.3f),
                    onClick = {
                        isDrawerOpen = false
                        viewModel.toggleDrawer()
                    }
                ) {
                    SummaryDrawer(
                        summaries = uiState.summaryList,
                        currentVideoId = uiState.videoId,
                        onVideoSelect = {
                            viewModel.onVideoSelect(it)
                            viewModel.toggleDrawer()
                        },
                        onClose = {
                            isDrawerOpen = false
                            viewModel.toggleDrawer()
                        },
                        modifier = Modifier
                            .width(300.dp)
                            .fillMaxHeight()
                            .align(Alignment.CenterEnd)
                    )
                }
            }
        }
    }
}
