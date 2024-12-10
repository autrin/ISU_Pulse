package com.coms309.isu_pulse_frontend.api;

import static com.coms309.isu_pulse_frontend.api.Constants.BASE_URL;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthenticationService {

    // Define a callback interface to handle the asynchronous response
    public interface VolleyCallback {
        void onSuccess(JSONObject result);
        void onError(String message);
    }

    public interface LoginCallback {
        void onSuccess(String result);
        void onError(String message);
    }

    public interface ForgetPasswordCallback {
        void onSuccess(String result);
        void onError(String message);
    }


    public void deleteAccount(String netId, Context context, final LoginCallback callback){
        String url = BASE_URL + "users/" + netId;
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Success callback
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Error callback
                        callback.onError("Failed to delete account: " + error.getMessage());
                    }
                });
        queue.add(request);
    }

    public void sendOtp(String email, Context context, final ForgetPasswordCallback callback){
        String url = BASE_URL + "auth/sendOtp?email=" + email;
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Success callback
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Error callback
                        callback.onError("Failed to send otp: " + error.getMessage());
                    }
                });
        queue.add(request);
    }

    public void verifyOtp(String email, String otp, Context context, final ForgetPasswordCallback callback){
        String url = BASE_URL + "auth/verifyOtp?email=" + email + "&otp=" + otp;
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Success callback
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Error callback
                        callback.onError("Failed to verify otp: " + error.getMessage());
                    }
                });
        queue.add(request);
    }


    public void forgotPassword(String netId, String newHashPassword, Context context, final ForgetPasswordCallback callback) {
        String url = BASE_URL + "users/forgotPassword?netId=" + netId + "&newHashPassword=" + newHashPassword;
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Success callback
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Error callback
                        callback.onError("Failed to update password: " + error.getMessage());
                    }
                });
        queue.add(request);
    }


    public void registerNewUser(
            String netId,
            String firstName,
            String lastName,
            String email,
            String password,
            String imageUrl,
            String userType,
            Context context,
            final LoginCallback callback
    ) {
        String url = BASE_URL + "users";

        // Create the JSON object representing the user
        JSONObject userJson = new JSONObject();
        try {
            userJson.put("netId", netId);
            userJson.put("firstName", firstName);
            userJson.put("lastName", lastName);
            userJson.put("email", email);
            userJson.put("hashedPassword", password);
            userJson.put("profilePictureUrl", imageUrl);
            userJson.put("userType", userType);
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onError("Failed to create JSON body: " + e.getMessage());
            return;
        }
        Log.d("RegisterNewUser", "URL: " + url);
        Log.d("RegisterNewUser", "Payload: " + userJson.toString());

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, url,
                response -> {
                    // Handle plain string response
                    callback.onSuccess(response);
                },
                error -> {
                    // Handle error response
                    callback.onError("Failed to register user: " + error.getMessage());
                }
        ) {
            @Override
            public byte[] getBody() {
                return userJson.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        queue.add(stringRequest);
    }


    public void checkUserExists(String netId, Context context, final VolleyCallback callback) {
        String url = BASE_URL + "users/" + netId;
        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                callback::onSuccess,
                error -> callback.onError(error.toString())
        );

        queue.add(jsonObjectRequest);
    }
}
