package com.coms309.isu_pulse_frontend.friend_functional;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.api.FriendService;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;
import com.coms309.isu_pulse_frontend.profile_activity.ProfileActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;

public class FriendRequest extends AppCompatActivity {

    private ImageView backButton;
    private EditText searchBar;
    private Button searchButton;
    private Spinner spinner;
    private RecyclerView friendsRecyclerView;
    private FriendRequestAdapter friendAdapter;
    private List<Friend> friendRequestList;
    private List<Friend> filteredFriendRequestList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_request);

        backButton = findViewById(R.id.back_button_);
        searchButton = findViewById(R.id.search_button);
        searchBar = findViewById(R.id.search_bar);
        spinner = findViewById(R.id.sort_spinner);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendRequest.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        friendsRecyclerView = findViewById(R.id.friends_list_request);
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        friendRequestList = new ArrayList<>();
        filteredFriendRequestList = new ArrayList<>();
        friendAdapter = new FriendRequestAdapter(this, filteredFriendRequestList);
        friendsRecyclerView.setAdapter(friendAdapter);

        fetchFriendRequests();

        // Set up the sort spinner with options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_alphabetically_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Set listener for sort option selection
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortFriendRequests(position);
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
                    filterFriendRequests(query);
                } else {
                    resetFriendRequestList();
                }
            }
        });
    }

    private void fetchFriendRequests() {
        String netId = UserSession.getInstance().getNetId();
        FriendService friendService = new FriendService(this);

        friendService.getReceivedRequests(netId, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                friendRequestList.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject friendObject = response.getJSONObject(i);
                        String firstName = friendObject.getString("firstName");
                        String lastName = friendObject.getString("lastName");
                        String netId = friendObject.getString("netId");
                        friendRequestList.add(new Friend(firstName, lastName, netId));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                resetFriendRequestList();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FriendRequest.this, "Failed to fetch friend requests", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterFriendRequests(String query) {
        filteredFriendRequestList.clear();
        for (Friend friend : friendRequestList) {
            String fullName = friend.getFirstName() + " " + friend.getLastName();
            if (fullName.toLowerCase().contains(query.toLowerCase())) {
                filteredFriendRequestList.add(friend);
            }
        }
        friendAdapter.notifyDataSetChanged();
    }

    private void resetFriendRequestList() {
        filteredFriendRequestList.clear();
        filteredFriendRequestList.addAll(friendRequestList); // Show all friend requests
        friendAdapter.notifyDataSetChanged();
    }

    private void sortFriendRequests(int sortOption) {
        if (sortOption == 0) {
            // Sort A-Z
            filteredFriendRequestList.sort((f1, f2) -> f1.getFirstName().compareToIgnoreCase(f2.getFirstName()));
        } else if (sortOption == 1) {
            // Sort Z-A
            filteredFriendRequestList.sort((f1, f2) -> f2.getFirstName().compareToIgnoreCase(f1.getFirstName()));
        }
        friendAdapter.notifyDataSetChanged();
    }
}
