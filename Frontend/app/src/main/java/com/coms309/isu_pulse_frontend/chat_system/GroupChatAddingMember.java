package com.coms309.isu_pulse_frontend.chat_system;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class GroupChatAddingMember extends AppCompatActivity {
    private TextView cancel;
    private TextView create;
    private TextView title;
    private EditText searchBar;
    private EditText groupName;
    private Spinner sortSpinner;
    private RecyclerView groupChatAddingMemberRecyclerView;
    private List<Friend> friendList;
    private List<Friend> filteredFriendList;
    private GroupAddFriendAdapter friendAdapter;
    private GroupChatApiService groupChatApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_adding_screen);

        // Initialize UI components
        cancel = findViewById(R.id.btn_cancel);
        create = findViewById(R.id.btn_create);
        title = findViewById(R.id.new_group);
        searchBar = findViewById(R.id.search_bar);
        groupName = findViewById(R.id.group_name);
        sortSpinner = findViewById(R.id.sort_spinner);
        groupChatAddingMemberRecyclerView = findViewById(R.id.friend_list);
        create.setText("Save");
        create.setTextColor(Color.BLUE);
        groupChatApiService = new GroupChatApiService(this);

        friendList = new ArrayList<>();
        filteredFriendList = new ArrayList<>();
        friendAdapter = new GroupAddFriendAdapter(this, filteredFriendList, selectedCount -> {
            // No dynamic color changes needed
        });
        groupChatAddingMemberRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        groupChatAddingMemberRecyclerView.setAdapter(friendAdapter);
        title.setText("Add Members");

        // Cancel button functionality
        cancel.setOnClickListener(v -> {
            Intent intent = new Intent(GroupChatAddingMember.this, ChatList.class);
            startActivity(intent);
        });

        // Create button functionality
        create.setOnClickListener(v -> {
            String groupNameText = groupName.getText().toString().trim();
            List<Friend> selectedFriends = filteredFriendList.stream()
                    .filter(Friend::isSelected)
                    .collect(Collectors.toList());

            if (groupNameText.isEmpty() && selectedFriends.isEmpty()) {
                Toast.makeText(this, "Please enter a group name or select at least one friend.", Toast.LENGTH_SHORT).show();
            } else {
                if (!groupNameText.isEmpty()) {
                    saveGroup();
                }
                if (!selectedFriends.isEmpty()) {
                    addMembersToGroup(selectedFriends);
                }
            }
            Intent intent = new Intent(GroupChatAddingMember.this, ChatList.class);
            startActivity(intent);
        });

        setupSpinner();
        setupSearchBar();
        fetchFriendsNotInGroup();
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_alphabetically_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortFriends(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void setupSearchBar() {
        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            String query = searchBar.getText().toString().trim();
            if (!query.isEmpty()) {
                filterFriends(query);
            } else {
                resetFriendList();
            }
            return true;
        });
    }

    private void fetchFriendsNotInGroup() {
        String netId = UserSession.getInstance().getNetId();
        Long groupId = getIntent().getLongExtra("groupId", -1);

        FriendService friendService = new FriendService(this);
        friendService.fetchFriendChats(netId, groupId, new Response.Listener<JSONArray>() {
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
        }, error -> Toast.makeText(GroupChatAddingMember.this, "Failed to fetch friends", Toast.LENGTH_SHORT).show());
    }

    private void saveGroup() {
        String groupNameText = groupName.getText().toString().trim();
        Long groupId = getIntent().getLongExtra("groupId", -1);

        groupChatApiService.modifyGroupChat(groupId, groupNameText, new GroupChatApiService.GroupChatCallback() {
            @Override
            public void onSuccess(String response) {
                Toast.makeText(GroupChatAddingMember.this, "Group name modified successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(GroupChatAddingMember.this, ChatList.class);
                startActivity(intent);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(GroupChatAddingMember.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMembersToGroup(List<Friend> selectedFriends) {
        String adderNetId = UserSession.getInstance().getNetId();
        Long groupId = getIntent().getLongExtra("groupId", -1);

        for (Friend friend : selectedFriends) {
            groupChatApiService.addMemberToGroup(adderNetId, friend.getNetId(), groupId, new GroupChatApiService.GroupChatCallback() {
                @Override
                public void onSuccess(String response) {
                    Toast.makeText(GroupChatAddingMember.this, "Member added successfully!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(GroupChatAddingMember.this, "Failed to add member: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void resetFriendList() {
        filteredFriendList.clear();
        filteredFriendList.addAll(friendList);
        friendAdapter.notifyDataSetChanged();
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

    private void sortFriends(int sortOption) {
        if (sortOption == 0) {
            filteredFriendList.sort((f1, f2) -> f1.getFirstName().compareToIgnoreCase(f2.getFirstName()));
        } else if (sortOption == 1) {
            filteredFriendList.sort((f1, f2) -> f2.getFirstName().compareToIgnoreCase(f1.getFirstName()));
        }
        friendAdapter.notifyDataSetChanged();
    }
}
