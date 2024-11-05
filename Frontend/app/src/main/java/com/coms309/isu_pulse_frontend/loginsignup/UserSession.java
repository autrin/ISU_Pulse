package com.coms309.isu_pulse_frontend.loginsignup;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSession {

    private static UserSession instance;
    private String netId;
    private String userType;

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
        editor.apply();
        netId = null;
    }
}
