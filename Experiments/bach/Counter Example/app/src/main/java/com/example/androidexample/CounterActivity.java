package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CounterActivity extends AppCompatActivity {

    private TextView numberTxt;
    private Button increaseBtn;
    private Button decreaseBtn;
    private Button multiplyBtn;
    private Button backBtn;
    private Button resetBtn;
    private Button increaseAmountBtn;
    private Button decreaseAmountBtn;
    private EditText amountInput;

    private long counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);

        // Initialize UI elements
        numberTxt = findViewById(R.id.number);
        increaseBtn = findViewById(R.id.counter_increase_btn);
        decreaseBtn = findViewById(R.id.counter_decrease_btn);
        multiplyBtn = findViewById(R.id.counter_multiply_btn);
        backBtn = findViewById(R.id.counter_back_btn);
        resetBtn = findViewById(R.id.counter_reset_btn);
        increaseAmountBtn = findViewById(R.id.counter_increase_amount_btn);
        decreaseAmountBtn = findViewById(R.id.counter_decrease_amount_btn);
        amountInput = findViewById(R.id.amount_input);

        // Increase button
        increaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberTxt.setText(String.valueOf(++counter));
            }
        });

        // Decrease button
        decreaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberTxt.setText(String.valueOf(--counter));
            }
        });

        // Multiply button
        multiplyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter *= 2;
                numberTxt.setText(String.valueOf(counter));
            }
        });

        // Reset button
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter = 0;
                numberTxt.setText(String.valueOf(counter));
            }
        });

        // Increase by amount button
        increaseAmountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int amount = Integer.parseInt(amountInput.getText().toString());
                    counter += amount;
                    numberTxt.setText(String.valueOf(counter));
                } catch (NumberFormatException e) {
                    Toast.makeText(CounterActivity.this, "Enter a valid number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Decrease by amount button
        decreaseAmountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int amount = Integer.parseInt(amountInput.getText().toString());
                    counter -= amount;
                    numberTxt.setText(String.valueOf(counter));
                } catch (NumberFormatException e) {
                    Toast.makeText(CounterActivity.this, "Enter a valid number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Back button
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CounterActivity.this, MainActivity.class);
                intent.putExtra("NUM", String.valueOf(counter));
                startActivity(intent);
            }
        });
    }
}