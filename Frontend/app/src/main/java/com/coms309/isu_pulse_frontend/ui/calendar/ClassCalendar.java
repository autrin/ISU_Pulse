package com.coms309.isu_pulse_frontend.ui.calendar;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.coms309.isu_pulse_frontend.R;

public class ClassCalendar extends AppCompatActivity {
    private GridLayout calendarGrid;
    private TextView hourLabel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);
        calendarGrid = findViewById(R.id.calendar_grid);
        for (int hour = 8; hour <= 19; hour++) {                 // 8 AM to 6 PM
            hourLabel = new TextView(this);
            if (hour >= 12) hourLabel.setText(hour + " PM");
            else hourLabel.setText(hour + " AM");
            hourLabel.setGravity(Gravity.CENTER);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.rowSpec = GridLayout.spec(hour - 8 + 1); // Row for this hour
            params.columnSpec = GridLayout.spec(0);         // Column for hours
            hourLabel.setLayoutParams(params);
            hourLabel.setPadding(10, 10, 10, 70);
            calendarGrid.addView(hourLabel);
        }

    }


}
