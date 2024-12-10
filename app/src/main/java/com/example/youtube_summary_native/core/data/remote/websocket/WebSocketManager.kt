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
    private var webSocket: WebSocket? = null

    // Flutter code에서 state를 관리하던 부분을 StateFlow로 대체
    private val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected.asStateFlow()

    private var isConnecting = false
    private var retryAttempts = 0

    // 메시지 수신 콜백 (type, data)
    private var messageCallback: ((String, String) -> Unit)? = null

    // 코루틴 범위 (필요하다면 외부에서 주입하거나 관리)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Flutter의 initialize()와 유사
     * 이미 연결되어 있거나 연결 중이면 재시도하지 않음.
     * 아니면 connect 시도
     */
    fun initialize(onMessage: (String, String) -> Unit) {
        if (_isConnected.value || isConnecting) return
        messageCallback = onMessage
        Log.d(TAG, "Initializing WebSocketManager...")
        connectWebSocket()
    }

    /**
     * Flutter의 connectWebSocket()과 유사
     */
    private fun connectWebSocket() {
        if (isConnecting) {
            Log.d(TAG, "Already in connecting process")
            return
        }

        isConnecting = true
        close() // 기존 연결 정리
        Log.d(TAG, "Starting WebSocket connection...")

        val request = Request.Builder()
            .url("${AppConfig.WS_BASE_URL}${ApiConstants.SUMMARY_PING_ENDPOINT}")
            .build()

        // OkHttp WebSocket 비동기 연결 시도
        webSocket = okHttpClient.newWebSocket(request, createWebSocketListener())

        // 타임아웃 처리: 5초 내에 ping-pong으로 연결확인 실패 시 실패 처리
        scope.launch {
            try {
                withTimeout(5000) {
                    // 100ms 대기 후 ping 전송
                    delay(100)
                    sendPing()
                    // ping 결과( pong ) 대기
                    while (!isConnected.value) {
                        delay(50)
                    }
                }
            } catch (e: TimeoutException) {
                Log.d(TAG, "WebSocket connection timeout")
                handleConnectionError()
            } finally {
                isConnecting = false
            }
        }
    }

    /**
     * Flutter의 sendPing()과 유사
     */
    private fun sendPing() {
        webSocket?.let {
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


    /**
     * 일반 메시지 전송 메서드
     */
    fun sendMessage(message: String) {
        if (_isConnected.value && webSocket != null) {
            try {
                Log.d(TAG, "Sending message: $message")
                webSocket?.send(message)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send message: ${e.message}")
                handleConnectionError()
            }
        } else {
            Log.e(TAG, "Cannot send message. WebSocket is not connected.")
        }
    }

    /**
     * Flutter의 retryConnection()과 유사
     */
    fun retryConnection() {
        Log.d(TAG, "Manual retry requested")
        connectWebSocket()
        // 결과 확인 로직
        scope.launch {
            delay(1000)
            if (isConnected.value) {
                Log.d(TAG, "Manual retry succeeded: Online")
            } else {
                Log.d(TAG, "Manual retry failed: Still Offline")
            }
        }
    }

    /**
     * Flutter의 _handleConnectionError()에 해당
     */
    private fun handleConnectionError() {
        updateState(false)
        // 여기서는 UI 상태 변경을 callback이나 Flow 통해 외부로 알릴 수 있음
        // 자동 재시도 로직을 여기서 구현해도 되고, 수동으로 재시도 가능하게 할 수도 있다.
    }

    /**
     * Flutter의 _updateState()에 해당
     */
    private fun updateState(isConnected: Boolean) {
        if (_isConnected.value != isConnected) {
            _isConnected.value = isConnected
            Log.d(TAG, "State updated - connected: $isConnected")
        }
    }

    /**
     * Flutter의 _disposeWebSocket()에 해당
     */
    fun close() {
        if (webSocket != null) {
            Log.d(TAG, "Disposing WebSocket connection")
            webSocket?.close(1000, "Closing connection")
            webSocket = null
        }
    }

    /**
     * WebSocketListener 구현
     */
    private fun createWebSocketListener() = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            Log.d(TAG, "WebSocket connected (onOpen)")
            // 여기서 ping을 보내고, pong 응답을 받을 때까지 대기
            // ping은 connectWebSocket 내의 coroutine에서 처리
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            try {
                val jsonData = Json.decodeFromString<JsonObject>(text)
                val type = jsonData["type"]?.toString()?.removeSurrounding("\"")
                val data = jsonData["data"]?.toString()?.removeSurrounding("\"")

                if (type == "ping" && data == "pong") {
                    Log.d(TAG, "WebSocket connected successfully (pong received)")
                    updateState(true)
                }

                if (type != null && data != null) {
                    messageCallback?.invoke(type, data)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing message: ${e.message}")
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosing(webSocket, code, reason)
            Log.d(TAG, "WebSocket is closing: $code / $reason")
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            Log.d(TAG, "WebSocket connection closed: $code / $reason")
            handleConnectionError()
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            Log.e(TAG, "WebSocket error: ${t.message}")
            handleConnectionError()
        }
    }

    companion object {
        private const val TAG = "WebSocketManager"
    }
}
