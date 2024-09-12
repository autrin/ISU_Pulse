package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView messageText;   // define message textview variable
    private TextView usernameText;  // define username textview variable
    private Button loginButton;     // define login button variable
    private Button signupButton;    // define signup button variable
    private TextView isuText;       // define isuText TextView variable
    private TextView ronaldoText;   // define ronaldoText TextView variable
    private Button backButton;      // Back button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);             // link to Main activity XML

        /* initialize UI elements */
        messageText = findViewById(R.id.main_msg_txt);      // link to message textview in the Main activity XML
        usernameText = findViewById(R.id.main_username_txt);// link to username textview in the Main activity XML
        loginButton = findViewById(R.id.main_login_btn);    // link to login button in the Main activity XML
        signupButton = findViewById(R.id.main_signup_btn);  // link to signup button in the Main activity XML
        isuText = findViewById(R.id.isu_student_txt);       // link to isuText TextView in the Main activity XML
        ronaldoText = findViewById(R.id.ronaldo_fan_txt);   // link to ronaldoText TextView in the Main activity XML

        /* extract data passed into this activity from another activity */
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            messageText.setText("Home Page");
            usernameText.setVisibility(View.INVISIBLE);
            isuText.setVisibility(View.INVISIBLE);
            ronaldoText.setVisibility(View.INVISIBLE); // set TextViews invisible initially
        } else {
            messageText.setText("Welcome");
            usernameText.setText(extras.getString("USERNAME")); // this will come from LoginActivity
            loginButton.setVisibility(View.INVISIBLE);          // set login button invisible
            signupButton.setVisibility(View.INVISIBLE);         // set signup button invisible
            isuText.setVisibility(View.VISIBLE);                // show isuText TextView
            ronaldoText.setVisibility(View.VISIBLE);            // show ronaldoText TextView
        }

        /* click listener on login button pressed */
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* when login button is pressed, use intent to switch to Login Activity */
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        /* click listener on signup button pressed */
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* when signup button is pressed, use intent to switch to Signup Activity */
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        /* click listener on isuText pressed */
        isuText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* change the layout to isu.xml */
                setContentView(R.layout.isu);

                /* back button functionality for isu.xml */
                Button backButton = findViewById(R.id.back_isu_btn);
                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /* Go back to Main layout */
                        setContentView(R.layout.ronaldo);
                    }
                });
            }
        });

        /* click listener on ronaldoText pressed */
        ronaldoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* change the layout to ronaldo.xml */
                setContentView(R.layout.ronaldo);

                /* back button functionality for ronaldo.xml */
                Button backButton = findViewById(R.id.back_ronaldo_btn);
                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /* Go back to Main layout */
                        setContentView(R.layout.isu);
                    }
                });
            }
        });
    }
}
