package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.java_websocket.handshake.ServerHandshake;

public class MainActivity extends AppCompatActivity implements WebSocketListener{

    private Button connectBtn;
    private EditText serverEtx, usernameEtx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* initialize UI elements */
        connectBtn = (Button) findViewById(R.id.connectBtn);
        serverEtx = (EditText) findViewById(R.id.serverEdt);
        usernameEtx = (EditText) findViewById(R.id.unameEdt);

        /* connect button listener */
// Inside onCreate()
        connectBtn.setOnClickListener(view -> {
            String serverUrl = serverEtx.getText().toString();
            String username = usernameEtx.getText().toString();

            if(serverUrl.isEmpty() || username.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter both server and username!", Toast.LENGTH_SHORT).show();
                return;
            }

            serverUrl = serverUrl + "/" + username;  // Customize URL pattern
            WebSocketManager.getInstance().connectWebSocket(serverUrl);
            WebSocketManager.getInstance().setWebSocketListener(MainActivity.this);

            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

    }


    @Override
    public void onWebSocketMessage(String message) {}

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {}

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {}

    @Override
    public void onWebSocketError(Exception ex) {}
}