package com.coms309.isu_pulse_frontend.chat_system;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;
import com.coms309.isu_pulse_frontend.web_socket.GroupChatServiceWebSocket;

import java.util.ArrayList;
import java.util.List;

public class GroupChatActivity extends AppCompatActivity implements GroupChatServiceWebSocket.ChatServiceListener {

    private RecyclerView recyclerViewMessages;
    private GroupChatAdapter groupChatAdapter;
    private List<ChatMessage> chatMessages = new ArrayList<>();
    private EditText editTextMessage;
    private Button buttonSend;
    private ImageButton buttonBack;
    private TextView textViewGroupName;
    private ImageButton buttonAdd;

    private GroupChatServiceWebSocket webSocketService;
    private String currentUserNetId;
    private Long groupId;
    private String groupName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        // Initialize UI components
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        buttonBack = findViewById(R.id.buttonBack);
        textViewGroupName = findViewById(R.id.textViewGroupName);
        buttonAdd = findViewById(R.id.buttonAdd);

        // Setup RecyclerView
        groupChatAdapter = new GroupChatAdapter(chatMessages);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(groupChatAdapter);

        // Retrieve group info from intent
        groupId = getIntent().getLongExtra("groupId", -1);
        groupName = getIntent().getStringExtra("groupName");
        currentUserNetId = UserSession.getInstance().getNetId();

        textViewGroupName.setText(groupName);

        // Initialize WebSocket
        connectToWebSocket();

        // Handle send button
        buttonSend.setOnClickListener(v -> {
            String message = editTextMessage.getText().toString();
            if (!TextUtils.isEmpty(message)) {
                webSocketService.sendMessage(currentUserNetId, groupId, message);
                editTextMessage.setText("");
            }
        });

        // Handle back button
        buttonBack.setOnClickListener(view -> {
            Intent intent = new Intent(GroupChatActivity.this, ChatList.class);
            startActivity(intent);
        });

        buttonAdd.setOnClickListener(view -> {
            Intent intent = new Intent(GroupChatActivity.this, GroupChatAddingMember.class);
            intent.putExtra("groupId", groupId);
            startActivity(intent);
        });
    }

    private void connectToWebSocket() {
        Log.d("GroupChatActivity", "Connecting to WebSocket for groupId: " + groupId);
        webSocketService = GroupChatServiceWebSocket.getInstance(this, currentUserNetId, groupId, this);
    }

//    @Override
//    public void onMessageReceived(String senderNetId, Long groupId, String content, String timestamp) {
//        // Add new message to the list and refresh RecyclerView
//        ChatMessage chatMessage = new ChatMessage(senderNetId, groupId, content, timestamp);
//        chatMessages.add(chatMessage);
//        runOnUiThread(() -> {
//            groupChatAdapter.notifyItemInserted(chatMessages.size() - 1);
//            recyclerViewMessages.smoothScrollToPosition(chatMessages.size() - 1); // Scroll to the latest message
//        });
//    }
    @Override
    public void onMessageReceived(String senderNetId, Long receivedGroupId, String content, String timestamp) {
        // Check if the received message belongs to the group the user is currently viewing
        if (receivedGroupId != null && receivedGroupId.equals(this.groupId)) {
            // This message is for the currently displayed group

            ChatMessage chatMessage = new ChatMessage(senderNetId, receivedGroupId, content, timestamp);
            chatMessages.add(chatMessage);

            // Update the UI on the main thread
            runOnUiThread(() -> {
                groupChatAdapter.notifyItemInserted(chatMessages.size() - 1);
                recyclerViewMessages.smoothScrollToPosition(chatMessages.size() - 1); // Scroll to the latest message
            });
        } else {
            // The message is not for the current group. You can ignore it or handle it differently.
            // For example:
            Log.d("GroupChatActivity", "Received a message for groupId: " + receivedGroupId + " but currently viewing groupId: " + this.groupId);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketService != null) {
            webSocketService.close();
            webSocketService = null;
        }
    }
}
