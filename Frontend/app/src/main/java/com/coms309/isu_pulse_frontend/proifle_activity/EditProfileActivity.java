package com.coms309.isu_pulse_frontend.proifle_activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.api.AuthenticationService;
import com.coms309.isu_pulse_frontend.api.UpdateAccount;
import com.coms309.isu_pulse_frontend.loginsignup.LoginActivity;
import com.coms309.isu_pulse_frontend.loginsignup.PasswordHasher;
import com.coms309.isu_pulse_frontend.loginsignup.SignupActivity;
import com.coms309.isu_pulse_frontend.model.Profile;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {
    private MaterialButton backButton;
    private TextInputLayout netid;
    private TextInputLayout oldPassword;
    private TextInputLayout newPassword;
    private TextInputLayout confirmNewPassword;
    private TextInputLayout description;
    private TextInputLayout linkedinUrl;
    private TextInputLayout externalUrl;
    private MaterialButton checkCredentialsButton;
    private MaterialButton updateProfileButton;
    private boolean checkcredential = false;
    private Profile existingProfile;
    private String userNetId; // Declare variable for net_id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile);

        // Initialize UserSession and get the user's net_id
        UserSession session = UserSession.getInstance(this);
        userNetId = session.getNetId(); // Fetch the net_id from session

        // Initialize UI elements
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });


        netid = findViewById(R.id.netIdLayout);
        oldPassword = findViewById(R.id.oldPasswordLayout);
        newPassword = findViewById(R.id.newPasswordLayout);
        confirmNewPassword = findViewById(R.id.confirmNewPasswordLayout);
        description = findViewById(R.id.descriptionLayout);
        linkedinUrl = findViewById(R.id.linkedinUrlLayout);
        externalUrl = findViewById(R.id.externalUrlLayout);
        checkCredentialsButton = findViewById(R.id.checkCredentialsButton);
        updateProfileButton = findViewById(R.id.updateProfileButton);

        // Set listeners for buttons
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        checkCredentialsButton.setOnClickListener(view -> checkCredentials());

        updateProfileButton.setOnClickListener(view -> updateProfile());

        // Fetch existing profile data when the activity starts
        fetchProfileData();
    }

    private void fetchProfileData() {
        UpdateAccount.fetchProfileData(this, new UpdateAccount.ProfileCallback() {
            @Override
            public void onSuccess(Profile profile) {
                existingProfile = profile; // Initialize the existing profile
                // Set current profile data in the input fields
                description.getEditText().setText(profile.getProfile().getDescription());
                linkedinUrl.getEditText().setText(profile.getProfile().getLinkedinUrl());
                externalUrl.getEditText().setText(profile.getProfile().getExternalUrl());
            }

            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();
            }
        });
    }

    private void checkCredentials() {
        String netIdinput = Objects.requireNonNull(netid.getEditText()).getText().toString().trim();
        String oldPasswordinput = Objects.requireNonNull(oldPassword.getEditText()).getText().toString().trim();
        String hashPassword = PasswordHasher.hashPassword(oldPasswordinput);

        if (!netIdinput.equals(userNetId)) {
            Toast.makeText(EditProfileActivity.this, "Net ID is wrong", Toast.LENGTH_SHORT).show();
        } else if (netIdinput.isEmpty() || oldPasswordinput.isEmpty()) {
            Toast.makeText(EditProfileActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
        } else {
            AuthenticationService apiService = new AuthenticationService();
            apiService.checkUserExists(netIdinput, EditProfileActivity.this, new AuthenticationService.VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    try {
                        String storedHashedPassword = result.getString("hashedPassword");
                        if (storedHashedPassword.equals(hashPassword)) {
                            Toast.makeText(EditProfileActivity.this, "Correct Information", Toast.LENGTH_SHORT).show();
                            checkcredential = true;
                        } else {
                            Toast.makeText(EditProfileActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(EditProfileActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(EditProfileActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateProfile() {
        UpdateAccount apiService = new UpdateAccount();
        String newPasswordinput = Objects.requireNonNull(newPassword.getEditText()).getText().toString().trim();
        String confirmNewPasswordinput = Objects.requireNonNull(confirmNewPassword.getEditText()).getText().toString().trim();
        String hashPassword = PasswordHasher.hashPassword(newPasswordinput);
        String descriptionInput = Objects.requireNonNull(description.getEditText()).getText().toString().trim();
        String linkedinUrlInput = Objects.requireNonNull(linkedinUrl.getEditText()).getText().toString().trim();
        String externalUrlInput = Objects.requireNonNull(externalUrl.getEditText()).getText().toString().trim();

        if (!checkcredential) {
            Toast.makeText(EditProfileActivity.this, "Please check credentials", Toast.LENGTH_SHORT).show();
            Log.e("CredentialCheck", "Credentials were not verified.");
            return;
        }

        if (newPasswordinput.isEmpty() || confirmNewPasswordinput.isEmpty()) {
            Toast.makeText(EditProfileActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPasswordinput.length() < 8) {
            Toast.makeText(EditProfileActivity.this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPasswordinput.equals(confirmNewPasswordinput)) {
            Toast.makeText(EditProfileActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        String netIdValue = Objects.requireNonNull(netid.getEditText()).getText().toString();
        if (!netIdValue.equals(userNetId))
            Toast.makeText(EditProfileActivity.this, "Net ID does not match", Toast.LENGTH_SHORT).show();

        // Update password
        apiService.updateUserPassword(hashPassword, EditProfileActivity.this, new UpdateAccount.VolleyCallback() {  // Deleted netIdValue
            @Override
            public void onSuccess(String result) {
                Toast.makeText(EditProfileActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(EditProfileActivity.this, "Password update failed", Toast.LENGTH_SHORT).show();
            }
        });

        // Update profile with description, LinkedIn, and external URL
        apiService.updateProfile(descriptionInput, externalUrlInput, linkedinUrlInput, EditProfileActivity.this, new UpdateAccount.VolleyCallback() { // Deleted userNetId
            @Override
            public void onSuccess(String result) {
                Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                startActivity(intent);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(EditProfileActivity.this, "Profile update failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
