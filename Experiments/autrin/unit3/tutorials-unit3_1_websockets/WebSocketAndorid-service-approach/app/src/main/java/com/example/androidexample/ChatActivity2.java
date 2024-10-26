package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatActivity2 extends AppCompatActivity {

    private Button sendBtn;
    private EditText msgEtx;
    private TextView msgTv, statusTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat2);

        sendBtn = findViewById(R.id.sendBtn2);
        msgEtx = findViewById(R.id.msgEdt2);
        msgTv = findViewById(R.id.tx2);
        statusTv = findViewById(R.id.statusTextView); // Add TextView for status

        sendBtn.setOnClickListener(v -> {
            Intent intent = new Intent("SendWebSocketMessage");
            intent.putExtra("key", "chat2");
            intent.putExtra("message", msgEtx.getText().toString());
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        });
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String key = intent.getStringExtra("key");
            if ("chat2".equals(key)){
                String message = intent.getStringExtra("message");
                String timestamp = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                runOnUiThread(() -> {
                    msgTv.append("\n" + timestamp + ": " + message);
                });
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,
                new IntentFilter("WebSocketMessageReceived"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
    }
}
