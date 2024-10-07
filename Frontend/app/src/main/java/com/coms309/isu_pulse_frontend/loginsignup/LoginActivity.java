package com.coms309.isu_pulse_frontend.loginsignup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

// Other necessary imports
import com.coms309.isu_pulse_frontend.MainActivity;
import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.api.ApiService_LoginSignup;
import com.coms309.isu_pulse_frontend.loginsignup.PasswordHasher;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private EditText netId;
    private EditText passWord;
    private TextView enter;
    private TextView signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);

        // Initialize UI elements
        netId = findViewById(R.id.netid_isu_pulse);
        passWord = findViewById(R.id.password_isu_pulse);
        enter = findViewById(R.id.enter_isu_pulse);
        signup = findViewById(R.id.sign_up_isu_pulse);

        // Set onClickListener for the "Enter" button
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String netIdInput = netId.getText().toString().trim();
                String passWordInput = passWord.getText().toString().trim();
                String hashPassword = PasswordHasher.hashPassword(passWordInput);

                // Input validation
                if (netIdInput.isEmpty() || passWordInput.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                } else if (passWordInput.length() < 8) {
                    Toast.makeText(LoginActivity.this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                } else {
                    // Proceed to check if user exists
                    ApiService_LoginSignup apiService = new ApiService_LoginSignup();
                    apiService.checkUserExists(netIdInput, LoginActivity.this, new ApiService_LoginSignup.VolleyCallback() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            // User exists, now verify the password
                            try {
                                String storedHashedPassword = result.getString("hashedPassword");

                                if (storedHashedPassword.equals(hashPassword)) {
                                    // Passwords match, login successful
                                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                                    // Proceed to the main activity
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Passwords don't match
                                    Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(LoginActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(String message) {
                            // User does not exist or other error
                            Toast.makeText(LoginActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        // Set onClickListener for the "Sign Up" button
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}
