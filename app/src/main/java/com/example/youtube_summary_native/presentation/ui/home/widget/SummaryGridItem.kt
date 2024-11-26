package com.example.youtube_summary_native.core.presentation.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.youtube_summary_native.core.constants.AppDimensions
import com.example.youtube_summary_native.core.domain.model.summary.SummaryInfo

@Composable
fun SummaryGridItem(
    summaryInfo: SummaryInfo,
    isEditMode: Boolean,
    onItemClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(16f / 9f)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(AppDimensions.DefaultRadius)
            )
            .clip(RoundedCornerShape(AppDimensions.DefaultRadius))
            .clickable(enabled = !isEditMode) { onItemClick(summaryInfo.videoId) }
    ) {
        AsyncImage(
            model = "https://img.youtube.com/vi/${summaryInfo.videoId}/maxresdefault.jpg",
            contentDescription = "Video thumbnail",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        if (isEditMode) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
            ) {
                IconButton(
                    onClick = { onDeleteClick(summaryInfo.videoId) },
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete summary",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(AppDimensions.BigIconSize)
                    )
                }
            }
        }
    }
}