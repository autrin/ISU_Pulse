package com.coms309.isu_pulse_frontend.chat_system;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;
import com.coms309.isu_pulse_frontend.web_socket.GroupChatServiceWebSocket;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GroupChatActivity extends AppCompatActivity implements GroupChatServiceWebSocket.GroupChatServiceListener {

    private static final String TAG = "GroupChatActivity";

    private ImageButton backButton, addButton, attachButton;
    private Button sendButton;
    private EditText messageEditText;
    private TextView groupNameTextView, typingIndicatorTextView;
    private RecyclerView recyclerViewMessages;
    private GroupChatServiceWebSocket groupChatServiceWebSocket;
    private GroupChatAdapter groupChatAdapter;

    private String netId;
    private Long groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        // Get group details from intent
        groupId = getIntent().getLongExtra("groupId", -1);
        String groupName = getIntent().getStringExtra("groupName");
        netId = UserSession.getInstance().getNetId();

        if (groupId == -1 || groupName == null) {
            Toast.makeText(this, "Invalid group details", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize UI components
        backButton = findViewById(R.id.buttonBack);
        addButton = findViewById(R.id.buttonAdd);
        attachButton = findViewById(R.id.buttonAttach);
        sendButton = findViewById(R.id.buttonSend);
        messageEditText = findViewById(R.id.editTextMessage);
        groupNameTextView = findViewById(R.id.textViewUsername);
        typingIndicatorTextView = findViewById(R.id.textViewTypingIndicator);
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);

        // Set group name
        groupNameTextView.setText(groupName);

        // Setup RecyclerView
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        groupChatAdapter = new GroupChatAdapter(new ArrayList<>());
        recyclerViewMessages.setAdapter(groupChatAdapter);

        // Setup WebSocket
        groupChatServiceWebSocket = GroupChatServiceWebSocket.getInstance(this, netId, groupId, this);
        groupChatServiceWebSocket.setWebSocketListener(this);

        // Button click listeners
        backButton.setOnClickListener(v -> finish());
        addButton.setOnClickListener(v -> openAddMemberScreen());
        sendButton.setOnClickListener(v -> sendMessage());

        // Fetch chat history
        fetchChatHistory();
    }

    private void openAddMemberScreen() {
        // Navigate to Add Member Screen
        Intent intent = new Intent(this, GroupChatAddingMember.class);
        intent.putExtra("groupId", groupId);
        startActivity(intent);
    }

    private void fetchChatHistory() {

    }

    private void sendMessage() {
        String message = messageEditText.getText().toString().trim();
        if (message.isEmpty()) {
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Display message locally
        displayMessage("You", message, LocalDateTime.now().toString());

        // Send message via WebSocket
        groupChatServiceWebSocket.sendMessage(netId, message);

        // Clear input field
        messageEditText.setText("");
    }

    private void displayMessage(String sender, String content, String timestamp) {
        groupChatAdapter.addMessage(new GroupChatMessage(sender, content, timestamp, sender.equals("You"), false));
        recyclerViewMessages.smoothScrollToPosition(groupChatAdapter.getItemCount() - 1);
    }

    @Override
    public void onMessageReceived(String senderNetId, Long groupId, String content, String timestamp) {
        Log.d(TAG, "Message received: " + content);
        runOnUiThread(() -> {
            String sender = senderNetId.equals(netId) ? "You" : senderNetId;
            displayMessage(sender, content, timestamp);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (groupChatServiceWebSocket != null) {
            groupChatServiceWebSocket.close();
        }
    }
}
