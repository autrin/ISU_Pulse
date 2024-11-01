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
import com.bumptech.glide.Glide;
import com.coms309.isu_pulse_frontend.model.Profile;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateAccount {

    public interface VolleyCallback {
        void onSuccess(String result);
        void onError(String message);
    }

    public interface ProfileCallback {
        void onSuccess(Profile profile);
        void onError(VolleyError error);
    }

    public void updateUserPassword(
            String netId,
            String newPassword,
            Context context,
            final VolleyCallback callback
    ) {
        String url = BASE_URL + "users/updatepw/" + netId + "?newPassword=" + newPassword;
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest putRequest = new StringRequest(
                Request.Method.PUT, url,
                callback::onSuccess,
                error -> {
                    callback.onError(error.toString());
                    error.printStackTrace();
                }
        );
        queue.add(putRequest);
    }

    public static void fetchProfileData(Context context, final ProfileCallback callback) {
        String url = BASE_URL + "profile/userTest"; //TODO: need to use the actual user's netId later
        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        Profile profile = gson.fromJson(response.toString(), Profile.class);
                        callback.onSuccess(profile);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                }
        );

        queue.add(jsonObjectRequest);
    }

    public void updateProfile(
            String netId,
            String description,
            String externalUrl,
            String linkedinUrl,
            Context context,
            final VolleyCallback callback
    ) {
        // Construct the URL with query parameters for PUT request
        String url = BASE_URL + "profile/" + netId
                + "?description=" + description
                + "&externalUrl=" + externalUrl
                + "&linkedinUrl=" + linkedinUrl;

        RequestQueue queue = Volley.newRequestQueue(context);

        // Create a PUT request
        StringRequest putRequest = new StringRequest(
                Request.Method.PUT, url,
                response -> callback.onSuccess(response),
                error -> {
                    callback.onError(error.toString());
                    error.printStackTrace();
                }
        );
        queue.add(putRequest);
    }

}
