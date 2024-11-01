package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button uploadBtn, imgBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uploadBtn = findViewById(R.id.btnUploadImage);
        imgBtn = findViewById(R.id.btnImageRequest);

        // Added personalized messages for feedback
        Toast.makeText(this, "Welcome! Choose an action to begin.", Toast.LENGTH_SHORT).show();

        /* button click listeners */
        uploadBtn.setOnClickListener(this);
        imgBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnUploadImage) {
            Toast.makeText(this, "Navigating to Image Upload", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, ImageUploadActivity.class));
        } else if (id == R.id.btnImageRequest) {
            Toast.makeText(this, "Requesting Image from Server", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, ImageReqActivity.class));
        }
    }
}
