package com.example.youtube_summary_native.core.domain.model.caption

import com.example.youtube_summary_native.core.domain.model.summary.ScriptItem
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

data class Caption(
    val text: String,
    val startTime: Duration,
    val endTime: Duration,
    val index: Int,
    val opacity: Float = 1.0f,
    val isTransitioning: Boolean = false
) {
    companion object {
        fun fromScriptItem(script: ScriptItem, index: Int): Caption {
            return Caption(
                text = script.text,
                startTime = (script.start * 1000).toLong().milliseconds,
                endTime = ((script.start + script.duration) * 1000).toLong().milliseconds,
                index = index
            )
        }
    }

    fun isActiveAt(position: Duration): Boolean {
        return position >= startTime && position <= endTime
    }

    fun shouldStartTransitionAt(position: Duration): Boolean {
        val transitionStart = startTime - 500.milliseconds
        return position >= transitionStart && position <= startTime
    }

    fun shouldEndTransitionAt(position: Duration): Boolean {
        val transitionStart = endTime - 500.milliseconds
        return position >= transitionStart && position <= endTime
    }
}