package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity{

    private Button connectBtn1, connectBtn2;
    private EditText serverEtx1, usernameEtx1, serverEtx2, usernameEtx2;
    private TextView status1Tv, status2Tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectBtn1 = findViewById(R.id.connectBtn);
        connectBtn2 = findViewById(R.id.connectBtn2);
        serverEtx1 = findViewById(R.id.serverEdt);
        usernameEtx1 = findViewById(R.id.unameEdt);
        serverEtx2 = findViewById(R.id.serverEdt2);
        usernameEtx2 = findViewById(R.id.unameEdt2);
        status1Tv = findViewById(R.id.status1TextView); // Display connection status
        status2Tv = findViewById(R.id.status2TextView); // Display connection status

        connectBtn1.setOnClickListener(view -> {
            String serverUrl = serverEtx1.getText().toString() + "/" + usernameEtx1.getText().toString();
            Intent serviceIntent = new Intent(this, WebSocketService.class);
            serviceIntent.setAction("CONNECT");
            serviceIntent.putExtra("key", "chat1");
            serviceIntent.putExtra("url", serverUrl);
            startService(serviceIntent);
            status1Tv.setText("Connecting to chat1...");
            startActivity(new Intent(this, ChatActivity1.class));
        });

        connectBtn2.setOnClickListener(view -> {
            String serverUrl = serverEtx2.getText().toString() + "/" + usernameEtx2.getText().toString();
            Intent serviceIntent = new Intent(this, WebSocketService.class);
            serviceIntent.setAction("CONNECT");
            serviceIntent.putExtra("key", "chat2");
            serviceIntent.putExtra("url", serverUrl);
            startService(serviceIntent);
            status2Tv.setText("Connecting to chat2...");
            startActivity(new Intent(this, ChatActivity2.class));
        });
    }
}
