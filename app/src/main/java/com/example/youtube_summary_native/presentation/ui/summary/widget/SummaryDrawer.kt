package com.example.youtube_summary_native.presentation.ui.summary.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.youtube_summary_native.core.domain.model.summary.SummaryInfo

@Composable
fun SummaryDrawer(
    summaries: List<SummaryInfo>,
    currentVideoId: String,
    onVideoSelect: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp // Increased elevation for a more prominent look
    ) {
        Column(
            modifier = Modifier.fillMaxHeight()
        ) {
            // Drawer Header with a close button
            DrawerHeader(onClose = onClose)

            // Video List with better spacing and design
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(summaries) { summary ->
                    VideoItem(
                        summary = summary,
                        isSelected = summary.videoId == currentVideoId,
                        onClick = { onVideoSelect(summary.videoId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DrawerHeader(onClose: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Added to space out the close button
        ) {
            Text(
                text = "영상 목록",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Drawer",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun VideoItem(
    summary: SummaryInfo,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) {
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f) // Softer selection highlight
        } else {
            MaterialTheme.colorScheme.surface
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp) // Added padding for spacing
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = summary.title,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = summary.createdAt,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
    Divider(color = Color.Gray.copy(alpha = 0.3f)) // Softer divider color
}
