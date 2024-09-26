package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private TextView messageText;   // define message textview variable
    private TextClock textClock;    // define textclock variable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);             // link to Main activity XML

        /* initialize UI elements */
        messageText = findViewById(R.id.main_msg_txt);      // link to message textview in the Main activity XML
        messageText.setText("Today");
        messageText.setTextColor(getResources().getColor(R.color.red));
        messageText.setTextSize(30);

        textClock = findViewById(R.id.main_text_clock);     // link to textclock in the Main activity XML
        textClock.setFormat12Hour("hh:mm:ss a");
        textClock.setTextSize(30);
    }
}