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
    object Connected : WebSocketMessage()
    object Ping : WebSocketMessage()  // 추가
}

@Singleton
class WebSocketManager @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    private var webSocket: WebSocket? = null
    private var messageCallback: ((WebSocketMessage) -> Unit)? = null
    private var isConnected = false
    private var videoId: String? = null // videoId 저장용 변수 추가

    fun connect(videoId: String, onMessage: (WebSocketMessage) -> Unit) { // videoId 파라미터 추가
        closeCurrentConnection()
        this.videoId = videoId // videoId 저장
        Log.d(TAG, "Attempting to connect WebSocket for video: $videoId")

        messageCallback = onMessage
        val request = Request.Builder()
            .url("${AppConfig.WS_BASE_URL}${ApiConstants.SUMMARY_WEBSOCKET_ENDPOINT}")
            .build()

        webSocket = okHttpClient.newWebSocket(request, createWebSocketListener())
    }

    private fun createWebSocketListener() = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
            Log.d(TAG, "WebSocket connection opened")
            messageCallback?.let { callback ->
                // Connected 메시지를 먼저 보내고
                callback(WebSocketMessage.Connected)

                // 그 다음 video ID를 전송
                videoId?.let { id ->
                    Log.d(TAG, "Sending video ID: $id")
                    webSocket.send(id)
                }
            }
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
                    "ping" -> {
                        Log.d(TAG, "Processing ping message")
                        messageCallback?.invoke(WebSocketMessage.Ping)
                        // 서버로 pong 응답을 보낼 수도 있습니다
                        webSocket.send("""{"type":"pong","data":"pong"}""")
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
