package com.example.youtube_summary_native.core.presentation.ui.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.youtube_summary_native.core.constants.AppConstants
import com.example.youtube_summary_native.core.constants.AppDimensions
import com.example.youtube_summary_native.core.domain.model.state.HomeScreenState
import com.example.youtube_summary_native.core.domain.model.summary.SummaryInfo

@Composable
fun HomeRecentSummary(
    homeScreenState: HomeScreenState,
    summaries: List<SummaryInfo>?,
    isLoading: Boolean,
    isAdmin: Boolean,
    isLoginUser: Boolean,
    isOfflineMode: Boolean,
    onEditClick: () -> Unit,
    onDeleteAllClick: () -> Unit,
    onSortClick: () -> Unit,
    onItemClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val gridWidth = if (screenWidth < AppDimensions.ResponsiveWidth) {
        screenWidth * 0.9f
    } else {
        screenWidth * 0.6f
    }

    val spacing = screenWidth / 50

    val columns = when {
        screenWidth <= 600.dp -> 1
        screenWidth <= AppDimensions.ResponsiveWidth -> 2
        else -> 3
    }

    Column(
        modifier = modifier.width(gridWidth),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RecentSummaryHeader(
            homeScreenState = homeScreenState,
            isAdmin = isAdmin,
            isLoginUser = isLoginUser,
            isOfflineMode = isOfflineMode,
            onEditClick = onEditClick,
            onDeleteAllClick = onDeleteAllClick,
            onSortClick = onSortClick
        )

        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(top = AppDimensions.ExtraLargePadding)
                        .size(48.dp)
                )
            }
            summaries.isNullOrEmpty() -> {
                EmptyStateMessage()
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    contentPadding = PaddingValues(spacing),
                    horizontalArrangement = Arrangement.spacedBy(spacing),
                    verticalArrangement = Arrangement.spacedBy(spacing),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = if (homeScreenState.recentSummaryState.gridSortState) {
                            summaries
                        } else {
                            summaries.reversed()
                        },
                        key = { it.videoId }
                    ) { summary ->
                        SummaryGridItem(
                            summaryInfo = summary,
                            isEditMode = homeScreenState.recentSummaryState.isEditMode,
                            onItemClick = onItemClick,
                            onDeleteClick = onDeleteClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyStateMessage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = AppDimensions.ExtraLargePadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
        Spacer(modifier = Modifier.height(AppDimensions.DefaultPadding))
        Text(
            text = AppConstants.NO_RECENT_SUMMARIES,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}