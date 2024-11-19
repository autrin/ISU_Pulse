package com.coms309.isu_pulse_frontend.friend_functional;

/**
 * Class showes all friend list of a user
 *
 * @author ntbach
 */
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

/**
 * The FriendList activity displays a list of friends.
 */
public class FriendList extends AppCompatActivity {
    /**
     * Back button to navigate to the ProfileActivity.
     */
    private ImageView backButton;

    /**
     * Search bar to filter friends by name.
     */
    private EditText searchBar;

    /**
     * Button to initiate a search for friends.
     */
    private Button searchButton;

    /**
     * Spinner for sorting friends.
     */
    private Spinner spinner;

    /**
     * RecyclerView to display the list of friends.
     */
    private RecyclerView friendsRecyclerView;

    /**
     * Adapter to bind the friend data to the RecyclerView.
     */
    private FriendAdapter friendAdapter;

    /**
     * List to hold the friend objects.
     */
    private List<Friend> friendList;

    /**
     * List to hold the filtered friend objects.
     */
    private List<Friend> filteredFriendList;

    /**
     * Initializes the activity, sets up UI components, and fetches friend data.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
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

    /**
     * Fetches the list of friends using the FriendService.
     */
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

    /**
     * Filters the list of friends based on the given query.
     * @param query The search query.
     */
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

    /**
     * Resets the list of friends to the original state.
     */
    private void resetFriendList() {
        filteredFriendList.clear();
        filteredFriendList.addAll(friendList); // Show all friends
        friendAdapter.notifyDataSetChanged();
    }

    /**
     * Sorts the list of friends based on the selected option.
     * @param sortOption
     */
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