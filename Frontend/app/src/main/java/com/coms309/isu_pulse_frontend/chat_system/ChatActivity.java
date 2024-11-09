package com.coms309.isu_pulse_frontend.chat_system;

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
import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.api.ChatApiService;
import com.coms309.isu_pulse_frontend.api.UpdateAccount;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;
import com.coms309.isu_pulse_frontend.model.Profile;
import com.coms309.isu_pulse_frontend.web_socket.ChatServiceWebSocket;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity implements ChatServiceWebSocket.ChatServiceListener {

    private ImageButton backButton;
    private ChatApiService chatApiService;
    private ImageView profileImageView;
    private ImageButton attachButton;
    private Button sendButton;
    private EditText messageEditText;
    private RecyclerView recyclerViewMessages;
    private TextView nameTextView;
    private TextView typingIndicatorTextView;
    private ChatAdapter chatAdapter;
    private ChatAdapter chatAdapterfetchHistory;
    private ChatServiceWebSocket chatServiceWebSocket;
    private String netId1;
    private String netId2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        netId1 = getIntent().getStringExtra("netId");
        netId2 = UserSession.getInstance().getNetId();
        backButton = findViewById(R.id.buttonBack);
        profileImageView = findViewById(R.id.imageViewLogo);
        nameTextView = findViewById(R.id.textViewUsername);
        messageEditText = findViewById(R.id.editTextMessage);
        sendButton = findViewById(R.id.buttonSend);
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(new ArrayList<>());
        chatAdapterfetchHistory = new ChatAdapter(new ArrayList<>());
        recyclerViewMessages.setAdapter(chatAdapter);
        chatServiceWebSocket = ChatServiceWebSocket.getInstance(this, netId2, netId1, this);
        chatServiceWebSocket.setWebSocketListener(this);
        chatApiService = new ChatApiService(this);



        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ChatActivity.this, ChatList.class);
            startActivity(intent);
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString();
                if (!message.isEmpty()) {
                    sendMessage(message);
//                    chatAdapter.addMessage(new ChatMessage(message, true, LocalDateTime.now().toString()));
//                    displayMessage(message, true, new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
                    messageEditText.setText("");
                }
            }
        });

        fetchProfileData();
        fetchChatHistory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchProfileData();
//        fetchChatHistory();
    }


    private void fetchChatHistory() {
        chatApiService.getChatHistory(netId1, netId2, new ChatApiService.ChatHistoryCallback() {
            @Override
            public void onSuccess(List<ChatMessageDTO> chatHistory) {
//                chatAdapter.clearMessages();
                for (ChatMessageDTO message : chatHistory) {
                    displayMessage(message.getContent(), message.getSenderNetId().equals(netId2), message.getTimestamp());
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ChatActivity.this, "Error fetching chat history: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void updateUI(Profile profile) {
        if (profile != null) {
            String imageUrl = profile.getProfilePictureUrl();
            Glide.with(this)
                    .load(imageUrl)
                    .into(profileImageView);
            nameTextView.setText(profile.getFirstName() + " " + profile.getLastName());
        }
        else {
            Log.e("FriendProfile", "Profile data is null");
        }
    }


    public void fetchProfileData() {
        UpdateAccount.fetchProfileData(netId1, this, new UpdateAccount.ProfileCallback() {
            @Override
            public void onSuccess(Profile profile) {
                updateUI(profile);
            }

            @Override
            public void onError(VolleyError error) {
                // Handle error
                error.printStackTrace();
            }
        });
    }

    private void sendMessage(String messageContent) {
        // Display the message locally
        displayMessage(messageContent, true, LocalDateTime.now().toString());

        // Send message via WebSocket
        chatServiceWebSocket.sendMessage(netId2, netId1, messageContent);
    }

    @Override
    public void onMessageReceived(String senderNetId, String recipientNetId, String message, String timestamp) {
//        Log.d(TAG, "Message received in ChatActivity: " + message);
//        displayMessage(message, false, timestamp);
//        boolean isSent = senderNetId.equals(netId2);
//        ChatMessage chatMessage = new ChatMessage(message, isSent, timestamp);
//        chatAdapter.addMessage(chatMessage);
        runOnUiThread(() -> {
            // Avoid adding the message twice by checking if it's from the current user
            if (!senderNetId.equals(UserSession.getInstance().getNetId())) {
                displayMessage(message, false, timestamp);
            }
        });
    }



    private void displayMessage(String message, boolean isSent, String timestamp) {
        chatAdapter.addMessage(new ChatMessage(message, isSent, timestamp));
        recyclerViewMessages.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close WebSocket connection if needed
        if (chatServiceWebSocket != null) {
            chatServiceWebSocket.close();
            chatServiceWebSocket = null;
        }
    }
}
