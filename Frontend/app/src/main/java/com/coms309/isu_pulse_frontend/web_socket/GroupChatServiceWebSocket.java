package com.coms309.isu_pulse_frontend.web_socket;

import android.app.Activity;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class GroupChatServiceWebSocket {
    private static final String TAG = "GroupChatWebSocket";
    private static GroupChatServiceWebSocket instance;
    private WebSocket webSocket;
    private ChatServiceListener listener;
    private String netId; // The current user's NetID
    private Long groupId;
    private Activity activity;

    /**
     * Private constructor for the singleton instance.
     * @param listener A listener for incoming messages
     * @param netId The current user's NetID
     * @param groupId The group ID for the chat
     */
    private GroupChatServiceWebSocket(ChatServiceListener listener, String netId, Long groupId) {
        this.listener = listener;
        this.netId = netId;
        this.groupId = groupId;

        // Debug logging to verify values
        Log.d(TAG, "Initializing WebSocket with netId: " + this.netId + " and groupId: " + this.groupId);

        connectWebSocket();
    }

    /**
     * Get an instance of the WebSocket connection.
     * This ensures that any previous instance is closed before creating a new one.
     */
    public static synchronized GroupChatServiceWebSocket getInstance(ChatServiceListener listener, String netId, Long groupId, AppCompatActivity activity) {
        if (instance != null) {
            instance.close(); // Ensure the previous instance is fully closed
        }
        instance = new GroupChatServiceWebSocket(listener, netId, groupId);
        return instance;
    }

    /**
     * Connects to the WebSocket endpoint.
     */
    private void connectWebSocket() {
        OkHttpClient client = new OkHttpClient();

        // Construct the WebSocket URL
        String wsUrl = String.format("ws://10.0.2.2:8080/ws/group-chat?netId=%s&groupId=%s", netId, groupId);
//        String wsUrl = String.format("ws://coms-3090-042.class.las.iastate.edu/ws/group-chat?netId=%s&groupId=%s", netId, groupId);
        Log.d(TAG, "Connecting to WebSocket URL: " + wsUrl);

        Request request = new Request.Builder().url(wsUrl).build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d(TAG, "WebSocket Connected: " + wsUrl);

                // Send a fetchMessages request upon connection if required by server
                JSONObject fetchRequest = new JSONObject();
                try {
//                    fetchRequest.put("action", "fetchMessages");
                    fetchRequest.put("groupId", groupId);
                    fetchRequest.put("senderNetId", netId);
//                    webSocket.send(fetchRequest.toString());
                    Log.d(TAG, "Sent fetchMessages request: " + fetchRequest.toString());
                } catch (JSONException e) {
                    Log.e(TAG, "Error creating fetch request", e);
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "WebSocket message received: " + text);


                try {
                    if (text.startsWith("[")) {
                        // Handle JSON array (chat history)
                        JSONArray messageArray = new JSONArray(text);
                        for (int i = 0; i < messageArray.length(); i++) {
                            JSONObject messageJson = messageArray.getJSONObject(i);
                            processMessage(messageJson);
                        }
                    } else if (text.startsWith("{")) {
                        // Handle single JSON object
                        JSONObject messageJson = new JSONObject(text);
                        processMessage(messageJson);
                    } else {
                        // Handle plain text (non-JSON) messages
                        Log.e(TAG, "Received non-JSON message: " + text);
                        if (text.contains("Invalid sender or group")) {
                            Log.e(TAG, "Server rejected request: " + text);
                        }
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing WebSocket message: " + text, e);
                }
            }

            /**
             * Process a single JSON message from the server.
             */
            private void processMessage(JSONObject messageJson) {
                try {
                    String senderNetId = messageJson.optString("senderNetId", null);
                    Long grpId = messageJson.optLong("groupId", -1);
                    String content = messageJson.optString("content", "no content");
                    String timestamp = messageJson.optString("timestamp", "unknown");

                    // Pass the message to the listener
                    if (listener != null) {
                        listener.onMessageReceived(senderNetId, grpId, content, timestamp);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing message JSON", e);
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e(TAG, "WebSocket Failure: " + t.getMessage());
                if (response != null) {
                    Log.e(TAG, "Response: " + response.toString());
                }
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "WebSocket Closed: " + reason);
            }
        });
    }

    /**
     * Send a message over the WebSocket.
     * Make sure to include the required fields the server expects.
     */
    public void sendMessage(String senderNetId, Long groupId, String content) {
        JSONObject messageJson = new JSONObject();
        try {
            // If the server requires an "action" field for sending messages
//            messageJson.put("action", "sendMessage");
            messageJson.put("senderNetId", senderNetId);
            messageJson.put("groupId", groupId);
            messageJson.put("content", content);

            webSocket.send(messageJson.toString());
            Log.d(TAG, "Message sent: " + messageJson.toString());

            // Optimistic update: Show the message immediately in the chat.
//            if (listener != null) {
//                listener.onMessageReceived(senderNetId, groupId, content, LocalDateTime.now().toString());
//            }
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON message", e);
        }
    }

    /**
     * Close the WebSocket connection.
     */
    public void close() {
        if (webSocket != null) {
            webSocket.close(1000, "Group chat closed");
            webSocket = null;
        }
    }

    public void setWebSocketListener(ChatServiceListener listener) {
        this.listener = listener;
    }

    public interface ChatServiceListener {
        void onMessageReceived(String senderNetId, Long groupId, String content, String timestamp);
    }
}
