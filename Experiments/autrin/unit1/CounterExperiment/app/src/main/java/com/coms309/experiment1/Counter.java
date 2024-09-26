package com.coms309.experiment1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Counter extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.counter);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button increament = findViewById(R.id.counter_counterIncreament);
        increament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // This is where we write the logic for the button
                // increament the counter
                TextView counter = findViewById(R.id.counter_counterText);
                int count = Integer.parseInt(counter.getText().toString());
                count++;
                counter.setText(String.valueOf(count));
            }
        });
        Button decreament = findViewById(R.id.counter_back);
        decreament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // decreament the counter
                TextView counter = findViewById(R.id.counter_counterText);
                int count = Integer.parseInt(counter.getText().toString());
                count--;
                counter.setText(String.valueOf(count));
            }
        });

        Button home = findViewById(R.id.button_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // go to the main activity
                finish();
            }
        });
    }

}