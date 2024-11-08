package com.coms309.isu_pulse_frontend.api;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

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
                Log.d(TAG, "WebSocket Closed: " + reason + " (Code: " + code + ")");
                if (code == 1006) {
                    Log.d(TAG, "Attempting to reconnect...");
                    connectWebSocket(netId, userType);
                }
            }

            @Override
            public void onError(Exception ex) {
                Log.e(TAG, "WebSocket Error: " + ex.getMessage());
            }
        };
        webSocketClient.connect();
    }

    public void setListener(WebSocketListener listener) {
        this.listener = listener;
    }

    public void postAnnouncement(long scheduleId, String content) {
        sendActionMessage("post", scheduleId, content, 0);
    }

    public void updateAnnouncement(long announcementId, String content) {
        sendActionMessage("update", 0, content, announcementId);
    }

    public void deleteAnnouncement(long announcementId) {
        sendActionMessage("delete", 0, null, announcementId);
    }

    public void disconnectWebSocket() {
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }

    public void sendActionMessage(String action, long scheduleId, String content, long announcementId) {
        try {
            JSONObject message = new JSONObject();
            message.put("action", action);
            if (scheduleId != 0) message.put("scheduleId", 7); // hardcoded schedule ID for testing
            if (content != null) message.put("content", content);
            if (announcementId != 0) message.put("announcementId", announcementId);
            if (webSocketClient != null && webSocketClient.isOpen()) {
                webSocketClient.send(message.toString());
                Log.d(TAG, "Sent message: " + message.toString());
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON message", e);
        }
    }

}
