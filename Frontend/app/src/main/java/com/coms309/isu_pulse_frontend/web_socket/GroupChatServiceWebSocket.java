package com.coms309.isu_pulse_frontend.web_socket;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GroupChatServiceWebSocket {
    private static final String TAG = "GroupChatServiceWebSocket";
    private static GroupChatServiceWebSocket instance;
    private WebSocket webSocket;
    private GroupChatServiceListener listener;
    private String netId;
    private Long groupId;
    private Activity activity; // Reference to the Activity for running on the main thread

    private boolean isConnected = false; // Connection state
    private final List<JSONObject> messageQueue = new ArrayList<>(); // Message queue for unsent messages

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
        String wsUrl = String.format("ws://10.0.2.2:8080/ws/group-chat?netId=%s&groupId=%s", netId, groupId);
        Request request = new Request.Builder().url(wsUrl).build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d(TAG, "WebSocket Connected to " + wsUrl);
                isConnected = true;
                processQueuedMessages();
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "Received message: " + text);
                if (listener != null) {
                    activity.runOnUiThread(() -> {
                        try {
                            if (text.startsWith("[")) {  // JSONArray
                                JSONArray jsonArray = new JSONArray(text);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonMessage = jsonArray.getJSONObject(i);
                                    String senderNetId = jsonMessage.getString("senderNetId");
                                    String content = jsonMessage.getString("content");
                                    String timestamp = jsonMessage.getString("timestamp");
                                    listener.onMessageReceived(senderNetId, groupId, content, timestamp);
                                }
                            } else {  // JSONObject
                                JSONObject jsonMessage = new JSONObject(text);
                                String senderNetId = jsonMessage.getString("senderNetId");
                                String content = jsonMessage.getString("content");
                                String timestamp = jsonMessage.getString("timestamp");
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
                Log.e(TAG, "WebSocket Connection Failed: " + t.getMessage());
                isConnected = false;
                activity.runOnUiThread(() ->
                        Toast.makeText(activity, "WebSocket connection failed. Please try again later.", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "WebSocket Closing: " + reason);
                isConnected = false;
                webSocket.close(1000, null);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "WebSocket Closed: " + reason);
                isConnected = false;
            }
        });
    }

    public void sendMessage(String senderNetId, String content) {
        if (!isConnected || webSocket == null) {
            Log.e(TAG, "WebSocket is not connected. Queuing message.");
            try {
                JSONObject jsonMessage = new JSONObject();
                jsonMessage.put("senderNetId", senderNetId);
                jsonMessage.put("groupId", groupId);
                jsonMessage.put("content", content);
                messageQueue.add(jsonMessage);
            } catch (JSONException e) {
                Log.e(TAG, "Error creating JSON message", e);
            }
            return;
        }

        try {
            JSONObject jsonMessage = new JSONObject();
            jsonMessage.put("senderNetId", senderNetId);
            jsonMessage.put("groupId", groupId);
            jsonMessage.put("content", content);
            webSocket.send(jsonMessage.toString());
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON message", e);
        }
    }

    private void processQueuedMessages() {
        for (JSONObject message : messageQueue) {
            webSocket.send(message.toString());
        }
        messageQueue.clear();
    }

    public void close() {
        if (webSocket != null) {
            webSocket.close(1000, "User closed the group chat");
            webSocket = null;
            isConnected = false;
        }
    }

    public interface GroupChatServiceListener {
        void onMessageReceived(String senderNetId, Long groupId, String content, String timestamp);
    }
}
