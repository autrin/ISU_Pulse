package com.coms309.isu_pulse_frontend.loginsignup;

/**
 * This activity handles user login.
 *
 * @author ntbach
 */
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.coms309.isu_pulse_frontend.MainActivity;
import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.api.AuthenticationService;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The LoginActivity handles user login.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * EditText for entering the user's NetID.
     */
    private EditText netId;

    /**
     * EditText for entering the user's password.
     */
    private EditText passWord;

    /**
     * TextView for the "Enter" button.
     */
    private TextView enter;

    /**
     * TextView for the "Sign Up" button.
     */
    private TextView signup;

    private TextView forgotPassword;

    /**
     * Initializes the activity, sets up UI components, and handles user login.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);

        // Initialize UI elements
        netId = findViewById(R.id.netid_isu_pulse);
        passWord = findViewById(R.id.password_isu_pulse);
        enter = findViewById(R.id.enter_isu_pulse);
        signup = findViewById(R.id.sign_up_isu_pulse);
        forgotPassword = findViewById(R.id.forgot_password_isu_pulse);


        forgotPassword.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
            startActivity(intent);
        });



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
                    return;
                }

                if (passWordInput.length() < 8) {
                    Toast.makeText(LoginActivity.this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Proceed to check if user exists
                AuthenticationService apiService = new AuthenticationService();
                apiService.checkUserExists(netIdInput, LoginActivity.this, new AuthenticationService.VolleyCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        // User exists, now verify the password
                        try {
                            String storedHashedPassword = result.getString("hashedPassword");

                            if (storedHashedPassword.equals(hashPassword)) {
                                // Save netId and userType using UserSession
                                UserSession.getInstance(LoginActivity.this).setNetId(netIdInput, LoginActivity.this);
                                UserSession.getInstance(LoginActivity.this).setUserType(result.getString("userType"), LoginActivity.this);

                                // Passwords match, login successful
                                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show(); // TODO: You can comment this later

                                // Proceed to the main activity
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("navigateToHome", true);  // Add this flag if you want to open Home by default
                                intent.putExtra("userRole", result.getString("userType"));  // Pass the user role
                                startActivity(intent);

                                finish();
                            } else {
                                // Passwords don't match
                                Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Error parsing response in onSuccess in LoginActivity", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String message) {
                        // User does not exist or other error
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });

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
