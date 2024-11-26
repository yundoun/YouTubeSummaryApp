package com.example.youtube_summary_native.core.domain.model.state

data class NetworkMonitorState(
    val isConnected: Boolean,
    val retryAttempts: Int
)