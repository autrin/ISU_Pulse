package com.coms309.isu_pulse_frontend.profile_activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.api.AuthenticationService;
import com.coms309.isu_pulse_frontend.api.UpdateAccount;
import com.coms309.isu_pulse_frontend.loginsignup.LoginActivity;
import com.coms309.isu_pulse_frontend.profile_activity.ProfileActivity;
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
    private Button backButton;
    private EditText netid;
    private EditText oldPassword;
    private EditText newPassword;
    private EditText confirmNewPassword;
    private EditText description;
    private EditText linkedinUrl;
    private EditText externalUrl;
    private Button checkCredentialsButton;
    private Button updateProfileButton;
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


        netid = findViewById(R.id.netIdEditText);
        oldPassword = findViewById(R.id.oldPasswordEditText);
        newPassword = findViewById(R.id.newPasswordEditText);
        confirmNewPassword = findViewById(R.id.confirmNewPasswordEditText);
        description = findViewById(R.id.descriptionEditText);
        linkedinUrl = findViewById(R.id.linkedinUrlEditText);
        externalUrl = findViewById(R.id.externalUrlEditText);
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
        UpdateAccount.fetchProfileData(String.valueOf(netid), this, new UpdateAccount.ProfileCallback() {
            @Override
            public void onSuccess(Profile profile) {
                existingProfile = profile; // Initialize the existing profile
                // Set current profile data in the input fields
                description.setText(profile.getProfile().getDescription());
                linkedinUrl.setText(profile.getProfile().getLinkedinUrl());
                externalUrl.setText(profile.getProfile().getExternalUrl());
            }

            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();
            }
        });
    }

    private void checkCredentials() {
        String netIdinput = netid.getText().toString().trim();
        String oldPasswordinput = oldPassword.getText().toString().trim();
        String hashPassword = PasswordHasher.hashPassword(oldPasswordinput);

        if (!netIdinput.equals(userNetId)) {
            Toast.makeText(EditProfileActivity.this, "Net ID is wrong", Toast.LENGTH_SHORT).show();
        } else if (netIdinput.isEmpty() || oldPasswordinput.isEmpty()) {
            Toast.makeText(EditProfileActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
        } else {
            AuthenticationService apiService = new AuthenticationService();
            apiService.checkUserExists(userNetId, EditProfileActivity.this, new AuthenticationService.VolleyCallback() {
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

        String newPasswordinput = newPassword.getText().toString().trim();
        String confirmNewPasswordinput = confirmNewPassword.getText().toString().trim();
        String hashPassword = PasswordHasher.hashPassword(newPasswordinput);
        String descriptionInput = description.getText().toString().trim();
        String linkedinUrlInput = linkedinUrl.getText().toString().trim();
        String externalUrlInput = externalUrl.getText().toString().trim();

        // Ensure credentials have been verified
        if (!checkcredential) {
            Toast.makeText(EditProfileActivity.this, "Please check credentials first", Toast.LENGTH_SHORT).show();
            Log.e("CredentialCheck", "Credentials were not verified.");
            return;
        }

        // Check if the user is attempting to change the password
        boolean isChangingPassword = !newPasswordinput.isEmpty() || !confirmNewPasswordinput.isEmpty();

        if (isChangingPassword) {
            // Validate password fields
            if (newPasswordinput.isEmpty() || confirmNewPasswordinput.isEmpty()) {
                Toast.makeText(EditProfileActivity.this, "Please enter both new password and confirmation", Toast.LENGTH_SHORT).show();
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

            // Update password if validation passes
            apiService.updateUserPassword(hashPassword, EditProfileActivity.this, new UpdateAccount.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Toast.makeText(EditProfileActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                    startActivity(intent);

                }

                @Override
                public void onError(String message) {
                    Toast.makeText(EditProfileActivity.this, "Password update failed", Toast.LENGTH_SHORT).show();
                }
            });
        }

        boolean isChangingProfile = !descriptionInput.isEmpty() || !linkedinUrlInput.isEmpty() || !externalUrlInput.isEmpty();
        if (isChangingProfile) {
            if (descriptionInput.isEmpty() || linkedinUrlInput.isEmpty() || externalUrlInput.isEmpty()) {
                Toast.makeText(EditProfileActivity.this, "Please enter at least one field to update", Toast.LENGTH_SHORT).show();
                return;
            }
            apiService.updateProfile(descriptionInput, externalUrlInput, linkedinUrlInput, EditProfileActivity.this, new UpdateAccount.VolleyCallback() {
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

}
