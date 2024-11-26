package com.example.youtube_summary_native.core.presentation.ui.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.youtube_summary_native.core.constants.AppConstants
import com.example.youtube_summary_native.core.constants.AppDimensions
import com.example.youtube_summary_native.core.domain.model.state.HomeScreenState

@Composable
fun RecentSummaryHeader(
    homeScreenState: HomeScreenState,
    isAdmin: Boolean,
    isLoginUser: Boolean,
    isOfflineMode: Boolean,
    onEditClick: () -> Unit,
    onDeleteAllClick: () -> Unit,
    onSortClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = AppDimensions.DefaultPadding,
                top = AppDimensions.LargePadding,
                bottom = AppDimensions.DefaultPadding
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = AppConstants.RECENT_SUMMARIZED_MOVIES,
            style = MaterialTheme.typography.titleLarge
        )

        // Offline Mode Badge
        if (isOfflineMode) {
            Surface(
                modifier = Modifier
                    .padding(start = AppDimensions.SmallPadding)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            ) {
                Text(
                    text = AppConstants.OFFLINE_MODE,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }

        // Edit Button (for logged-in users)
        if (isLoginUser && !isOfflineMode) {
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = if (homeScreenState.recentSummaryState.isEditMode) {
                        Icons.Default.Edit
                    } else {
                        Icons.Default.Edit
                    },
                    contentDescription = "Toggle edit mode",
                    modifier = Modifier.size(AppDimensions.IconSize)
                )
            }
        }

        // Delete All Button (for admin in edit mode)
        if (isAdmin && homeScreenState.recentSummaryState.isEditMode) {
            TextButton(
                onClick = onDeleteAllClick,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(AppConstants.DELETE_ALL)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Sort Button
        TextButton(
            onClick = onSortClick,
            modifier = Modifier.padding(end = AppDimensions.DefaultPadding)
        ) {
            Text(
                text = if (homeScreenState.recentSummaryState.gridSortState) {
                    AppConstants.OLDEST
                } else {
                    AppConstants.LATEST
                }
            )
            Icon(
                imageVector = if (homeScreenState.recentSummaryState.gridSortState) {
                    Icons.Default.KeyboardArrowUp
                } else {
                    Icons.Default.KeyboardArrowDown
                },
                contentDescription = "Sort direction",
                modifier = Modifier.size(AppDimensions.IconSize)
            )
        }
    }
}