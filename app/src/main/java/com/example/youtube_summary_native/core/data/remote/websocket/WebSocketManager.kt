package com.example.youtube_summary_native.core.data.remote.websocket

import android.util.Log
import com.example.youtube_summary_native.config.env.AppConfig
import com.example.youtube_summary_native.core.constants.ApiConstants
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketManager @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    private var pingWebSocket: WebSocket? = null
    private var summaryWebSocket: WebSocket? = null
    private var currentVideoId: String? = null

    private val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected.asStateFlow()

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState = _connectionState.asStateFlow()

    private var messageCallback: ((String, String) -> Unit)? = null
    private var reconnectJob: Job? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    sealed class ConnectionState {
        object Connected : ConnectionState()
        object Connecting : ConnectionState()
        object Disconnected : ConnectionState()
        data class Error(val message: String) : ConnectionState()
    }

    companion object {
        private const val TAG = "WebSocketManager"
        private const val RECONNECT_DELAY = 5000L
        private const val MAX_RECONNECT_ATTEMPTS = 5
    }

    fun initialize(onMessage: (String, String) -> Unit) {
        messageCallback = onMessage
        connectPingWebSocket()
    }

    private fun connectPingWebSocket() {
        if (_connectionState.value is ConnectionState.Connecting) return

        scope.launch {
            try {
                _connectionState.value = ConnectionState.Connecting
                val request = Request.Builder()
                    .url("${AppConfig.WS_BASE_URL}${ApiConstants.SUMMARY_PING_ENDPOINT}")
                    .build()

                pingWebSocket = okHttpClient.newWebSocket(request, createPingWebSocketListener())
                startPingInterval()
            } catch (e: Exception) {
                handleConnectionError("Ping connection failed: ${e.message}")
            }
        }
    }

    private fun createPingWebSocketListener() = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d(TAG, "Ping WebSocket connected")
            _isConnected.value = true
            _connectionState.value = ConnectionState.Connected
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            scope.launch {
                try {
                    val jsonData = Json.decodeFromString<JsonObject>(text)
                    val type = jsonData["type"]?.toString()?.removeSurrounding("\"")
                    val data = jsonData["data"]?.toString()?.removeSurrounding("\"")

                    if (type == "ping" && data == "pong") {
                        Log.d(TAG, "Received pong response")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing ping message", e)
                }
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e(TAG, "Ping WebSocket failure", t)
            handleConnectionError("Ping connection failed: ${t.message}")
            startReconnectProcess()
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(TAG, "Ping WebSocket closing: $reason")
            _isConnected.value = false
        }
    }

    fun connectSummaryWebSocket(videoId: String) {
        currentVideoId = videoId
        if (_connectionState.value is ConnectionState.Connected) {
            sendSummaryMessage(videoId)
            return
        }

        scope.launch {
            try {
                val request = Request.Builder()
                    .url("${AppConfig.WS_BASE_URL}${ApiConstants.SUMMARY_WEBSOCKET_ENDPOINT}")
                    .build()

                summaryWebSocket = okHttpClient.newWebSocket(
                    request,
                    createSummaryWebSocketListener(videoId)
                )
            } catch (e: Exception) {
                handleConnectionError("Summary connection failed: ${e.message}")
            }
        }
    }

    private fun createSummaryWebSocketListener(videoId: String) = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d(TAG, "Summary WebSocket connected")
            _connectionState.value = ConnectionState.Connected
            sendSummaryMessage(videoId)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            scope.launch {
                try {
                    val jsonData = Json.decodeFromString<JsonObject>(text)
                    val type = jsonData["type"]?.toString()?.removeSurrounding("\"")
                    val data = jsonData["data"]?.toString()?.removeSurrounding("\"")

                    if (type != null && data != null) {
                        messageCallback?.invoke(type, data)
                        if (type == "complete") {
                            Log.d(TAG, "Summary complete, closing connection")
                            closeWithSuccess()
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing message", e)
                }
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(TAG, "Summary WebSocket closing: $reason")
            if (code != 1000) {
                handleConnectionError(reason)
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            handleConnectionError("Summary WebSocket failure: ${t.message}")
            startReconnectProcess()
        }
    }

    private fun sendPing() {
        scope.launch {
            try {
                val message = buildJsonObject {
                    put("type", "ping")
                    put("timestamp", System.currentTimeMillis().toString())
                }
                val jsonMessage = Json.encodeToString(message)
                pingWebSocket?.send(jsonMessage)
                Log.d(TAG, "Ping sent")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send ping", e)
                handleConnectionError("Failed to send ping: ${e.message}")
            }
        }
    }

    fun sendSummaryMessage(videoId: String) {
        scope.launch {
            try {
                Log.d(TAG, "Sending videoId to summary websocket: $videoId")
                summaryWebSocket?.send(videoId)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send summary message", e)
                handleConnectionError("Failed to send summary message: ${e.message}")
            }
        }
    }

    private fun startPingInterval() {
        scope.launch {
            while (isActive) {
                sendPing()
                delay(30000) // 30초마다 ping
            }
        }
    }

    private fun startReconnectProcess() {
        reconnectJob?.cancel()
        reconnectJob = scope.launch {
            var attempts = 0
            while (attempts < MAX_RECONNECT_ATTEMPTS) {
                delay(RECONNECT_DELAY)
                Log.d(TAG, "Attempting to reconnect... (${attempts + 1}/$MAX_RECONNECT_ATTEMPTS)")

                if (currentVideoId != null) {
                    connectSummaryWebSocket(currentVideoId!!)
                    if (_connectionState.value is ConnectionState.Connected) {
                        Log.d(TAG, "Reconnection successful")
                        return@launch
                    }
                }
                attempts++
            }
            handleConnectionError("Max reconnection attempts reached")
        }
    }

    private fun handleConnectionError(message: String) {
        Log.e(TAG, message)
        _connectionState.value = ConnectionState.Error(message)
        _isConnected.value = false
    }

    private fun closeWithSuccess() {
        scope.launch {
            _connectionState.value = ConnectionState.Disconnected
            closeSummaryWebSocket()
        }
    }

    fun closePingWebSocket() {
        scope.launch {
            try {
                pingWebSocket?.close(1000, "Normal ping closure")
                pingWebSocket = null
                Log.d(TAG, "Ping WebSocket closed")
            } catch (e: Exception) {
                Log.e(TAG, "Error closing ping WebSocket", e)
            }
        }
    }

    fun closeSummaryWebSocket() {
        scope.launch {
            try {
                summaryWebSocket?.close(1000, "Normal summary closure")
                summaryWebSocket = null
                currentVideoId = null
                Log.d(TAG, "Summary WebSocket closed")
            } catch (e: Exception) {
                Log.e(TAG, "Error closing summary WebSocket", e)
            }
        }
    }

    fun closeAll() {
        scope.launch {
            reconnectJob?.cancel()
            closePingWebSocket()
            closeSummaryWebSocket()
            _connectionState.value = ConnectionState.Disconnected
            _isConnected.value = false
            messageCallback = null
        }
    }
}