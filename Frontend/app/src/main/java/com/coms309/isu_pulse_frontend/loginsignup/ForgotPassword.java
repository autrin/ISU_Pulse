package com.coms309.isu_pulse_frontend.loginsignup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.coms309.isu_pulse_frontend.MainActivity;
import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.api.AuthenticationService;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPassword extends AppCompatActivity {
    private TextView forgotPasswordDescription;
    private EditText emailOtpInput;
    private Button verifyOtpButton;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private Button updateProfileButton;
    private Button backButton;
    private AuthenticationService authenticationService;
    private boolean isverified;
    private String netId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        forgotPasswordDescription = findViewById(R.id.forgot_password_description);
        emailOtpInput = findViewById(R.id.email_otp_input);
        verifyOtpButton = findViewById(R.id.verify_otp_button);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        updateProfileButton = findViewById(R.id.update_profile_button);
        backButton = findViewById(R.id.back_button);
        authenticationService = new AuthenticationService();
        isverified = false;


        verifyOtpButton.setOnClickListener(view -> {
            String input = emailOtpInput.getText().toString();

            if (!isverified) {
                if (verifyOtpButton.getText().toString().equalsIgnoreCase("Verify NetId")) {
                    // Step 1: Check if user exists
                    authenticationService.checkUserExists(input, ForgotPassword.this, new AuthenticationService.VolleyCallback() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            // User exists, proceed to sending OTP
                            netId = input;
                            forgotPasswordDescription.setText("Enter your email for OTP.");
                            emailOtpInput.setHint("Enter email");
                            verifyOtpButton.setText("Send OTP");
                        }

                        @Override
                        public void onError(String message) {
                            // User does not exist
                            Toast.makeText(ForgotPassword.this, "User does not exist", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (verifyOtpButton.getText().toString().equalsIgnoreCase("Send OTP")) {
                    // Step 2: Send OTP
                    authenticationService.sendOtp(input, ForgotPassword.this, new AuthenticationService.ForgetPasswordCallback() {
                        @Override
                        public void onSuccess(String response) {
                            // OTP sent successfully
                            Toast.makeText(ForgotPassword.this, "OTP sent to your email.", Toast.LENGTH_SHORT).show();
                            forgotPasswordDescription.setText("Enter the OTP you received.");
                            emailOtpInput.setHint("Enter OTP in 6-digit number");
                            verifyOtpButton.setText("Verify OTP");
                            emailOtpInput.setTag(input); // Save email for verification
                        }

                        @Override
                        public void onError(String message) {
                            // Failed to send OTP
                            Toast.makeText(ForgotPassword.this, "Failed to send OTP: " + message, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (verifyOtpButton.getText().toString().equalsIgnoreCase("Verify OTP")) {
                    // Step 3: Verify OTP
                    String email = emailOtpInput.getTag().toString(); // Retrieve saved email
                    authenticationService.verifyOtp(email, input, ForgotPassword.this, new AuthenticationService.ForgetPasswordCallback() {
                        @Override
                        public void onSuccess(String response) {
                            // OTP verified successfully
                            Toast.makeText(ForgotPassword.this, "OTP verified. You can now reset your password.", Toast.LENGTH_SHORT).show();
                            isverified = true;
                            forgotPasswordDescription.setText("Enter your new password.");
                            emailOtpInput.setVisibility(View.GONE);
                            verifyOtpButton.setVisibility(View.GONE);
                            passwordInput.setVisibility(View.VISIBLE);
                            confirmPasswordInput.setVisibility(View.VISIBLE);
                            updateProfileButton.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(String message) {
                            // Failed to verify OTP
                            Toast.makeText(ForgotPassword.this, "Failed to verify OTP: " + message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });



        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(ForgotPassword.this, LoginActivity.class);
            startActivity(intent);
        });

        updateProfileButton.setOnClickListener(view -> {
            String newHashPassword = PasswordHasher.hashPassword(passwordInput.getText().toString());
            String confirmHashPassword = PasswordHasher.hashPassword(confirmPasswordInput.getText().toString());
            if (!isverified) {
                Toast.makeText(ForgotPassword.this, "Please verify your identity", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (newHashPassword.isEmpty() || confirmHashPassword.isEmpty()) {
                Toast.makeText(ForgotPassword.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (newHashPassword.length() < 8) {
                Toast.makeText(ForgotPassword.this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (!newHashPassword.equals(confirmHashPassword)) {
                Toast.makeText(ForgotPassword.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                authenticationService.forgotPassword(netId, newHashPassword,ForgotPassword.this, new AuthenticationService.ForgetPasswordCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(ForgotPassword.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ForgotPassword.this, LoginActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(String message) {
                        // User does not exist or other error
                        Toast.makeText(ForgotPassword.this, "Update password failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}
