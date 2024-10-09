package com.coms309.isu_pulse_frontend.api;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthenticationService {
//    private static final String BASE_URL = "http://coms-3090-042.class.las.iastate.edu:8080/users";
    private static final String BASE_URL = "http://10.0.2.2:8080/users";

    // Define a callback interface to handle the asynchronous response
    public interface VolleyCallback {
        void onSuccess(JSONObject result);

        void onError(String message);
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
            final VolleyCallback callback
    ) {
        String url = BASE_URL;

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
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest postRequest = new JsonObjectRequest(
                Request.Method.POST, url, userJson,
                callback::onSuccess,
                error -> {
                    callback.onError(error.toString());
                    error.printStackTrace();
                }
        );

        queue.add(postRequest);
    }

    public void checkUserExists(String netId, Context context, final VolleyCallback callback) {
        String url = BASE_URL + "/" + netId;
        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                callback::onSuccess,
                error -> callback.onError(error.toString())
        );

        queue.add(jsonObjectRequest);
    }
}
