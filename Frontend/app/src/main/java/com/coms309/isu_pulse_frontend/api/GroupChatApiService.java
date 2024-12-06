package com.coms309.isu_pulse_frontend.api;

import static com.coms309.isu_pulse_frontend.api.Constants.BASE_URL;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class GroupChatApiService {
    private RequestQueue requestQueue;

    public GroupChatApiService(Context context)  {
        requestQueue = Volley.newRequestQueue(context);
    }

    public interface GroupChatCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    public void createGroupChat(String groupName, String netId, GroupChatCallback callback) {
        String url = BASE_URL + "groups/create?groupName=" + groupName + "&netId=" + netId;

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
                        callback.onError("Failed to create group: " + error.getMessage());
                    }
        });
        requestQueue.add(request);
    }

    public void addMemberToGroup(String adderNetId, String personAddedNetId, Long groupId, GroupChatCallback callback) {
        String url = BASE_URL + "groups/addMember?adderNetId=" + adderNetId + "&personAddedNetId=" + personAddedNetId + "&groupId=" + groupId;

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
                        callback.onError("Failed to add member: " + error.getMessage());
                    }
        });
        requestQueue.add(request);
    }
}
