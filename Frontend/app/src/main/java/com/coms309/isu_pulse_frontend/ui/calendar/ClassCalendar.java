package com.coms309.isu_pulse_frontend.ui.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import androidx.annotation.NonNull;

import com.coms309.isu_pulse_frontend.R;

public class ClassCalendar {
    GridLayout calendarGrid;

    public View onCreate(@NonNull LayoutInflater inflater,
                         ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.calendar, container, false);
        calendarGrid = root.findViewById(R.id.calendar_grid);



        return root;
    }


}
