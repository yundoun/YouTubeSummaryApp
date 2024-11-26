package com.example.youtube_summary_native.core.presentation.ui.home.components

import android.annotation.SuppressLint
import androidx.compose.ui.input.pointer.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.youtube_summary_native.core.constants.AppConstants
import com.example.youtube_summary_native.core.constants.AppDimensions
import com.example.youtube_summary_native.presentation.theme.AppColors

@Composable
fun HomeStartSummary(
    videoId: String,
    isOfflineMode: Boolean,
    onStartSummaryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // 썸네일 너비 계산
    val thumbnailWidth = if (screenWidth < AppDimensions.ResponsiveWidth) {
        screenWidth * 0.9f
    } else {
        screenWidth * 0.5f
    }

    var isHovered by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Thumbnail
        if (videoId.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .width(thumbnailWidth)
                    .padding(
                        horizontal = AppDimensions.HomeMobileSearchBarPadding,
                        vertical = AppDimensions.DefaultPadding
                    )
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(AppDimensions.DefaultRadius)
                    )
                    .clip(RoundedCornerShape(AppDimensions.DefaultRadius))
            ) {
                AsyncImage(
                    model = "https://img.youtube.com/vi/$videoId/maxresdefault.jpg",
                    contentDescription = "Video thumbnail",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                )
            }
        }

        Spacer(modifier = Modifier.height(AppDimensions.ExtraLargePadding))

        // Start Summary Button
        Box(
            modifier = Modifier
                .width(thumbnailWidth)
                .shadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(AppDimensions.DefaultRadius)
                )
                .background(
                    brush = Brush.horizontalGradient(
                        colors = if (isHovered) {
                            listOf(AppColors.Primary, AppColors.Primary)
                        } else {
                            AppColors.PrimaryGradient
                        }
                    ),
                    shape = RoundedCornerShape(AppDimensions.DefaultRadius)
                )
                .clip(RoundedCornerShape(AppDimensions.DefaultRadius))
                .hoverable(
                    onEnter = { isHovered = true },
                    onExit = { isHovered = false },
                    enabled = !isOfflineMode
                )
                .clickable(
                    enabled = !isOfflineMode,
                    onClick = onStartSummaryClick
                )
        ) {
            Text(
                text = AppConstants.START_SUMMARY,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(
                        vertical = if (screenWidth > AppDimensions.ResponsiveWidth) {
                            AppDimensions.MediumPadding
                        } else {
                            AppDimensions.DefaultPadding
                        }
                    )
            )
        }
    }
}

@SuppressLint("ReturnFromAwaitPointerEventScope")
@Composable
private fun Modifier.hoverable(
    onEnter: () -> Unit,
    onExit: () -> Unit,
    enabled: Boolean = true
) = if (enabled) {
    pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent()
                when {
                    event.type == PointerEventType.Enter -> onEnter()
                    event.type == PointerEventType.Exit -> onExit()
                }
            }
        }
    }
} else {
    this
}