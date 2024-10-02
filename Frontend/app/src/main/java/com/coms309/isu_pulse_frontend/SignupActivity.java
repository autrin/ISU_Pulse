package com.coms309.isu_pulse_frontend;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.coms309.isu_pulse_frontend.PasswordHasher;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


public class SignupActivity extends AppCompatActivity {
    private EditText netId;
    private EditText firstname;
    private EditText lastname;
    private EditText email;
    private EditText password;
    private EditText retypepassword;
    private TextView enter;
    private TextView signin;
    private Spinner usertype;
//    private TextView forgotpassword;


    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.sign_up);

        firstname = findViewById(R.id.firstname_isu_pulse);
        lastname = findViewById(R.id.lastname_isu_pulse);
        email = findViewById(R.id.email_isu_pulse);
        netId = findViewById(R.id.netid_isu_pulse);;
        password = findViewById(R.id.password_isu_pulse);
        retypepassword = findViewById(R.id.confirm_password_isu_pulse);
        enter = findViewById(R.id.enter_isu_pulse);
        signin = findViewById(R.id.sign_in_isu_pulse);
        usertype = findViewById(R.id.usertype_isu_pulse);
//        forgotpassword = findViewById(R.id.forgot_password_isu_pulse);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.user_roles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        usertype.setAdapter(adapter);

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstNameInput = firstname.getText().toString();
                String lastNameInput = lastname.getText().toString();
                String emailInput = email.getText().toString();
                String netIdInput = netId.getText().toString();
                String passwordInput = password.getText().toString();
                String hashedPassword = PasswordHasher.hashPassword(passwordInput);
                String retypePasswordInput = retypepassword.getText().toString();

                if (firstNameInput.isEmpty() || lastNameInput.isEmpty() || emailInput.isEmpty() || netIdInput.isEmpty() || passwordInput.isEmpty() || retypePasswordInput.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                }
                else if (passwordInput.length() < 8) {
                    Toast.makeText(SignupActivity.this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                }
                else if (!passwordInput.equals(retypePasswordInput)){
                    Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }
}
