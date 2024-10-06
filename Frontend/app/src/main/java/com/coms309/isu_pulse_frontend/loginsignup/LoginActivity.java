package com.coms309.isu_pulse_frontend.loginsignup;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

// Volley imports
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.coms309.isu_pulse_frontend.MainActivity;
import com.coms309.isu_pulse_frontend.R;

// Other necessary imports
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText netId;
    private EditText passWord;
    private TextView enter;
    private TextView signup;

    // Define your mock server URL
    private static final String MOCK_SERVER_URL = "https://68acd349-c1c9-43f0-a12a-c47a910b017c.mock.pstmn.io";

    // RequestQueue for Volley
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);

        // Initialize UI elements
        netId = findViewById(R.id.netid_isu_pulse);
        passWord = findViewById(R.id.password_isu_pulse);
        enter = findViewById(R.id.enter_isu_pulse);
        signup = findViewById(R.id.sign_up_isu_pulse);
        // forgotPassword = findViewById(R.id.forgot_password_isu_pulse);

        // Initialize the RequestQueue
        requestQueue = Volley.newRequestQueue(this);

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
                    // Proceed with the network request
                    makeLoginRequest(netIdInput, hashPassword);
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


    private void makeLoginRequest(final String netId, final String hashPassword) {
        // Construct the URL with query parameters
        String url = MOCK_SERVER_URL + "/user/test"; // Adjust the endpoint as needed

        // If your server expects query parameters, you can append them like this:
        // String urlWithParams = url + "?netId=" + netId + "&password=" + hashPassword;

        // However, it's more secure to use POST for sending credentials

        // Create a StringRequest with POST method
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the response from the server
                        // Assuming the server returns a success message or user data
                        Toast.makeText(LoginActivity.this, "Login Successful: " + response, Toast.LENGTH_LONG).show();

                        // Proceed to the next activity, e.g., MainActivity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Optional: Finish the LoginActivity so user can't go back to it
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors
                        Toast.makeText(LoginActivity.this, "Login Failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        // Optionally log the error or handle specific error types
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login endpoint
                Map<String, String> params = new HashMap<>();
                params.put("netId", netId);
                params.put("password", hashPassword);
                return params;
            }
        };

        // Optionally set a tag to the request for cancellation purposes
        stringRequest.setTag("LOGIN_REQUEST");

        // Add the request to the RequestQueue
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Cancel all pending requests to prevent memory leaks
        if (requestQueue != null) {
            requestQueue.cancelAll("LOGIN_REQUEST");
        }
    }
}
