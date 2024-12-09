package com.coms309.isu_pulse_frontend.profile_activity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.coms309.isu_pulse_frontend.MainActivity;
import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.api.AuthenticationService;
import com.coms309.isu_pulse_frontend.api.FriendService;
import com.coms309.isu_pulse_frontend.chat_system.ChatList;
import com.coms309.isu_pulse_frontend.course_functional.CourseView;
import com.coms309.isu_pulse_frontend.friend_functional.Friend;
import com.coms309.isu_pulse_frontend.friend_functional.FriendList;
import com.coms309.isu_pulse_frontend.friend_functional.FriendRequest;
import com.coms309.isu_pulse_frontend.friend_functional.FriendSentRequest;
import com.coms309.isu_pulse_frontend.friend_functional.FriendSuggestion;
import com.coms309.isu_pulse_frontend.loginsignup.LoginActivity;
import com.coms309.isu_pulse_frontend.model.Profile;
import com.coms309.isu_pulse_frontend.api.UpdateAccount;
import com.coms309.isu_pulse_frontend.api.CourseService;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;
import com.coms309.isu_pulse_frontend.schedule.Schedule;
import com.coms309.isu_pulse_frontend.ui.home.HomeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private Button chatButton;
    private Button editProfile;
    private Button logout;
    private ImageView profileImage;
    private TextView coursesTextView;
    private TextView friendsTextView;
    private TextView friendsrequestsTextView;
    private TextView friendSentRequestTextView;
    private TextView friendSuggestionTextView;
    private TextView numcoursesTextView;
    private TextView numfriendsTextView;
    private TextView numrequestsTextView;
    private TextView numsuggestionsTextView;
    private TextView numsentrequestsTextView;
    private ImageButton backButton;
    private TextView firstNameTextView;
    private TextView lastNameTextView;
    private TextView usernameTextView;
    private TextView linkedinUrlTextView;
    private TextView externalUrlTextView;
    private TextView descriptionTextView;
    private Button deleteAccountButton;
    public List<Friend> friendRequestList;
    public List<Friend> friendSuggestionList;
    private AuthenticationService authenticationService;

    private CourseService courseService;
    private FriendService friendService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        chatButton = findViewById(R.id.chatButton);
        coursesTextView = findViewById(R.id.coursesLabelTextView);
        friendsTextView = findViewById(R.id.friendsLabelTextView);
        friendsrequestsTextView = findViewById(R.id.friendsRequest);
        friendSentRequestTextView = findViewById(R.id.sentRequestsLabelTextView);
        friendSuggestionTextView = findViewById(R.id.friendSuggestionsLabelTextView);
        numcoursesTextView = findViewById(R.id.coursesCountTextView);
        numfriendsTextView = findViewById(R.id.friendsCountTextView);
        numrequestsTextView = findViewById(R.id.friendsRequestNumber);
        numsentrequestsTextView = findViewById(R.id.sentRequestsCountTextView);
        numsuggestionsTextView = findViewById(R.id.friendSuggestionsCountTextView);
        deleteAccountButton = findViewById(R.id.deleteAccountButton);

        Button deleteAccountButton = findViewById(R.id.deleteAccountButton);
        authenticationService = new AuthenticationService();

        deleteAccountButton.setOnClickListener(v -> {
            // Create an AlertDialog builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete Account");
            builder.setMessage("Are you sure you want to delete your account? This action cannot be undone.");

            // Add "Yes" button
            builder.setPositiveButton("Yes", (dialog, which) -> {
                authenticationService.deleteAccount(UserSession.getInstance().getNetId(), ProfileActivity.this, new AuthenticationService.LoginCallback(){
                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(ProfileActivity.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(ProfileActivity.this, "Delete account failed", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            // Add "No" button
            builder.setNegativeButton("No", (dialog, which) -> {
                // Dismiss the dialog
                dialog.dismiss();
            });

            // Create and show the AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();
        });



        friendRequestList = new ArrayList<>();
        friendSuggestionList = new ArrayList<>();

        backButton = findViewById(R.id.backButton);
        editProfile = findViewById(R.id.updateProfileButton);
        logout = findViewById(R.id.logoutButton);
        profileImage = findViewById(R.id.profileImage);
        firstNameTextView = findViewById(R.id.firstNameTextView);
        lastNameTextView = findViewById(R.id.lastNameTextView);
        usernameTextView = findViewById(R.id.usernameTextView);
        linkedinUrlTextView = findViewById(R.id.linkedinUrlTextView);
        externalUrlTextView = findViewById(R.id.externalUrlTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        chatButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ChatList.class);
            startActivity(intent);
        });

        friendsTextView.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, FriendList.class);
            startActivity(intent);
        });

        friendsrequestsTextView.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, FriendRequest.class);
            startActivity(intent);
        });

        friendSentRequestTextView.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, FriendSentRequest.class);
            startActivity(intent);
        });

        editProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        logout.setOnClickListener(v -> {
            // Clear session data
            UserSession.getInstance(ProfileActivity.this).clearSession(ProfileActivity.this);

            Intent intent = new Intent(ProfileActivity.this, MainActivity.class); //TODO: Question Is this supposed to be MainActivity?
            startActivity(intent);
            finish(); // Added this line too
        });

        coursesTextView.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, CourseView.class);
            startActivity(intent);
        });

        courseService = new CourseService(this);
        friendService = new FriendService(this);



        friendSuggestionTextView.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, FriendSuggestion.class);
            startActivity(intent);
        });

        fetchProfileData();
        fetchEnrolledCourses();
        fetchFriends();
        fetchFriendsRequest();
        fetchFriendsSendRequest();
        fetchFriendSuggestions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchProfileData(); // Fetch the latest profile data when the activity is resumed
        fetchEnrolledCourses(); // Fetch the latest number of courses when resumed
        fetchFriends(); // Fetch the latest number of friends when resumed
        fetchFriendsRequest(); // Fetch the latest number of friend requests when resumed
        fetchFriendsSendRequest(); // Fetch the latest number of friend sent requests when resumed
        fetchFriendSuggestions(); // Fetch the latest number of friend suggestions when resumed
    }

    private void fetchProfileData() {
        String netId = UserSession.getInstance().getNetId();
        UpdateAccount.fetchProfileData(netId,this, new UpdateAccount.ProfileCallback() {
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

    private void updateUI(Profile profile) {
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
    }

    private void fetchEnrolledCourses() {
        // Get the current student's net ID using UserSession
        String studentNetId = UserSession.getInstance().getNetId();

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

    private void fetchFriends() {
        // Get the current student's net ID using UserSession
        String studentNetId = UserSession.getInstance().getNetId();

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


    private void fetchFriendsRequest() {
        // Get the current student's net ID using UserSession
        String studentNetId = UserSession.getInstance().getNetId();

        if (studentNetId != null) {
            friendService.getReceivedRequests(studentNetId, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    // Parse the response and update the number of friends in the UI
                    runOnUiThread(() -> {
                        numrequestsTextView.setText(String.valueOf(response.length()));
                    });
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Handle error
                    numrequestsTextView.setText("0");
                    Log.e("ProfileActivity", "Error fetching friends: " + error.getMessage());
                }
            });
        } else {
            numrequestsTextView.setText("0");
            Log.e("ProfileActivity", "Student net ID not found");
        }
    }

    private void fetchFriendsSendRequest() {
        // Get the current student's net ID using UserSession
        String studentNetId = UserSession.getInstance().getNetId();

        if (studentNetId != null) {
            friendService.getSentRequests(studentNetId, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    // Parse the response and update the number of friends in the UI
                    runOnUiThread(() -> {
                        numsentrequestsTextView.setText(String.valueOf(response.length()));
                    });
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Handle error
                    numsentrequestsTextView.setText("0");
                    Log.e("ProfileActivity", "Error fetching friends: " + error.getMessage());
                }
            });
        } else {
            numsentrequestsTextView.setText("0");
            Log.e("ProfileActivity", "Student net ID not found");
        }
    }



    private void fetchFriendSuggestions() {
        String studentNetId = UserSession.getInstance().getNetId();
        FriendService friendService = new FriendService(this);
        friendService.getFriendSuggestions(studentNetId, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject friendObject = response.getJSONObject(i);
                        String firstName = friendObject.getString("firstName");
                        String lastName = friendObject.getString("lastName");
                        String netId = friendObject.getString("netId");
                        friendSuggestionList.add(new Friend(firstName, lastName, netId));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProfileActivity.this, "Failed to fetch friends", Toast.LENGTH_SHORT).show();
            }
        });
    }
}