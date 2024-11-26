package com.example.youtube_summary_native.core.util

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.youtube_summary_native.config.env.AppConfig
import com.example.youtube_summary_native.core.constants.ApiConstants
import com.example.youtube_summary_native.core.domain.model.state.NetworkMonitorState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.net.Socket
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkMonitor @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var webSocket: WebSocket? = null
    private var isConnecting = false

    private val _state = MutableStateFlow(NetworkMonitorState(
        isConnected = false,
        retryAttempts = 0
    ))
    val state: StateFlow<NetworkMonitorState> = _state.asStateFlow()

    fun initialize() {
        if (state.value.isConnected || isConnecting) return
        Log.d(TAG, "Initializing NetworkMonitor...")
        scope.launch { connectWebSocket() }
    }

    private suspend fun connectWebSocket() {
        if (isConnecting) {
            Log.d(TAG, "Already in connecting process")
            return
        }

        isConnecting = true
        disposeWebSocket()
        Log.d(TAG, "Starting WebSocket connection...")

        try {
            val uri = "${AppConfig.WS_BASE_URL}${ApiConstants.SUMMARY_PING_ENDPOINT}"
            Log.d(TAG, "Attempting to connect to WebSocket: $uri")

            if (checkServerPortOpen(uri)) {
                val request = Request.Builder()
                    .url(uri)
                    .build()

                webSocket = okHttpClient.newWebSocket(request, createWebSocketListener())
            } else {
                Log.d(TAG, "Port check failed")
                handleConnectionError()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to establish WebSocket connection: ${e.message}")
            handleConnectionError()
        } finally {
            isConnecting = false
        }
    }

    private fun createWebSocketListener() = object : WebSocketListener() {
        override fun onMessage(webSocket: WebSocket, text: String) {
            try {
                Log.d(TAG, "Received data: $text")
                val jsonData = Json.decodeFromString<JsonObject>(text)
                val type = jsonData["type"]?.toString()?.removeSurrounding("\"")
                val messageData = jsonData["data"]?.toString()?.removeSurrounding("\"")

                if (type == "ping" && messageData == "pong") {
                    Log.d(TAG, "WebSocket connected successfully")
                    updateState(isConnected = true)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing message: ${e.message}")
            }
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(TAG, "WebSocket connection closed")
            handleConnectionError()
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e(TAG, "WebSocket error: ${t.message}")
            handleConnectionError()
        }
    }

    private suspend fun checkServerPortOpen(wsUrl: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val uri = java.net.URI(wsUrl)
            Socket(uri.host, uri.port).use {
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Server port is not open: ${e.message}")
            false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendPing() {
        webSocket?.let {
            try {
                val message = buildJsonObject {
                    put("type", JsonPrimitive("ping"))
                    put("timestamp", JsonPrimitive(LocalDateTime.now().toString()))
                }.toString()
                Log.d(TAG, "Sending ping message: $message")
                it.send(message)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send ping: ${e.message}")
                handleConnectionError()
            }
        }
    }

    private fun handleConnectionError() {
        updateState(isConnected = false)
    }

    private fun updateState(isConnected: Boolean) {
        _state.update { currentState ->
            if (currentState.isConnected != isConnected) {
                Log.d(TAG, "State updated - connected: $isConnected")
                currentState.copy(isConnected = isConnected)
            } else {
                currentState
            }
        }
    }

    private fun disposeWebSocket() {
        webSocket?.let {
            Log.d(TAG, "Disposing WebSocket connection")
            it.cancel()
            webSocket = null
        }
    }

    suspend fun retryConnection() {
        Log.d(TAG, "Manual retry requested")
        connectWebSocket()
        Log.d(TAG, if (state.value.isConnected) "Manual retry succeeded: Online" else "Manual retry failed: Still Offline")
    }

    companion object {
        private const val TAG = "NetworkMonitor"
    }
}