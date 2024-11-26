package com.example.youtube_summary_native.core.data.remote.websocket

import android.util.Log
import com.example.youtube_summary_native.config.env.AppConfig
import com.example.youtube_summary_native.core.constants.ApiConstants
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketManager @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    private var webSocket: WebSocket? = null
    private var messageCallback: ((String, String) -> Unit)? = null

    fun connect(onMessage: (String, String) -> Unit) {
        messageCallback = onMessage
        val request = Request.Builder()
            .url("${AppConfig.WS_BASE_URL}${ApiConstants.SUMMARY_WEBSOCKET_ENDPOINT}")
            .build()

        webSocket = okHttpClient.newWebSocket(request, createWebSocketListener())
    }

    private fun createWebSocketListener() = object : WebSocketListener() {
        override fun onMessage(webSocket: WebSocket, text: String) {
            try {
                val jsonData = Json.decodeFromString<JsonObject>(text)
                val type = jsonData["type"]?.toString()?.removeSurrounding("\"")
                val data = jsonData["data"]?.toString()?.removeSurrounding("\"")

                if (type != null && data != null) {
                    messageCallback?.invoke(type, data)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing message: ${e.message}")
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
            Log.e(TAG, "WebSocket error: ${t.message}")
        }
    }

    fun sendMessage(message: String) {
        webSocket?.send(message)
    }

    fun close() {
        webSocket?.close(1000, "Closing connection")
        webSocket = null
        messageCallback = null
    }

    companion object {
        private const val TAG = "WebSocketManager"
    }
}