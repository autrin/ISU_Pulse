package com.coms309.isu_pulse_frontend.api;

import static com.coms309.isu_pulse_frontend.api.Constants.BASE_URL;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.coms309.isu_pulse_frontend.loginsignup.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserService {

    private final RequestQueue requestQueue;

    public UserService(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public void searchUsers(String name, final UserCallback callback) {
        String url = BASE_URL + "users/search?name=" + name;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<User> users = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonMessage = response.getJSONObject(i);
                                User user = new User(
                                        jsonMessage.getString("netId"),
                                        jsonMessage.getString("firstName"),
                                        jsonMessage.getString("lastName"),
                                        jsonMessage.getString("email"),
                                        jsonMessage.getString("hashedPassword"),
                                        jsonMessage.getString("profilePictureUrl"),
                                        jsonMessage.getString("userType")
                                );
                                users.add(user);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                callback.onError("Error parsing chat history.");
                                return;
                            }
                        }
                        callback.onSuccess(users);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError("Error fetching chat history: " + error.getMessage());
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);

    }

    public interface UserCallback {
        void onSuccess(List<User> users);
        void onError(String message);
    }
}
