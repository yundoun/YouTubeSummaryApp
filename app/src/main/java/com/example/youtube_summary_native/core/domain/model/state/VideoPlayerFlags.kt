package com.example.youtube_summary_native.core.domain.model.state

import kotlinx.serialization.Serializable

@Serializable
data class VideoPlayerFlags(
    val autoPlay: Boolean = true,
    val mute: Boolean = false,
    val disableControls: Boolean = false,
    val loop: Boolean = false,
    val isLive: Boolean = false,
    val forceHD: Boolean = false,
    val enableCaption: Boolean = false,
    val hideTitle: Boolean = false
)