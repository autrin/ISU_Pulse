package com.coms309.isu_pulse_frontend.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.model.CourseTask;
import com.coms309.isu_pulse_frontend.model.PersonalTask;
import com.coms309.isu_pulse_frontend.viewholders.ViewHolder;

import java.util.List;

public class WeeklyCalendarAdapter extends RecyclerView.Adapter<WeeklyCalendarAdapter.WeeklyViewHolder> {

    private List<String> days;
    private List<Object> tasksDueToday;
    private List<String> events;

    public WeeklyCalendarAdapter(List<String> days, List<Object> tasksDueToday, List<String> events) {
        this.days = days;
        this.tasksDueToday = tasksDueToday;
        this.events = events;
    }

    @NonNull
    @Override
    public WeeklyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_day, parent, false);
        return new WeeklyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeeklyViewHolder holder, int position) {
        String day = days.get(position);
        holder.textViewDay.setText(day);

        if (!tasksDueToday.isEmpty() && position < tasksDueToday.size()) {
            Object task = tasksDueToday.get(position);
            if (task instanceof CourseTask) {
                holder.textViewTask.setText(((CourseTask) task).getTitle());
            } else if (task instanceof PersonalTask) {
                holder.textViewTask.setText(((PersonalTask) task).getTitle());
            } else {
                holder.textViewTask.setText("Unknown task type");
            }
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

    public static class WeeklyViewHolder extends RecyclerView.ViewHolder implements ViewHolder {
        TextView textViewDay;
        TextView textViewTask;
        TextView textViewEvent;

        public WeeklyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDay = itemView.findViewById(R.id.textViewDay);
            textViewTask = itemView.findViewById(R.id.textViewTasks);
            textViewEvent = itemView.findViewById(R.id.textViewEvents);
        }

        @Override
        public void bind(Object obj) {
            // Implement binding logic if needed
        }
    }
}
