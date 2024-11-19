package com.coms309.isu_pulse_frontend.loginsignup;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.coms309.isu_pulse_frontend.api.AnnouncementWebSocketClient;

/**
 * Singleton class to manage the user's session in the application.
 * Handles user information, shared preferences, and WebSocket connections.
 */
public class UserSession {

    private static UserSession instance; // Singleton instance
    private String netId;               // User's NetID
    private long id;                    // User's unique ID
    private String userType;            // User type (e.g., "student", "faculty")
    private AnnouncementWebSocketClient webSocketClient; // WebSocket client for announcements
    private static final String TAG = "UserSession";      // Tag for logging

    /**
     * Private default constructor to enforce Singleton pattern.
     */
    private UserSession() {}

    /**
     * Private constructor to initialize user session from shared preferences.
     *
     * @param context the application context
     */
    private UserSession(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        this.netId = sharedPreferences.getString("netId", null);
    }

    // ---- Static Methods ----

    /**
     * Returns the singleton instance of the UserSession.
     * Creates a new instance if one does not already exist.
     *
     * @return the singleton instance of UserSession
     */
    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    /**
     * Returns the singleton instance of the UserSession and initializes it using the context.
     * Creates a new instance if one does not already exist.
     *
     * @param context the application context
     * @return the singleton instance of UserSession
     */
    public static synchronized UserSession getInstance(Context context) {
        if (instance == null) {
            instance = new UserSession(context);
        }
        return instance;
    }

    // ---- Getters ----

    /**
     * Gets the NetID of the user.
     *
     * @return the NetID of the user
     */
    public String getNetId() {
        return netId;
    }

    /**
     * Gets the unique ID of the user.
     *
     * @return the unique ID of the user
     */
    public long getId() {
        return id;
    }

    /**
     * Gets the user type (e.g., "student", "faculty").
     *
     * @return the user type
     */
    public String getUserType() {
        return userType;
    }

    /**
     * Gets the WebSocket client for announcements.
     *
     * @return the WebSocket client
     */
    public AnnouncementWebSocketClient getWebSocketClient() {
        return webSocketClient;
    }

    // ---- Setters ----

    /**
     * Sets the NetID of the user.
     *
     * @param netId the NetID to set
     */
    public void setNetId(String netId) {
        this.netId = netId;
    }

    /**
     * Sets the unique ID of the user.
     *
     * @param id the unique ID to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Sets the NetID and saves it to shared preferences.
     * Also initializes the WebSocket connection.
     *
     * @param netId   the NetID to set
     * @param context the application context
     */
    public void setNetId(String netId, Context context) {
        this.netId = netId;
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("netId", netId);
        editor.apply();
        initWebSocket();
    }

    /**
     * Sets the user type and saves it to shared preferences.
     * Also initializes the WebSocket connection.
     *
     * @param userType the user type to set
     * @param context  the application context
     */
    public void setUserType(String userType, Context context) {
        this.userType = userType;
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userType", userType);
        editor.apply();
        initWebSocket();
    }

    // ---- Session Management ----

    /**
     * Clears the current user session and removes data from shared preferences.
     * Also disconnects the WebSocket connection.
     *
     * @param context the application context
     */
    public void clearSession(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("netId");
        editor.remove("userType");
        editor.apply();
        netId = null;
        userType = null;
        disconnectWebSocket();
    }

    // ---- WebSocket Management ----

    /**
     * Initializes the WebSocket connection for announcements.
     */
    private void initWebSocket() {
        if (webSocketClient == null && netId != null && userType != null) {
            webSocketClient = new AnnouncementWebSocketClient(message -> {
                Log.d(TAG, "Message received: " + message);
            });
            webSocketClient.connectWebSocket(netId, userType);
        }
    }

    /**
     * Disconnects the WebSocket connection if it exists.
     */
    public void disconnectWebSocket() {
        if (webSocketClient != null) {
            webSocketClient.disconnectWebSocket();
            webSocketClient = null;
            Log.d(TAG, "WebSocket disconnected");
        }
    }
}
