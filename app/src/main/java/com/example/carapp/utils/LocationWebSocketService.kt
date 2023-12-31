package com.example.carapp.utils

import android.util.Log
import kotlinx.coroutines.*
import okhttp3.*
import okio.ByteString

class LocationWebSocketService(private val listener: LocationUpdateListener) {
    private lateinit var webSocket: WebSocket
    private var job: Job? = null

    fun startWebSocket() {
        val request = Request.Builder().url("ws://54.227.148.12:4000/ws").build()
        val client = OkHttpClient()

        client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                this@LocationWebSocketService.webSocket = webSocket
                listener.onConnectionOpened()
                sendMessage("Hello there")
                Log.d("WebSocket", "WebSocket connection opened")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                listener.onLocationUpdateReceived(text)
                Log.d("WebSocket", "Received message: $text")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                listener.onConnectionFailure(t)
                Log.e("WebSocket", "WebSocket connection failure: ${t.message}")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                listener.onConnectionClosed()
                Log.d("WebSocket", "WebSocket connection closed")
            }
        })
    }

    fun stopWebSocket() {
        if (::webSocket.isInitialized && this::webSocket.isInitialized) {
            webSocket.close(1000, "Closing connection")
            Log.d("WebSocket", "WebSocket connection stopped")
        } else {
            Log.e("WebSocket", "WebSocket was not initialized or already closed")
        }
    }
    fun sendMessage(message: String) {
        if (::webSocket.isInitialized && this::webSocket.isInitialized) {
            webSocket.send(message)
            Log.d("WebSocket", "Sent message: $message")
        } else {
            Log.e("WebSocket", "WebSocket was not initialized or already closed")
        }
    }

    interface LocationUpdateListener {
        fun onConnectionOpened()
        fun onLocationUpdateReceived(updates: String)
        fun onConnectionFailure(t: Throwable)
        fun onConnectionClosed()
    }
}

