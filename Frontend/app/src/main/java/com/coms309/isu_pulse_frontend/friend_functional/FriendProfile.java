package com.coms309.isu_pulse_frontend.friend_functional;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
import org.json.JSONException;

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

        // Initialize views
        backButton = findViewById(R.id.backButton);
        addFriendButton = findViewById(R.id.addFriendButton);
        pingFriendButton = findViewById(R.id.pingFriendButton);
        numcoursesTextView = findViewById(R.id.coursesCountTextView);
        coursesTextView = findViewById(R.id.coursesLabelTextView);
        friendsTextView = findViewById(R.id.friendsLabelTextView);
        numfriendsTextView = findViewById(R.id.friendsCountTextView);
        profileImage = findViewById(R.id.profileImage);

        // Initialize other text views
        firstNameTextView = findViewById(R.id.firstNameTextView);
        lastNameTextView = findViewById(R.id.lastNameTextView);
        usernameTextView = findViewById(R.id.usernameTextView);
        linkedinUrlTextView = findViewById(R.id.linkedinUrlTextView);
        externalUrlTextView = findViewById(R.id.externalUrlTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);

        // Set back button functionality
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(FriendProfile.this, ProfileActivity.class);
            startActivity(intent);
        });

        // Set ping button functionality
        pingFriendButton.setOnClickListener(v -> {
            Intent intent = new Intent(FriendProfile.this, ChatActivity.class);
            intent.putExtra("netId", getIntent().getStringExtra("netId"));
            startActivity(intent);
        });

        // Initialize service instances
        courseService = new CourseService(this);
        friendService = new FriendService(this);

        // Add popup functionality for courses and friends
        View.OnClickListener showPopup = v -> {
            if (v == numcoursesTextView || v == coursesTextView) {
                showPopup("Courses Taking", "courses");
            } else if (v == numfriendsTextView || v == friendsTextView) {
                showPopup("Friends List", "friends");
            }
        };
        numcoursesTextView.setOnClickListener(showPopup);
        coursesTextView.setOnClickListener(showPopup);
        numfriendsTextView.setOnClickListener(showPopup);
        friendsTextView.setOnClickListener(showPopup);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchProfileData();
        fetchEnrolledCourses();
        fetchFriends();
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
                        Log.e("FriendProfile", "Error fetching enrolled courses: " + error);
                    });
                }
            });
        } else {
            numcoursesTextView.setText("0");
            Log.e("FriendProfile", "Student net ID not found");
        }
    }

    public void fetchFriends() {
        String studentNetId = getIntent().getStringExtra("netId");
        if (studentNetId != null) {
            friendService.getFriendList(studentNetId, response -> {
                runOnUiThread(() -> numfriendsTextView.setText(String.valueOf(response.length())));
            }, error -> {
                numfriendsTextView.setText("0");
                Log.e("FriendProfile", "Error fetching friends: " + error.getMessage());
            });
        } else {
            numfriendsTextView.setText("0");
            Log.e("FriendProfile", "Student net ID not found");
        }
    }

    public void updateUI(Profile profile) {
        if (profile != null) {
            Glide.with(this).load(profile.getProfilePictureUrl()).into(profileImage);
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
                error.printStackTrace();
            }
        });
    }

    private void showPopup(String title, String type) {
        String studentNetId = getIntent().getStringExtra("netId");
        if (studentNetId != null) {
            View popupView = LayoutInflater.from(FriendProfile.this).inflate(R.layout.popup_layout, null);
            PopupWindow popupWindow = new PopupWindow(popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true);

            TextView popupTitle = popupView.findViewById(R.id.popupTitle);
            TextView popupContent = popupView.findViewById(R.id.popupContent);

            popupTitle.setText(title);

            if (type.equals("courses")) {
                courseService.getEnrolledCoursesById(studentNetId, new CourseService.GetEnrolledCoursesCallback() {
                    @Override
                    public void onSuccess(List<Schedule> courses) {
                        StringBuilder courseList = new StringBuilder();
                        for (Schedule course : courses) {
                            courseList.append(course.getCourse().getCode()).append("\n");
                        }
                        popupContent.setText(courseList.toString());
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("FriendProfile", "Error fetching courses for popup: " + error);
                    }
                });
                popupWindow.showAsDropDown(coursesTextView, 0, 0);
            } else if (type.equals("friends")) {
                friendService.getFriendList(studentNetId, response -> {
                    StringBuilder friendsList = new StringBuilder();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            String firstName = response.getJSONObject(i).getString("firstName");
                            String lastName = response.getJSONObject(i).getString("lastName");
                            friendsList.append(firstName).append(" ").append(lastName).append("\n");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    popupContent.setText(friendsList.toString());
                }, error -> {
                    Log.e("FriendProfile", "Error fetching friends for popup: " + error.getMessage());
                });
                popupWindow.showAsDropDown(friendsTextView, 0, 0);
            }
        }
    }
}
