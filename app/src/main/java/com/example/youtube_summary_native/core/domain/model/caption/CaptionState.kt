package com.example.youtube_summary_native.core.domain.model.caption

import kotlin.time.Duration

data class CaptionState(
    val captions: List<Caption> = emptyList(),
    val isVisible: Boolean = false,
    val currentPosition: Duration = Duration.ZERO,
    val activeCaptions: List<Caption> = emptyList(),
    val captionOpacities: Map<Int, Float> = emptyMap(),
    val isControlsVisible: Boolean = false,
    val captionOffset: Float = 8f,
    val captionStyle: CaptionStyle = CaptionStyle.DEFAULT,
    val fontSize: Float = 14f
) {
    // 유틸리티 메서드
    fun getVisibleCaptions(): List<Caption> {
        return activeCaptions.filter { caption ->
            val opacity = captionOpacities[caption.index] ?: 0f
            opacity > 0f
        }
    }

    // 특정 시간에 표시되어야 할 자막들을 찾는 메서드
    fun getCaptionsForPosition(position: Duration): List<Caption> {
        return captions.filter { it.isActiveAt(position) }
    }

    // 전환이 필요한 자막들을 찾는 메서드
    fun getTransitioningCaptions(position: Duration): List<Caption> {
        return captions.filter { caption ->
            caption.shouldStartTransitionAt(position) ||
                    caption.shouldEndTransitionAt(position)
        }
    }
}