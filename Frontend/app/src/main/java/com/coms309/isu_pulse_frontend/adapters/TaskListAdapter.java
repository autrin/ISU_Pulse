package com.coms309.isu_pulse_frontend.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.ui.home.ListTaskObject;
import com.coms309.isu_pulse_frontend.viewholders.ViewHolder;

import java.util.List;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> {
    private List<ListTaskObject> tasks;

    public TaskListAdapter(List<ListTaskObject> tasks) {
        this.tasks = tasks;
    }


    @NonNull
    @Override
    public TaskListAdapter.TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull TaskListAdapter.TaskViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
    public static class TaskViewHolder extends RecyclerView.ViewHolder implements ViewHolder {
        TextView textViewTitle;
        TextView textViewDescription;
        TextView textViewDate;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewDate = itemView.findViewById(R.id.textViewDate);
        }

        @Override
        public void bind(Object obj) {

        }
    }
}
