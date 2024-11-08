package com.coms309.isu_pulse_frontend.api;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class AnnouncementWebSocketClient {
    private static final String TAG = "AnnouncementWebSocket";
    private WebSocketClient webSocketClient;
    private WebSocketListener listener;

    public interface WebSocketListener {
        void onMessageReceived(String message);
    }

    public AnnouncementWebSocketClient(WebSocketListener listener) {
        this.listener = listener;
    }

    public void connectWebSocket(String netId, String userType) {
        String wsUrl = Constants.BASE_URL_WS + "ws/announcement?netId=" + netId + "&userType=" + userType;
        URI uri;
        try {
            uri = new URI(wsUrl);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.d(TAG, "WebSocket Opened");
            }

            @Override
            public void onMessage(String message) {
                Log.d(TAG, "Received message: " + message);
                if (listener != null) {
                    listener.onMessageReceived(message);
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d(TAG, "WebSocket Closed: " + reason);
            }

            @Override
            public void onError(Exception ex) {
                Log.e(TAG, "WebSocket Error: " + ex.getMessage());
            }
        };
        webSocketClient.connect();
    }

    public void disconnectWebSocket() {
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }

    public void sendMessage(String action, long scheduleId, String content) {
        String message = "{\"action\":\"" + action + "\",\"scheduleId\":" + scheduleId + ",\"content\":\"" + content + "\"}";
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.send(message);
            Log.d(TAG, "Message sent: " + message);
        }
    }
}
