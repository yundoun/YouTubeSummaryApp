package com.example.youtube_summary_native.presentation.ui.summary.state

import com.example.youtube_summary_native.core.domain.model.caption.Caption
import com.example.youtube_summary_native.core.domain.model.caption.CaptionStyle

data class CaptionState(
    val isVisible: Boolean = true,
    val fontSize: Float = 14f,
    val captionOffset: Float = 8f,
    val isControlsVisible: Boolean = false,
    val currentPosition: Long = 0L,
    val captions: List<Caption> = emptyList(),
    val activeCaptions: List<Caption> = emptyList(),
    val captionOpacities: Map<Int, Float> = emptyMap(),
    val captionStyle: CaptionStyle = CaptionStyle.DEFAULT
)