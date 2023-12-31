package com.example.carapp.services;

import android.util.Log;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class LocationWebSocketService {
    private WebSocket webSocket;

    private final LocationUpdateListener listener;

    public LocationWebSocketService(LocationUpdateListener listener) {
        this.listener = listener;
    }

    public void startWebSocket() {
        Request request = new Request.Builder().url("ws://44.212.1.125:4000/ws").build();
        OkHttpClient client = new OkHttpClient();

        client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                LocationWebSocketService.this.webSocket = webSocket;
                listener.onConnectionOpened();
                Log.d("WebSocket", "WebSocket connection opened");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                listener.onLocationUpdateReceived(text);
                Log.d("WebSocket", "Received message: " + text);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                listener.onConnectionFailure(t);
                Log.e("WebSocket", "WebSocket connection failure: " + t.getMessage());
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                listener.onConnectionClosed();
                Log.d("WebSocket", "WebSocket connection closed");
            }
        });
    }

    public void stopWebSocket() {
        webSocket.close(1000, "Closing connection");
        Log.d("WebSocket", "WebSocket connection stopped");
    }

    public interface LocationUpdateListener {
        void onConnectionOpened();
        void onLocationUpdateReceived(String updates);
        void onConnectionFailure(Throwable t);
        void onConnectionClosed();
    }
}

