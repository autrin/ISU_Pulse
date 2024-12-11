package com.coms309.isu_pulse_frontend.ui.ask_ai;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.NoCopySpan;
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

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.coms309.isu_pulse_frontend.MainActivity;
import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.api.AskAiApiService;
import com.coms309.isu_pulse_frontend.api.ChatApiService;
import com.coms309.isu_pulse_frontend.api.UpdateAccount;
import com.coms309.isu_pulse_frontend.chat_system.ChatActivity;
import com.coms309.isu_pulse_frontend.adapters.AskAiAdapter;
import com.coms309.isu_pulse_frontend.chat_system.ChatAdapter;
import com.coms309.isu_pulse_frontend.chat_system.ChatList;
import com.coms309.isu_pulse_frontend.chat_system.ChatMessage;
import com.coms309.isu_pulse_frontend.chat_system.ChatMessageDTO;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;
import com.coms309.isu_pulse_frontend.model.Profile;
import com.coms309.isu_pulse_frontend.ui.home.HomeActivity;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.os.Handler;
import android.window.OnBackInvokedDispatcher;

public class AskAiActivity extends AppCompatActivity {

    private ImageButton backButton;
    private ChatApiService chatApiService;
    private ImageView profileImageView;
    private ImageButton attachButton;
    private Button sendButton;
    private EditText messageEditText;
    private RecyclerView recyclerViewMessages;
    private TextView nameTextView;
    private TextView typingIndicatorTextView;
    private ChatAdapter askAiAdapter;
    private AskAiApiService askAiApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ask_ai);

        // Initialize UI components
        backButton = findViewById(R.id.buttonBack);
        profileImageView = findViewById(R.id.imageViewLogo);
        nameTextView = findViewById(R.id.textViewUsername);
        messageEditText = findViewById(R.id.editTextMessage);
        sendButton = findViewById(R.id.buttonSend);
        recyclerViewMessages = findViewById(R.id.recyclerAskAiViewMessages);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        askAiAdapter = new ChatAdapter(new ArrayList<>());
        recyclerViewMessages.setAdapter(askAiAdapter);
        askAiApiService = new AskAiApiService(this);
        // Set ChatGPT-specific UI
//        Glide.with(this).load(R.drawable.chatgpt_100).into(profileImageView);
        nameTextView.setText("ChatGPT");

        // Handle the Intent and initialize the session
//        String chatId = getIntent().getStringExtra("chatId");
//        if (chatId != null) {
//            Log.d("AskAiActivity", "Chat session started for chatId: " + chatId);
//        }

        // Handle back button navigation
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(AskAiActivity.this, MainActivity.class); // might be MainActivity or HomeActivity
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Avoid multiple instances
            startActivity(intent);
//            finish(); // Finish current activity
        });


        // Handle send button
        sendButton.setOnClickListener(v -> {
            String message = messageEditText.getText().toString();
            if (!message.isEmpty()) {
                sendMessage(message);
                messageEditText.setText(""); // Clear the input field
            }
        });

        // Display a welcome message from ChatGPT
        displayMessage("Hi! I'm ChatGPT. How can I assist you today?", false, getCurrentTimestamp());
    }

    // Method to handle sending a message
    private void sendMessage(String messageContent) {
        // Display the user's message
        displayMessage(messageContent, true, getCurrentTimestamp());

        // Simulate ChatGPT's response
        simulateAITyping();
        new Handler().postDelayed(() -> {
            String aiResponse = generateAIResponse(messageContent); // Replace with actual API call later
            displayMessage(aiResponse, false, getCurrentTimestamp());
        }, 2000); // Simulate a 2-second response delay // TODO: Change to actual API call
    }

    // Simulates ChatGPT's typing indicator
    private void simulateAITyping() {
        TextView typingIndicator = findViewById(R.id.textViewTypingIndicator);
        typingIndicator.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> typingIndicator.setVisibility(View.GONE), 2000); // TODO Change to actual typing indicator
    }

    private void displayMessage(String message, boolean isSent, String timestamp) {
        ChatMessage chatMessage = new ChatMessage(message, isSent, timestamp);
        askAiAdapter.addMessage(chatMessage); // Add the message to the adapter
        recyclerViewMessages.smoothScrollToPosition(askAiAdapter.getItemCount() - 1);
    }

    // Mock AI response generator (to be replaced by API call)
    private String generateAIResponse(String userMessage) {
        //TODO
        // Example static response for testing
        return "You asked: \"" + userMessage + "\". Here's what I think...";
    }

    // Utility method to get the current timestamp
    private String getCurrentTimestamp() {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
    }
    // Helper method to navigate back to history
//    private void navigateToHistory() {
//        Intent intent = new Intent(AskAiActivity.this, AskAiAllHistoryActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        finish();
//    }
}
