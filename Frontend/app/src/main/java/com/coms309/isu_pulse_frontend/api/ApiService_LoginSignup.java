package com.coms309.isu_pulse_frontend.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class ApiService_LoginSignup {

    private static final String BASE_URL = "http://coms-3090-042.class.las.iastate.edu:8080/users";

    // Define a callback interface to handle the asynchronous response
    public interface VolleyCallback {
        void onSuccess(JSONObject result);

        void onError(String message);
    }

    // Method to check if a user exists by netId
    public void checkUserExists(String netId, Context context, final VolleyCallback callback) {
        String url = BASE_URL + "/" + netId;
        RequestQueue queue = Volley.newRequestQueue(context);

        // Create a GET request using Volley
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // User exists, return the response
                    callback.onSuccess(response);
                }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // User does not exist or another error occurred
                callback.onError(error.toString());
            }
        });

        // Add the request to the RequestQueue
        queue.add(jsonObjectRequest);
    }
}
