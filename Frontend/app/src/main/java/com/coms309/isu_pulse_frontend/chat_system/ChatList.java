package com.coms309.isu_pulse_frontend.chat_system;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.api.ChatApiService;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;
import com.coms309.isu_pulse_frontend.ui.home.HomeActivity;

import java.util.ArrayList;
import java.util.List;

public class ChatList extends AppCompatActivity {

    private ImageView backButton;
    private Button searchButton;
    private EditText searchBar;
    private ImageButton createButton;
    private RecyclerView chatsRecyclerView;
    private ChatViewAdapter chatViewAdapter;
    private List<ChatMessage> chatList;
    private List<ChatMessage> allChats; // To store the complete list of chats

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_screen);

        backButton = findViewById(R.id.back_button);
        searchButton = findViewById(R.id.search_button);
        searchBar = findViewById(R.id.search_bar);
        createButton = findViewById(R.id.add_button);

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ChatList.this, HomeActivity.class);
            startActivity(intent);
        });

        createButton.setOnClickListener(v -> {
            Intent intent = new Intent(ChatList.this, GroupChatCreating.class);
            startActivity(intent);
        });

        chatsRecyclerView = findViewById(R.id.recyclerView);
        chatsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        chatList = new ArrayList<>();
        allChats = new ArrayList<>();
        chatViewAdapter = new ChatViewAdapter(chatList);
        chatsRecyclerView.setAdapter(chatViewAdapter);

        fetchChats();

        // Implement search functionality
        searchButton.setOnClickListener(v -> {
            String query = searchBar.getText().toString().trim();
            searchChats(query);
        });
    }

    private void fetchChats() {
        String netId = UserSession.getInstance().getNetId();
        ChatApiService chatApiService = new ChatApiService(this);

        chatApiService.getLatestMessage(netId, new ChatApiService.ChatLatestCallback() {
            @Override
            public void onSuccess(List<ChatMessage> chatHistory) {
                chatList.clear();
                chatList.addAll(chatHistory);
                allChats.clear();
                allChats.addAll(chatHistory); // Store full list for search
                chatViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ChatList.this, "Failed to fetch chats", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchChats(String query) {
        if (query.isEmpty()) {
            chatList.clear();
            chatList.addAll(allChats); // Show all chats if search query is empty
        } else {
            List<ChatMessage> filteredList = new ArrayList<>();
            for (ChatMessage chatMessage : allChats) {
                // Filter based on sender, recipient, or group name
                if ((chatMessage.getSenderFullName() != null && chatMessage.getSenderFullName().toLowerCase().contains(query.toLowerCase())) ||
                        (chatMessage.getRecipientFullName() != null && chatMessage.getRecipientFullName().toLowerCase().contains(query.toLowerCase())) ||
                        (chatMessage.getGroupName() != null && chatMessage.getGroupName().toLowerCase().contains(query.toLowerCase()))) {
                    filteredList.add(chatMessage);
                }
            }
            chatList.clear();
            chatList.addAll(filteredList);
        }
        chatViewAdapter.notifyDataSetChanged();
    }
}
