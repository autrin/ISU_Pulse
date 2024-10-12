package com.coms309.isu_pulse_frontend.proifle_activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.api.AuthenticationService;
import com.coms309.isu_pulse_frontend.api.UpdateAccount;
import com.coms309.isu_pulse_frontend.loginsignup.PasswordHasher;
import com.coms309.isu_pulse_frontend.model.Profile;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile);

        backButton = findViewById(R.id.backButton);
        netid = findViewById(R.id.netIdLayout);
        oldPassword = findViewById(R.id.oldPasswordLayout);
        newPassword = findViewById(R.id.newPasswordLayout);
        confirmNewPassword = findViewById(R.id.confirmNewPasswordLayout);
        description = findViewById(R.id.descriptionLayout);
        linkedinUrl = findViewById(R.id.linkedinUrlLayout);
        externalUrl = findViewById(R.id.externalUrlLayout);
        checkCredentialsButton = findViewById(R.id.checkCredentialsButton);
        updateProfileButton = findViewById(R.id.updateProfileButton);

        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        checkCredentialsButton.setOnClickListener(view -> {
            String netIdinput = Objects.requireNonNull(netid.getEditText()).getText().toString().trim();
            String oldPasswordinput = Objects.requireNonNull(oldPassword.getEditText()).getText().toString().trim();
            String hashPassword = PasswordHasher.hashPassword(oldPasswordinput);

            if (netIdinput.isEmpty() || oldPasswordinput.isEmpty()) {
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
        });

        updateProfileButton.setOnClickListener(view -> {
            UpdateAccount apiService = new UpdateAccount();
            String newPasswordinput = Objects.requireNonNull(newPassword.getEditText()).getText().toString().trim();
            String confirmNewPasswordinput = Objects.requireNonNull(confirmNewPassword.getEditText()).getText().toString().trim();
            String hashPassword = PasswordHasher.hashPassword(newPasswordinput);
            String descriptionInput = Objects.requireNonNull(description.getEditText()).getText().toString().trim();
            String linkedinUrlInput = Objects.requireNonNull(linkedinUrl.getEditText()).getText().toString().trim();
            String externalUrlInput = Objects.requireNonNull(externalUrl.getEditText()).getText().toString().trim();

            if (!checkcredential) {
                Toast.makeText(EditProfileActivity.this, "Please check credentials", Toast.LENGTH_SHORT).show();
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
            apiService.updateUserPassword(netIdValue, hashPassword, EditProfileActivity.this, new UpdateAccount.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Toast.makeText(EditProfileActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(EditProfileActivity.this, "Password update failed", Toast.LENGTH_SHORT).show();
                }
            });

            // Update profile with additional fields
            existingProfile.getProfile().setDescription(descriptionInput);
            existingProfile.getProfile().setLinkedinUrl(linkedinUrlInput);
            existingProfile.getProfile().setExternalUrl(externalUrlInput);
            apiService.updateProfile(netIdValue, existingProfile, EditProfileActivity.this, new UpdateAccount.VolleyCallback() {
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
        });
    }
}