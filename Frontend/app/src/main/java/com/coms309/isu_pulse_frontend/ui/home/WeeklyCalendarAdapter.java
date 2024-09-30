package com.coms309.isu_pulse_frontend.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coms309.isu_pulse_frontend.R;

import java.util.List;

public class WeeklyCalendarAdapter extends RecyclerView.Adapter<WeeklyCalendarAdapter.ViewHolder> {

    private final List<String> days;
    private final List<String> tasks;
    private final List<String> events;

    public WeeklyCalendarAdapter(List<String> days, List<String> tasks, List<String> events) {
        this.days = days;
        this.tasks = tasks;
        this.events = events;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_day, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textViewDay.setText(days.get(position));
        holder.textViewTasks.setText("Tasks: " + tasks.get(position));
        holder.textViewEvents.setText("Events: " + events.get(position));
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewDay;
        public TextView textViewTasks;
        public TextView textViewEvents;

        public ViewHolder(View view) {
            super(view);
            textViewDay = view.findViewById(R.id.textViewDay);
            textViewTasks = view.findViewById(R.id.textViewTasks);
            textViewEvents = view.findViewById(R.id.textViewEvents);
        }
    }
}