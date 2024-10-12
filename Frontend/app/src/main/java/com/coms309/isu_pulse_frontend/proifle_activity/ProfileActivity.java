package com.coms309.isu_pulse_frontend.proifle_activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.coms309.isu_pulse_frontend.MainActivity;
import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.model.Profile;
import com.coms309.isu_pulse_frontend.api.UpdateAccount;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

public class ProfileActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private MaterialButton editProfile;
    private MaterialButton logout;
    private ShapeableImageView profileImage;
    private TextView firstNameTextView;
    private TextView lastNameTextView;
    private TextView linkedinUrlTextView;
    private TextView externalUrlTextView;
    private TextView descriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        toolbar = findViewById(R.id.toolbar);
        editProfile = findViewById(R.id.updateProfileButton);
        logout = findViewById(R.id.logoutButton);
        profileImage = findViewById(R.id.profileImage);
        firstNameTextView = findViewById(R.id.firstNameTextView);
        lastNameTextView = findViewById(R.id.lastNameTextView);
        linkedinUrlTextView = findViewById(R.id.linkedinUrlTextView);
        externalUrlTextView = findViewById(R.id.externalUrlTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);

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
        Glide.with(this)
                .load(imageUrl)
                .into(profileImage);
        String firstName = profile.getFirstName();
        String lastName = profile.getLastName();

        // Log the values for debugging
        Log.d("ProfileActivity", "First Name: " + firstName);
        Log.d("ProfileActivity", "Last Name: " + lastName);
        // Set the values to the TextViews
        firstNameTextView.setText(firstName != null ? firstName : "No First Name");
        lastNameTextView.setText(lastName != null ? lastName : "No Last Name");

        firstNameTextView.setText(profile.getFirstName());
        lastNameTextView.setText(profile.getLastName());
        linkedinUrlTextView.setText(profile.getProfile().getLinkedinUrl());
        externalUrlTextView.setText(profile.getProfile().getExternalUrl());
        descriptionTextView.setText(profile.getProfile().getDescription());
    }
}