package com.coms309.isu_pulse_frontend.friend_functional;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.api.CourseService;
import com.coms309.isu_pulse_frontend.api.FriendService;
import com.coms309.isu_pulse_frontend.api.UpdateAccount;
import com.coms309.isu_pulse_frontend.chat_system.ChatActivity;
import com.coms309.isu_pulse_frontend.model.Profile;
import com.coms309.isu_pulse_frontend.profile_activity.ProfileActivity;
import com.coms309.isu_pulse_frontend.schedule.Schedule;

import org.json.JSONArray;

import java.util.List;

public class FriendProfile extends AppCompatActivity {

    private ImageButton backButton;
    private Button addFriendButton;
    private ImageView profileImage;
    private Button pingFriendButton;
    private TextView numcoursesTextView;
    private TextView coursesTextView;
    private TextView friendsTextView;
    private TextView numfriendsTextView;
    private TextView firstNameTextView;
    private TextView lastNameTextView;
    private TextView usernameTextView;
    private TextView linkedinUrlTextView;
    private TextView externalUrlTextView;
    private TextView descriptionTextView;
    private CourseService courseService;
    private FriendService friendService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_profile);

        // Initialize all views using findViewById
        backButton = findViewById(R.id.backButton);
        addFriendButton = findViewById(R.id.addFriendButton);
        pingFriendButton = findViewById(R.id.pingFriendButton);
        numcoursesTextView = findViewById(R.id.coursesCountTextView);
        coursesTextView = findViewById(R.id.coursesLabelTextView);
        friendsTextView = findViewById(R.id.friendsLabelTextView);
        numfriendsTextView = findViewById(R.id.friendsCountTextView);
        profileImage = findViewById(R.id.profileImage);

        // Initialize TextViews for first name, last name, username, etc.
        firstNameTextView = findViewById(R.id.firstNameTextView);
        lastNameTextView = findViewById(R.id.lastNameTextView);
        usernameTextView = findViewById(R.id.usernameTextView);
        linkedinUrlTextView = findViewById(R.id.linkedinUrlTextView);
        externalUrlTextView = findViewById(R.id.externalUrlTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(FriendProfile.this, ProfileActivity.class);
            startActivity(intent);
        });

        pingFriendButton.setOnClickListener(v -> {
            Intent intent = new Intent(FriendProfile.this, ChatActivity.class);
            intent.putExtra("netId", getIntent().getStringExtra("netId"));
            startActivity(intent);
        });

        // Initialize service instances
        courseService = new CourseService(this);
        friendService = new FriendService(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchProfileData(); // Fetch the latest profile data when the activity is resumed
        fetchEnrolledCourses(); // Fetch the latest number of courses when resumed
        fetchFriends(); // Fetch the latest number of friends when resumed
    }

    public void fetchEnrolledCourses() {
        String studentNetId = getIntent().getStringExtra("netId");
        if (studentNetId != null) {
            courseService.getEnrolledCoursesById(studentNetId, new CourseService.GetEnrolledCoursesCallback() {
                @Override
                public void onSuccess(List<Schedule> courses) {
                    runOnUiThread(() -> {
                        if (courses != null) {
                            numcoursesTextView.setText(String.valueOf(courses.size()));
                        } else {
                            numcoursesTextView.setText("0");
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        numcoursesTextView.setText("0");
                        Log.e("ProfileActivity", "Error fetching enrolled courses: " + error);
                    });
                }
            });
        } else {
            numcoursesTextView.setText("0");
            Log.e("ProfileActivity", "Student net ID not found");
        }
    }

    public void fetchFriends() {
        String studentNetId = getIntent().getStringExtra("netId");
        if (studentNetId != null) {
            friendService.getFriendList(studentNetId, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    // Parse the response and update the number of friends in the UI
                    runOnUiThread(() -> {
                        numfriendsTextView.setText(String.valueOf(response.length()));
                    });
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Handle error
                    numfriendsTextView.setText("0");
                    Log.e("ProfileActivity", "Error fetching friends: " + error.getMessage());
                }
            });
        } else {
            numfriendsTextView.setText("0");
            Log.e("ProfileActivity", "Student net ID not found");
        }
    }

    public void updateUI(Profile profile) {
        // Check if profile is not null before setting values
        if (profile != null) {
            String imageUrl = profile.getProfilePictureUrl();
            Glide.with(this)
                    .load(imageUrl)
                    .into(profileImage);
            firstNameTextView.setText(profile.getFirstName());
            lastNameTextView.setText(profile.getLastName());
            usernameTextView.setText(profile.getNetId());
            linkedinUrlTextView.setText(profile.getProfile().getLinkedinUrl());
            externalUrlTextView.setText(profile.getProfile().getExternalUrl());
            descriptionTextView.setText(profile.getProfile().getDescription());
        } else {
            Log.e("FriendProfile", "Profile data is null");
        }
    }

    public void fetchProfileData() {
        String netId = getIntent().getStringExtra("netId");
        UpdateAccount.fetchProfileData(netId, this, new UpdateAccount.ProfileCallback() {
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
}
