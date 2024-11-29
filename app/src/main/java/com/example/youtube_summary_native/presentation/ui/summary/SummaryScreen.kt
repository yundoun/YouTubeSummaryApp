package com.example.youtube_summary_native.presentation.ui.summary

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
                    title = {
                        Text(
                            text = uiState.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
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
            // Main content with single scroll
            SingleChildScrollView(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Main Content
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        // Video Player
                        YouTubePlayer(
                            videoId = uiState.videoId,
                            modifier = Modifier.padding(16.dp),
                            onReady = { player ->
                                viewModel.onPlayerReady(player)
                            }
                        )

                        // Caption Controls
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Caption Controls")
                            }
                        }

                        // Tab Layout and Content
                        var selectedTab by remember { mutableIntStateOf(0) }
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TabRow(selectedTabIndex = selectedTab) {
                                Tab(
                                    selected = selectedTab == 0,
                                    onClick = { selectedTab = 0 },
                                    text = { Text("요약 스크립트") },
                                    icon = {
                                        Icon(
                                            Icons.Default.DateRange,
                                            contentDescription = null
                                        )
                                    }
                                )
                                Tab(
                                    selected = selectedTab == 1,
                                    onClick = { selectedTab = 1 },
                                    text = { Text("전체 스크립트") },
                                    icon = {
                                        Icon(
                                            Icons.Default.DateRange,
                                            contentDescription = null
                                        )
                                    }
                                )
                            }

                            // Tab Content without scroll
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                when (selectedTab) {
                                    0 -> ScriptWidget(
                                        scriptData = uiState.summaryContent,
                                        isLoading = uiState.isLoading,
                                        hasError = uiState.error != null,
                                        onTimeClick = { timeInSeconds ->
                                            viewModel.seekTo(timeInSeconds)
                                        }
                                    )

                                    1 -> ScriptWidget(
                                        scriptData = uiState.scriptContent,
                                        isLoading = uiState.isLoading,
                                        hasError = uiState.error != null,
                                        onTimeClick = { timeInSeconds ->
                                            viewModel.seekTo(timeInSeconds)
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
                                isDrawerOpen = false  // Auto close drawer
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
                    Box(modifier = Modifier.fillMaxSize()) {
                        SummaryDrawer(
                            summaries = uiState.summaryList,
                            currentVideoId = uiState.videoId,
                            onVideoSelect = {
                                viewModel.onVideoSelect(it)
                                isDrawerOpen = false  // Auto close drawer
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
}

@Composable
private fun SingleChildScrollView(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        content()
    }
}