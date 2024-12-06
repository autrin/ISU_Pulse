package com.coms309.isu_pulse_frontend.api;

import static com.coms309.isu_pulse_frontend.api.Constants.BASE_URL;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.coms309.isu_pulse_frontend.chat_system.ChatMessage;
import com.coms309.isu_pulse_frontend.chat_system.ChatMessageDTO;
import com.coms309.isu_pulse_frontend.loginsignup.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ChatApiService {
    private RequestQueue requestQueue;
    private ChatMessage message;

    public ChatApiService(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    // Fetch Chat History between two users
    public void getChatHistory(String user1NetId, String user2NetId, final ChatHistoryCallback callback) {
        String url = BASE_URL + "chat/history?user1NetId=" + user1NetId + "&user2NetId=" + user2NetId;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<ChatMessageDTO> chatHistory = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonMessage = response.getJSONObject(i);
                                ChatMessageDTO message = new ChatMessageDTO(
                                        jsonMessage.getString("senderNetId"),
                                        jsonMessage.getString("recipientNetId"),
                                        jsonMessage.getString("content"),
                                        jsonMessage.getString("timestamp")
                                );
                                chatHistory.add(message);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                callback.onError("Error parsing chat history.");
                                return;
                            }
                        }
                        callback.onSuccess(chatHistory);
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

    // Fetch Latest Message between two users
    public void getLatestMessage(String netId, final ChatLatestCallback callback) {
        String url = BASE_URL + "chat/allLatestMessages/" + netId;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<ChatMessage> chatHistory = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonMessage = response.getJSONObject(i);
                                if (jsonMessage.has("group")){
                                    if (jsonMessage.isNull("sender")){
                                        JSONObject groupJson = jsonMessage.getJSONObject("group");
                                        message = new ChatMessage(
                                                groupJson.getLong("id"),
                                                groupJson.getString("name"),
                                                jsonMessage.getString("content"),
                                                jsonMessage.getString("timestamp")
                                        );
                                    }
                                    else {
                                        JSONObject senderJson = jsonMessage.getJSONObject("sender");
                                        JSONObject groupJson = jsonMessage.getJSONObject("group");
                                        message = new ChatMessage(
                                                senderJson.getString("firstName"),
                                                senderJson.getString("lastName"),
                                                senderJson.getString("netId"),
                                                groupJson.getLong("id"),
                                                groupJson.getString("name"),
                                                jsonMessage.getString("content"),
                                                jsonMessage.getString("timestamp")
                                        );
                                    }
                                }
                                else{
                                    JSONObject senderJson = jsonMessage.getJSONObject("sender");
                                    JSONObject recipientJson = jsonMessage.getJSONObject("recipient");
                                    message = new ChatMessage(
                                            senderJson.getString("firstName"),
                                            senderJson.getString("lastName"),
                                            recipientJson.getString("firstName"),
                                            recipientJson.getString("lastName"),
                                            senderJson.getString("netId"),
                                            recipientJson.getString("netId"),
                                            jsonMessage.getString("content"),
                                            jsonMessage.getString("timestamp")
                                    );
                                }
                                chatHistory.add(message);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                callback.onError("Error parsing chat history.");
                                return;
                            }
                        }
                        callback.onSuccess(chatHistory);
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

    public interface ChatLatestCallback {
        void onSuccess(List<ChatMessage> latestMessage);
        void onError(String error);
    }


    // Callback interface for responses
    public interface ChatHistoryCallback {
        void onSuccess(List<ChatMessageDTO> chatHistory);
        void onError(String error);
    }
}