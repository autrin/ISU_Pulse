package com.coms309.isu_pulse_frontend.web_socket;

import android.app.Activity;
import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GroupChatServiceWebSocket {
    private static final String TAG = "GroupChatServiceWebSocket";
    private static GroupChatServiceWebSocket instance;
    private WebSocket webSocket;
    private GroupChatServiceListener listener;
    private String netId;
    private Long groupId;
    private Activity activity; // Reference to the Activity for running on the main thread

    private GroupChatServiceWebSocket(GroupChatServiceListener listener, String netId, Long groupId, Activity activity) {
        this.listener = listener;
        this.netId = netId;
        this.groupId = groupId;
        this.activity = activity;
        connectWebSocket();
    }

    private GroupChatServiceWebSocket() {
        // Empty private constructor to prevent direct instantiation
    }

    public static synchronized GroupChatServiceWebSocket getInstance(GroupChatServiceListener listener, String netId, Long groupId, Activity activity) {
        if (instance == null) {
            instance = new GroupChatServiceWebSocket(listener, netId, groupId, activity);
            Log.d(TAG, "GroupChatServiceWebSocket initialized");
        } else {
            instance.setWebSocketListener(listener);
        }
        return instance;
    }

    public void setWebSocketListener(GroupChatServiceListener listener) {
        this.listener = listener;
    }

    private void connectWebSocket() {
        OkHttpClient client = new OkHttpClient();
        String wsUrl = String.format("ws://10.0.2.2:8080/ws/groupChat?netId=%s&groupId=%s", netId, groupId);
        Request request = new Request.Builder().url(wsUrl).build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d(TAG, "WebSocket Connected to " + wsUrl);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "Received message: " + text);
                if (listener != null) {
                    // Run on the main thread
                    activity.runOnUiThread(() -> {
                        try {
                            // Check if the received text is a JSONArray or JSONObject
                            if (text.startsWith("[")) {  // Indicates a JSONArray
                                JSONArray jsonArray = new JSONArray(text);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonMessage = jsonArray.getJSONObject(i);
                                    String senderNetId = jsonMessage.getString("senderNetId");
                                    String content = jsonMessage.getString("content");
                                    String timestamp = jsonMessage.getString("timestamp");

                                    // Pass each message to the listener
                                    listener.onMessageReceived(senderNetId, groupId, content, timestamp);
                                }
                            } else {  // Single JSONObject
                                JSONObject jsonMessage = new JSONObject(text);
                                String senderNetId = jsonMessage.getString("senderNetId");
                                String content = jsonMessage.getString("content");
                                String timestamp = jsonMessage.getString("timestamp");

                                // Pass the single message to the listener
                                listener.onMessageReceived(senderNetId, groupId, content, timestamp);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing received message JSON", e);
                        }
                    });
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e(TAG, "WebSocket Error: " + t.getMessage());
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                webSocket.close(1000, null);
                Log.d(TAG, "WebSocket Closing: " + reason);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "WebSocket Closed: " + reason);
            }
        });
    }

    public void sendMessage(String senderNetId, String content) {
        JSONObject jsonMessage = new JSONObject();
        try {
            jsonMessage.put("senderNetId", senderNetId);
            jsonMessage.put("groupId", groupId);
            jsonMessage.put("content", content);
            webSocket.send(jsonMessage.toString());
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON message", e);
        }
    }

    public void close() {
        if (webSocket != null) {
            webSocket.close(1000, "User closed the group chat");
            webSocket = null;
        }
    }

    public interface GroupChatServiceListener {
        void onMessageReceived(String senderNetId, Long groupId, String content, String timestamp);
    }
}
