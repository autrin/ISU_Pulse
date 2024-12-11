package com.coms309.isu_pulse_frontend.ui.ask_ai;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coms309.isu_pulse_frontend.MainActivity;
import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.adapters.AskAiAdapter;
import com.coms309.isu_pulse_frontend.api.AskAiApiService;
import com.coms309.isu_pulse_frontend.chat_system.ChatAdapter;
import com.coms309.isu_pulse_frontend.chat_system.ChatMessage;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AskAiActivity extends AppCompatActivity {

    private ImageButton backButton;
    private Button sendButton;
    private EditText messageEditText;
    private RecyclerView recyclerViewMessages;
    private TextView nameTextView;
    private ChatAdapter chatAdapter;
    private AskAiApiService askAiApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ask_ai);

        // Initialize UI components
        backButton = findViewById(R.id.buttonBack);
        nameTextView = findViewById(R.id.textViewUsername);
        messageEditText = findViewById(R.id.editTextMessage);
        sendButton = findViewById(R.id.buttonSend);
        recyclerViewMessages = findViewById(R.id.recyclerAskAiViewMessages);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(new ArrayList<>());
        recyclerViewMessages.setAdapter(chatAdapter);

        // Initialize API service
        askAiApiService = new AskAiApiService(this);

        // Set ChatGPT name
        nameTextView.setText("ChatGPT");

        // Handle back button
        backButton.setOnClickListener(v -> navigateToMainActivity());

        // Handle send button
        sendButton.setOnClickListener(v -> {
            String message = messageEditText.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessage(message);
                messageEditText.setText(""); // Clear the input field
            }
        });

        // Load chat history
        loadChatHistory();
    }
    private void loadChatHistory() {
        askAiApiService.fetchChatHistory(new AskAiApiService.MessageHistoryCallback() {
            @Override
            public void onSuccess(List<ChatMessage> chatHistory) {
                runOnUiThread(() -> {
                    // Clear adapter to avoid duplicates
                    chatAdapter.setMessages(new ArrayList<>());

                    if (chatHistory.isEmpty()) {
                        displayMessage("Hi! I'm ChatGPT. How can I assist you today?", false, getCurrentTimestamp());
                    } else {
                        for (ChatMessage msg : chatHistory) {
                            displayMessage(msg.getMessage(), msg.isSent(), msg.getTimestamp());
                        }
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() ->
                        Toast.makeText(AskAiActivity.this, "Failed to load chat history: " + error, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
    private void sendMessage(String messageContent) {
        // Show user's message immediately
        displayMessage(messageContent, true, getCurrentTimestamp());

        // Send to AI
        askAiApiService.sendMessageToAi(messageContent, new AskAiApiService.SendMessageCallback() {
            @Override
            public void onSuccess(String response) {
                // Show AI's response as a received message
                runOnUiThread(() -> displayMessage(response, false, getCurrentTimestamp()));
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() ->
                        Toast.makeText(AskAiActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

//    private void sendMessage(String messageContent) {
//        // Show user's message
//        displayMessage(messageContent, true, getCurrentTimestamp());
//
//        // Send to AI
//        askAiApiService.sendMessageToAi(messageContent, new AskAiApiService.SendMessageCallback() {
//            @Override
//            public void onSuccess(String response) {
//                // The response is a plain string now, display it as received
//                runOnUiThread(() -> displayMessage(response, false, getCurrentTimestamp()));
//            }
//
//            @Override
//            public void onError(String error) {
//                runOnUiThread(() -> Toast.makeText(AskAiActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show());
//            }
//        });
//    }



    public void displayMessage(String message, boolean isSent, String timestamp) {
        ChatMessage chatMessage = new ChatMessage(message, isSent, timestamp);
        chatAdapter.addMessage(chatMessage);
        recyclerViewMessages.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
    }

    private String getCurrentTimestamp() {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(AskAiActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
