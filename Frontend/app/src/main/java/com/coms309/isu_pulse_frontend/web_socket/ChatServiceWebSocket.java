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

public class ChatServiceWebSocket {
    private static final String TAG = "ChatServiceWebSocket";
    private static ChatServiceWebSocket instance;
    private WebSocket webSocket;
    private ChatServiceListener listener;
    private String netId;
    private String recipientNetId;
    private Activity activity;  // Reference to the Activity for running on the main thread

    private ChatServiceWebSocket(ChatServiceListener listener, String netId, String recipientNetId, Activity activity) {
        this.listener = listener;
        this.netId = netId;
        this.recipientNetId = recipientNetId;
        this.activity = activity;
        connectWebSocket();
    }

    private ChatServiceWebSocket() {

    }

    public static synchronized ChatServiceWebSocket getInstance(ChatServiceListener listener, String netId, String recipientNetId, Activity activity) {
        if (instance == null) {
            instance = new ChatServiceWebSocket(listener, netId, recipientNetId, activity);
            Log.d(TAG, "ChatServiceWebSocket initialized");
        } else {
            instance.setWebSocketListener(listener);
//            instance.netId = netId;
//            instance.recipientNetId = recipientNetId;
//            instance.activity = activity;
//            instance.connectWebSocket();  // Reconnect with updated parameters if necessary
        }
        return instance;
    }

    public void setWebSocketListener(ChatServiceListener listener) {
        this.listener = listener;
    }

    private void connectWebSocket() {
        OkHttpClient client = new OkHttpClient();
        String wsUrl = String.format("ws://10.0.2.2:8080/ws/chat?netId=%s&recipientNetId=%s", netId, recipientNetId);
//        String wsUrl = String.format("ws://coms-3090-042.class.las.iastate.edu:8080/ws/chat?netId=%s&recipientNetId=%s", netId, recipientNetId);
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
                                    String recipientNetId = jsonMessage.getString("recipientNetId");
                                    String content = jsonMessage.getString("content");
                                    String timestamp = jsonMessage.getString("timestamp");

                                    // Pass each message to the listener
                                    listener.onMessageReceived(senderNetId, recipientNetId, content, timestamp);
                                }
                            } else {  // Single JSONObject
                                JSONObject jsonMessage = new JSONObject(text);
                                String senderNetId = jsonMessage.getString("senderNetId");
                                String recipientNetId = jsonMessage.getString("recipientNetId");
                                String content = jsonMessage.getString("content");
                                String timestamp = jsonMessage.getString("timestamp");

                                // Pass the single message to the listener
                                listener.onMessageReceived(senderNetId, recipientNetId, content, timestamp);
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

    public void sendMessage(String senderNetId, String recipientNetId, String content) {
        JSONObject jsonMessage = new JSONObject();
        try {
            jsonMessage.put("senderNetId", senderNetId);
            jsonMessage.put("recipientNetId", recipientNetId);
            jsonMessage.put("content", content);
            webSocket.send(jsonMessage.toString());
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON message", e);
        }
    }

    public void close() {
        if (webSocket != null) {
            webSocket.close(1000, "User closed the chat");
            webSocket = null;
        }
    }

    public interface ChatServiceListener {
        void onMessageReceived(String senderNetId, String recipientNetId, String content, String timestamp);
    }
}
