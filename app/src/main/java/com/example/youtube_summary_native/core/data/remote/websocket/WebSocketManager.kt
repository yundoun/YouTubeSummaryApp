package com.example.youtube_summary_native.core.data.remote.websocket

import android.util.Log
import com.example.youtube_summary_native.config.env.AppConfig
import com.example.youtube_summary_native.core.constants.ApiConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

sealed class WebSocketMessage {
    data class Summary(val content: String) : WebSocketMessage()
    object Complete : WebSocketMessage()
    data class Error(val message: String) : WebSocketMessage()
}

@Singleton
class WebSocketManager @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    private var webSocket: WebSocket? = null
    private var messageCallback: ((WebSocketMessage) -> Unit)? = null
    private var isConnected = false
    private var pingJob: Job? = null

    fun connect(onMessage: (WebSocketMessage) -> Unit) {
        closeCurrentConnection()  // 기존 연결 정리

        Log.d(TAG, "Attempting to connect WebSocket")
        if (isConnected) {
            Log.d(TAG, "WebSocket is already connected")
            return
        }

        // 연결 상태 확인을 위한 ping
        pingJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                delay(30000)  // 30초마다
                if (isConnected) {
                    webSocket?.send("{\"type\":\"ping\"}")
                }
            }
        }

        messageCallback = onMessage
        val request = Request.Builder()
            .url("${AppConfig.WS_BASE_URL}${ApiConstants.SUMMARY_WEBSOCKET_ENDPOINT}")
            .build()

        webSocket = okHttpClient.newWebSocket(request, createWebSocketListener())
        isConnected = true
        Log.d(TAG, "WebSocket connection initiated")
    }

    private fun createWebSocketListener() = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
            Log.d(TAG, "WebSocket connection opened")
            isConnected = true
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            try {
                val jsonData = Json.decodeFromString<JsonObject>(text)
                val type = jsonData["type"]?.toString()?.removeSurrounding("\"")
                val data = jsonData["data"]?.toString()?.removeSurrounding("\"")

                Log.d(TAG, "Parsed message - type: $type, data: $data")

                when (type) {
                    "summary" -> data?.let {
                        Log.d(TAG, "Processing summary message")
                        messageCallback?.invoke(WebSocketMessage.Summary(it))
                    }
                    "complete" -> {
                        Log.d(TAG, "Processing complete message")
                        messageCallback?.invoke(WebSocketMessage.Complete)
                    }
                    else -> {
                        Log.d(TAG, "Unknown message type: $type")
                        messageCallback?.invoke(WebSocketMessage.Error("Unknown message type"))
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing message: ${e.message}")
                messageCallback?.invoke(WebSocketMessage.Error(e.message ?: "Unknown error"))
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
            Log.e(TAG, "WebSocket failure: ${t.message}")
            isConnected = false
            messageCallback?.invoke(WebSocketMessage.Error(t.message ?: "Connection failed"))
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(TAG, "WebSocket closing: $reason")
            isConnected = false
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(TAG, "WebSocket closed: $reason")
            isConnected = false
        }
    }

    fun sendMessage(message: String) {
        Log.d(TAG, "Sending message: $message")
        if (!isConnected) {
            Log.w(TAG, "Attempting to send message while disconnected")
            return
        }
        webSocket?.send(message) ?: Log.e(TAG, "WebSocket is null")
    }

    private fun closeCurrentConnection() {
        pingJob?.cancel()
        webSocket?.close(1000, "Closing previous connection")
        webSocket = null
        messageCallback = null
        isConnected = false
    }

    fun close() {
        webSocket?.close(1000, "Closing connection")
        webSocket = null
        messageCallback = null
        isConnected = false
        Log.d(TAG, "WebSocket connection closed")
    }

    companion object {
        private const val TAG = "WebSocketManager"
    }
}
