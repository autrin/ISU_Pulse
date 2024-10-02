package com.coms309.isu_pulse_frontend;

import com.coms309.isu_pulse_frontend.PasswordHasher;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class LoginActivity  extends AppCompatActivity{
    private EditText netId;
    private EditText passWord;
    private TextView enter;
    private TextView signup;
//    private TextView forgotPassword;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.log_in);

        netId = findViewById(R.id.netid_isu_pulse);
        passWord = findViewById(R.id.password_isu_pulse);
        enter = findViewById(R.id.enter_isu_pulse);
        signup = findViewById(R.id.sign_up_isu_pulse);
//        forgotPassword = findViewById(R.id.forgot_password_isu_pulse);

        enter.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String netIdInput =  netId.getText().toString();
                String passWordInput = passWord.getText().toString();
                String hashPassword = PasswordHasher.hashPassword(passWordInput);

                if (netIdInput.isEmpty() || passWordInput.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                }
                else if (passWordInput.length() < 8){
                    Toast.makeText(LoginActivity.this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(LoginActivity.this, "Welcome, " + netIdInput, Toast.LENGTH_SHORT).show();
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

}