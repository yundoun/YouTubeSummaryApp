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

    private val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected.asStateFlow()

    private var isConnecting = false
    private var messageCallback: ((String, String) -> Unit)? = null

    // 코루틴 범위
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private const val TAG = "WebSocketManager"
    }

    /**
     * 핑 웹소켓 초기화 및 연결
     */
    fun initialize(onMessage: (String, String) -> Unit) {
        if (_isConnected.value || isConnecting) return
        messageCallback = onMessage
        Log.d(TAG, "Initializing WebSocketManager...")
        connectPingWebSocket()
    }

    /**
     * 핑 웹소켓 연결
     */
    private fun connectPingWebSocket() {
        if (isConnecting) {
            Log.d(TAG, "Already in connecting process")
            return
        }

        isConnecting = true
        closePingWebSocket()
        Log.d(TAG, "Starting Ping WebSocket connection...")

        val request = Request.Builder()
            .url("${AppConfig.WS_BASE_URL}${ApiConstants.SUMMARY_PING_ENDPOINT}")
            .build()

        pingWebSocket = okHttpClient.newWebSocket(request, createPingWebSocketListener())

        // 타임아웃 처리
        scope.launch {
            try {
                withTimeout(5000) {
                    delay(100)
                    sendPing()
                    while (!isConnected.value) {
                        delay(50)
                    }
                }
            } catch (e: TimeoutException) {
                Log.d(TAG, "Ping WebSocket connection timeout")
                handleConnectionError()
            } finally {
                isConnecting = false
            }
        }
    }

    /**
     * 요약 웹소켓 연결
     */
    fun connectSummaryWebSocket() {
        Log.d(TAG, "Starting Summary WebSocket connection...")
        closeSummaryWebSocket()

        val request = Request.Builder()
            .url("${AppConfig.WS_BASE_URL}${ApiConstants.SUMMARY_WEBSOCKET_ENDPOINT}")
            .build()

        summaryWebSocket = okHttpClient.newWebSocket(request, createSummaryWebSocketListener())
    }

    private fun sendPing() {
        pingWebSocket?.let {
            try {
                val message = buildJsonObject {
                    put("type", "ping")
                    put("timestamp", System.currentTimeMillis().toString())
                }
                val jsonMessage = Json.encodeToString(message)
                Log.d(TAG, "Sending ping message: $jsonMessage")
                it.send(jsonMessage)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send ping: ${e.message}")
                handleConnectionError()
            }
        }
    }

    fun sendSummaryMessage(videoId: String) {
        if (summaryWebSocket == null) {
            Log.e(TAG, "Cannot send summary message. WebSocket is not connected.")
            return
        }

        try {
            Log.d(TAG, "Sending videoId to summary websocket: $videoId")
            summaryWebSocket?.send(videoId)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send summary message: ${e.message}")
        }
    }

    private fun createPingWebSocketListener() = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d(TAG, "Ping WebSocket connected (onOpen)")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            try {
                val jsonData = Json.decodeFromString<JsonObject>(text)
                val type = jsonData["type"]?.toString()?.removeSurrounding("\"")
                val data = jsonData["data"]?.toString()?.removeSurrounding("\"")

                if (type == "ping" && data == "pong") {
                    Log.d(TAG, "Ping WebSocket connected successfully (pong received)")
                    updateState(true)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing ping message: ${e.message}")
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e(TAG, "Ping WebSocket error: ${t.message}")
            handleConnectionError()
        }
    }

    private fun createSummaryWebSocketListener() = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d(TAG, "Summary WebSocket connected")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            try {
                val jsonData = Json.decodeFromString<JsonObject>(text)
                val type = jsonData["type"]?.toString()?.removeSurrounding("\"")
                val data = jsonData["data"]?.toString()?.removeSurrounding("\"")

                if (type != null && data != null) {
                    Log.d(TAG, "Received summary message - type: $type, data: $data")
                    messageCallback?.invoke(type, data)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing summary message: ${e.message}")
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e(TAG, "Summary WebSocket error: ${t.message}")
        }
    }

    private fun handleConnectionError() {
        updateState(false)
    }

    private fun updateState(isConnected: Boolean) {
        if (_isConnected.value != isConnected) {
            _isConnected.value = isConnected
            Log.d(TAG, "State updated - connected: $isConnected")
        }
    }

    fun closePingWebSocket() {
        pingWebSocket?.close(1000, "Closing ping connection")
        pingWebSocket = null
    }

    fun closeSummaryWebSocket() {
        summaryWebSocket?.close(1000, "Closing summary connection")
        summaryWebSocket = null
    }

    fun closeAll() {
        closePingWebSocket()
        closeSummaryWebSocket()
    }

    fun onDataReceived(callback: (String, String) -> Unit) {
        messageCallback = callback
    }

    fun retryConnection() {
        Log.d(TAG, "Manual retry requested")
        connectPingWebSocket()
        scope.launch {
            delay(1000)
            if (isConnected.value) {
                Log.d(TAG, "Manual retry succeeded: Online")
            } else {
                Log.d(TAG, "Manual retry failed: Still Offline")
            }
        }
    }
}
