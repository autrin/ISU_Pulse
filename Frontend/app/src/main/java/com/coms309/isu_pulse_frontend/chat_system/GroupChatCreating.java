package com.coms309.isu_pulse_frontend.chat_system;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.api.FriendService;
import com.coms309.isu_pulse_frontend.api.GroupChatApiService;
import com.coms309.isu_pulse_frontend.friend_functional.Friend;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GroupChatCreating extends AppCompatActivity {
    private TextView cancel;
    private TextView create;
    private EditText groupName;
    private RecyclerView groupChatAddingMemberRecyclerView;
    private List<Friend> friendList;
    private List<Friend> filteredFriendList;
    private GroupAddFriendAdapter friendAdapter;
    private Spinner spinner;
    private Button searchButton;
    private EditText searchBar;
    private GroupChatApiService groupChatApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_adding_screen);

        cancel = findViewById(R.id.btn_cancel);
        create = findViewById(R.id.btn_create);
        searchBar = findViewById(R.id.search_bar);
        searchButton = findViewById(R.id.search_button);
        groupName = findViewById(R.id.group_name);
        groupChatAddingMemberRecyclerView = findViewById(R.id.friend_list);
        groupChatAddingMemberRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        spinner = findViewById(R.id.sort_spinner);
        friendList = new ArrayList<>();
        filteredFriendList = new ArrayList<>();
        groupChatApiService = new GroupChatApiService(this);

        friendAdapter = new GroupAddFriendAdapter(this, filteredFriendList, selectedCount -> {
            if (selectedCount >= 2) {
                create.setTextColor(Color.BLUE);  // Change to blue if 2 or more are selected
            } else {
                create.setTextColor(Color.GRAY);  // Change back to gray otherwise
            }
        });

        groupChatAddingMemberRecyclerView.setAdapter(friendAdapter);

        cancel.setOnClickListener(v -> {
            Intent intent = new Intent(GroupChatCreating.this, ChatList.class);
            startActivity(intent);
        });

        create.setOnClickListener(v -> {
            if (filteredFriendList.stream().filter(Friend::isSelected).count() >= 2) {
                createGroup();
            } else {
                Toast.makeText(GroupChatCreating.this, "Select at least 2 friends to create a group", Toast.LENGTH_SHORT).show();
            }
        });

        fetchFriends();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_alphabetically_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortFriends(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        searchButton.setOnClickListener(v -> {
            String query = searchBar.getText().toString().trim();
            if (!query.isEmpty()) {
                filterFriends(query);
            } else {
                resetFriendList();
            }
        });
    }

    private void createGroup() {
        String groupNameText = groupName.getText().toString().trim();
        String netId = UserSession.getInstance().getNetId();

        // If group name is empty, set it to concatenated first names of selected friends
        if (groupNameText.isEmpty()) {
            List<Friend> selectedFriends = filteredFriendList.stream()
                    .filter(Friend::isSelected)
                    .collect(Collectors.toList());

            groupNameText = selectedFriends.stream()
                    .map(Friend::getFirstName)
                    .collect(Collectors.joining(", "));

            if (selectedFriends.size() > 3) {
                groupNameText = selectedFriends.stream()
                        .limit(3)
                        .map(Friend::getFirstName)
                        .collect(Collectors.joining(", ")) + "...";
            }
        }

        // Create the group
        groupChatApiService.createGroupChat(groupNameText, netId, new GroupChatApiService.GroupChatCallback() {
            @Override
            public void onSuccess(String response) {
                // Group created successfully; proceed to add members
                addInitialMembers();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(GroupChatCreating.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addInitialMembers() {
        String creatorNetId = UserSession.getInstance().getNetId();
        List<Friend> selectedFriends = filteredFriendList.stream()
                .filter(Friend::isSelected)
                .collect(Collectors.toList());

        for (Friend friend : selectedFriends) {
            groupChatApiService.addInitialMembers(creatorNetId, friend.getNetId(), new GroupChatApiService.GroupChatCallback() {
                @Override
                public void onSuccess(String response) {
                    Log.d("GroupChatCreating", "Successfully added member: " + friend.getNetId());
                }

                @Override
                public void onError(String error) {
                    Log.e("GroupChatCreating", "Failed to add member: " + friend.getNetId() + " - " + error);
                }
            });
        }

        // After adding members, navigate back to the chat list
        Toast.makeText(GroupChatCreating.this, "Group created successfully!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(GroupChatCreating.this, ChatList.class);
        startActivity(intent);
    }

    private void fetchFriends() {
        String netId = UserSession.getInstance().getNetId();
        FriendService friendService = new FriendService(this);

        friendService.getFriendList(netId, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                friendList.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject friendObject = response.getJSONObject(i);
                        String firstName = friendObject.getString("firstName");
                        String lastName = friendObject.getString("lastName");
                        String netId = friendObject.getString("netId");
                        friendList.add(new Friend(firstName, lastName, netId));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                resetFriendList();
            }
        }, error -> Toast.makeText(GroupChatCreating.this, "Failed to fetch friends", Toast.LENGTH_SHORT).show());
    }

    private void filterFriends(String query) {
        filteredFriendList.clear();
        for (Friend friend : friendList) {
            String fullName = friend.getFirstName() + " " + friend.getLastName();
            if (fullName.toLowerCase().contains(query.toLowerCase())) {
                filteredFriendList.add(friend);
            }
        }
        friendAdapter.notifyDataSetChanged();
    }

    private void resetFriendList() {
        filteredFriendList.clear();
        filteredFriendList.addAll(friendList);
        friendAdapter.notifyDataSetChanged();
    }

    private void sortFriends(int sortOption) {
        if (sortOption == 0) {
            filteredFriendList.sort((f1, f2) -> f1.getFirstName().compareToIgnoreCase(f2.getFirstName()));
        } else if (sortOption == 1) {
            filteredFriendList.sort((f1, f2) -> f2.getFirstName().compareToIgnoreCase(f1.getFirstName()));
        }
        friendAdapter.notifyDataSetChanged();
    }
}
