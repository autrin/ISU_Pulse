package com.coms309.isu_pulse_frontend.friend_functional;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.api.FriendService;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;
import com.coms309.isu_pulse_frontend.profile_activity.ProfileActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


public class FriendList extends AppCompatActivity {

    private ImageView backButton;
    private EditText searchBar;
    private Button searchButton;
    private Spinner spinner;
    private RecyclerView friendsRecyclerView;
    private FriendAdapter friendAdapter;
    private List<Friend> friendList;
    private List<Friend> filteredFriendList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_list);

        backButton = findViewById(R.id.back_button_);
        searchButton = findViewById(R.id.search_button);
        searchBar = findViewById(R.id.search_bar);
        spinner = findViewById(R.id.sort_spinner);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendList.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        friendsRecyclerView = findViewById(R.id.friends_list);
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        friendList = new ArrayList<>();
        filteredFriendList = new ArrayList<>();
        friendAdapter = new FriendAdapter(this, filteredFriendList);
        friendsRecyclerView.setAdapter(friendAdapter);

        fetchFriends();

        // Set up the sort spinner with options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_alphabetically_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Set listener for sort option selection
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortFriends(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchBar.getText().toString().trim();
                if (!query.isEmpty()) {
                    filterFriends(query);
                } else {
                    resetFriendList();
                }
            }
        });
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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FriendList.this, "Failed to fetch friends", Toast.LENGTH_SHORT).show();
            }
        });
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
        filteredFriendList.addAll(friendList); // Show all friends
        friendAdapter.notifyDataSetChanged();
    }

    private void sortFriends(int sortOption) {
        if (sortOption == 0) {
            // Sort A-Z
            filteredFriendList.sort((f1, f2) -> f1.getFirstName().compareToIgnoreCase(f2.getFirstName()));
        } else if (sortOption == 1) {
            // Sort Z-A
            filteredFriendList.sort((f1, f2) -> f2.getFirstName().compareToIgnoreCase(f1.getFirstName()));
        }
        friendAdapter.notifyDataSetChanged();
    }
}