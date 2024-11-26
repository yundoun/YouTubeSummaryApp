package com.example.youtube_summary_native.core.domain.model.state

import kotlinx.serialization.Serializable

@Serializable
data class VideoPlayerState(
    val isPlayerReady: Boolean = false,
    val isPlaying: Boolean = false,
    val hasError: Boolean = false,
    val flags: VideoPlayerFlags? = null
)