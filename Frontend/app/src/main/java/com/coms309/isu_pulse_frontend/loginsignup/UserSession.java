package com.coms309.isu_pulse_frontend.loginsignup;

import android.content.Context;
import android.content.SharedPreferences;

import com.coms309.isu_pulse_frontend.api.AnnouncementWebSocketClient;

public class UserSession {

    private static UserSession instance;
    private String netId;
    private String userType;
    private AnnouncementWebSocketClient webSocketClient;

    private UserSession(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        this.netId = sharedPreferences.getString("netId", null);
        this.userType = sharedPreferences.getString("userType", null);
    }

    public static synchronized UserSession getInstance(Context context) {
        if (instance == null) {
            instance = new UserSession(context);
        }
        return instance;
    }

    public String getNetId() {
        return netId;
    }

    public String getUserType() {
        return userType;
    }

    public void setNetId(String netId, Context context) {
        this.netId = netId;
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("netId", netId);
        editor.apply();
    }

    public void setUserType(String userType, Context context) {
        this.userType = userType;
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userType", userType);
        editor.apply();
    }

    public void clearSession(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("netId");
        editor.remove("userType"); // Also clear the user type for consistency
        editor.apply();
        netId = null;
        userType = null;

        // Disconnect WebSocket on session clear
        disconnectWebSocket();
    }

    // Method to initialize and connect the WebSocket client
    public void initWebSocket(String netId, String userType) {
        this.netId = netId;
        this.userType = userType;

        // Connect only if webSocketClient is null or disconnected
        if (webSocketClient == null) {
            webSocketClient = new AnnouncementWebSocketClient(message -> {
                // Handle the received message or update LiveData here
                // Example: broadcast the message or use LiveData
            });
            webSocketClient.connectWebSocket(netId, userType);
        }
    }

    public AnnouncementWebSocketClient getWebSocketClient() {
        return webSocketClient;
    }

    // Method to disconnect WebSocket when the app is terminating
    public void disconnectWebSocket() {
        if (webSocketClient != null) {
            webSocketClient.disconnectWebSocket();
            webSocketClient = null;
        }
    }
}
