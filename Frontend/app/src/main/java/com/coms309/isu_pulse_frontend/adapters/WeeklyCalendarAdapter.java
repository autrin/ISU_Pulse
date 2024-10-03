package com.coms309.isu_pulse_frontend.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coms309.isu_pulse_frontend.R;

import java.util.List;

public class WeeklyCalendarAdapter extends RecyclerView.Adapter<WeeklyCalendarAdapter.ViewHolder> {

    private List<String> days;
    private List<String> tasksDueToday;
    private List<String> events;

    public WeeklyCalendarAdapter(List<String> days, List<String> tasksDueToday, List<String> events) {
        this.days = days;
        this.tasksDueToday = tasksDueToday;
        this.events = events;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_day, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String day = days.get(position);
        holder.textViewDay.setText(day);

        // Check if tasksDueToday and events lists are not empty before accessing them
        if (!tasksDueToday.isEmpty() && position < tasksDueToday.size()) {
            holder.textViewTask.setText(tasksDueToday.get(position));
        } else {
            holder.textViewTask.setText("No tasks");
        }

        if (!events.isEmpty() && position < events.size()) {
            holder.textViewEvent.setText(events.get(position));
        } else {
            holder.textViewEvent.setText("No events");
        }
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDay;
        TextView textViewTask;
        TextView textViewEvent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDay = itemView.findViewById(R.id.textViewDay);
            textViewTask = itemView.findViewById(R.id.textViewTasks);
            textViewEvent = itemView.findViewById(R.id.textViewEvents);
        }
    }
}