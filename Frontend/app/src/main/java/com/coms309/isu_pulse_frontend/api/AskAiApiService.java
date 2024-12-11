package com.coms309.isu_pulse_frontend.api;

import static com.coms309.isu_pulse_frontend.api.Constants.BASE_URL;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.coms309.isu_pulse_frontend.chat_system.ChatMessage;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AskAiApiService {
    private final com.android.volley.RequestQueue requestQueue;
    private Context context;

    public AskAiApiService(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    public interface SendMessageCallback {
        void onSuccess(String response);
        void onError(String error);
    }
    public interface MessageHistoryCallback {
        void onSuccess(List<ChatMessage> chatHistory);
        void onError(String error);
    }

    public void sendMessageToAi(String messageContent, SendMessageCallback callback) {
        String url = BASE_URL + "chatbot/send"
                + "?netId=" + UserSession.getInstance().getNetId()
                + "&message=" + messageContent;

        // Use StringRequest since the server returns a plain string
        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    // 'response' is the raw string from the server
                    Log.d("Ask AI API Response", response);
                    callback.onSuccess(response);
                },
                error -> {
                    String errorMessage = (error.getMessage() != null) ? error.getMessage() : "Unknown error in sendMessageToAi()";
                    Log.e("Ask AI API Error", errorMessage);
                    callback.onError(errorMessage);
                }
        );

        requestQueue.add(request);
    }

    // Fetch chat history
    public void fetchChatHistory(MessageHistoryCallback callback) {
        String url = BASE_URL + "chatbot/history?netId=" + UserSession.getInstance().getNetId();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        List<ChatMessage> chatHistory = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject messageObject = response.getJSONObject(i);
                            int id = messageObject.optInt("id", -1);
                            String sender = messageObject.optString("sender", "UNKNOWN");
                            String message = messageObject.optString("message", "");
                            String timestamp = messageObject.optString("timestamp", "");

                            // Determine if the message is from the user
                            // If sender == "USER", isSent = true; if sender == "CHATBOT", isSent = false.
                            boolean isSent = sender.equalsIgnoreCase("USER");

                            // Use the constructor that sets id and sender, and then determine isSent below
                            ChatMessage chatMessage = new ChatMessage(id, sender, message, timestamp);
                            // Update isSent field dynamically
                            // You could modify the ChatMessage class to set isSent based on sender directly,
                            // or just use the existing constructor for isSent messages (For now, let's assume we tweak the ChatMessage class)

                            // If you want to keep things consistent, you can also create a new constructor:
                            // For now, just assume ChatMessage has a setter or we always set it after creation.
                            chatMessage.setSent(isSent);

                            chatHistory.add(chatMessage);
                        }
                        callback.onSuccess(chatHistory);
                    } catch (JSONException e) {
                        callback.onError("Error parsing JSON: " + e.getMessage());
                    }
                },
                error -> callback.onError("Error fetching chat history: " + (error.getMessage() != null ? error.getMessage() : "Unknown error"))
        );

        requestQueue.add(jsonArrayRequest);
    }

}
