package com.coms309.isu_pulse_frontend.proifle_activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.coms309.isu_pulse_frontend.MainActivity;
import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.course_functional.CourseView;
import com.coms309.isu_pulse_frontend.model.Profile;
import com.coms309.isu_pulse_frontend.api.UpdateAccount;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private MaterialButton editProfile;
    private MaterialButton logout;
    private ShapeableImageView profileImage;
    private TextView coursesTextView;
    private TextView friendsTextView;
    private TextView numcoursesTextView;
    private TextView firstNameTextView;
    private TextView lastNameTextView;
    private TextView linkedinUrlTextView;
    private TextView externalUrlTextView;
    private TextView descriptionTextView;
    private NavigationView navigationView;
    private View headerView;
    private TextView navHeaderTitle;
    private ImageView navHeaderImage;
    private TextView navHeaderEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        toolbar = findViewById(R.id.toolbar);
        coursesTextView = findViewById(R.id.coursesLabelTextView);
        friendsTextView = findViewById(R.id.friendsLabelTextView);
        numcoursesTextView = findViewById(R.id.coursesCountTextView);

        editProfile = findViewById(R.id.updateProfileButton);
        logout = findViewById(R.id.logoutButton);
        profileImage = findViewById(R.id.profileImage);
        firstNameTextView = findViewById(R.id.firstNameTextView);
        lastNameTextView = findViewById(R.id.lastNameTextView);
        linkedinUrlTextView = findViewById(R.id.linkedinUrlTextView);
        externalUrlTextView = findViewById(R.id.externalUrlTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        navigationView = findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        navHeaderTitle = headerView.findViewById(R.id.nav_header_title);
        navHeaderImage = headerView.findViewById(R.id.nav_header_image);
        navHeaderEmail = headerView.findViewById(R.id.nav_header_email);

        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
        });

        editProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        logout.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
        });

        fetchProfileData();

        coursesTextView.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, CourseView.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchProfileData(); // Fetch the latest profile data when the activity is resumed
    }

    private void fetchProfileData() {
        UpdateAccount.fetchProfileData(this, new UpdateAccount.ProfileCallback() {
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
        String imageUrl = "https://firebasestorage.googleapis.com/v0/b/coms-309-image-storage.appspot.com/o/images%2Fbaonguyen.jpg?alt=media&token=9a992e70-2847-4007-b03c-90b622029b0d";
        // Load image into profileImage (ShapeableImageView)
        Glide.with(this)
                .load(imageUrl)
                .into(profileImage);
        // Load the same image into navHeaderImage (ImageView)
        Glide.with(this)
                .load(imageUrl)
                .into(navHeaderImage);
        firstNameTextView.setText(profile.getFirstName());
        lastNameTextView.setText(profile.getLastName());
        linkedinUrlTextView.setText(profile.getProfile().getLinkedinUrl());
        externalUrlTextView.setText(profile.getProfile().getExternalUrl());
        descriptionTextView.setText(profile.getProfile().getDescription());
        navHeaderTitle.setText(String.format(getString(R.string.nav_header_title), profile.getFirstName(), profile.getLastName()));
        String email = profile.getNetId() + "@iastate.edu";
        navHeaderEmail.setText(email);
    }
}
