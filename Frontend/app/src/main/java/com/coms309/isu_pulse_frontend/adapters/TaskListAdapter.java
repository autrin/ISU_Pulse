package com.coms309.isu_pulse_frontend.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.api.TaskApiService;
import com.coms309.isu_pulse_frontend.ui.home.CourseTask;
import com.coms309.isu_pulse_frontend.ui.home.PersonalTask;
import com.coms309.isu_pulse_frontend.viewholders.ViewHolder;

import java.util.List;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> {

    private List<Object> taskList;
    private TaskApiService taskApiService;

    public TaskListAdapter(List<Object> taskList, TaskApiService taskApiService) {
        this.taskList = taskList;
        this.taskApiService = taskApiService;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Object task = taskList.get(position);
        if (task instanceof CourseTask) {
            CourseTask courseTask = (CourseTask) task;
            holder.title.setText(courseTask.getTitle());
            holder.description.setText(courseTask.getDescription());
            holder.dueDate.setText(courseTask.getDueDate().toString());
        } else if (task instanceof PersonalTask) {
            PersonalTask personalTask = (PersonalTask) task;
            holder.title.setText(personalTask.getTitle());
            holder.description.setText(personalTask.getDescription());
            holder.dueDate.setText(personalTask.getDueDate());
        }

        holder.checkBox.setOnCheckedChangeListener(null); // Remove previous listener
        holder.checkBox.setChecked(false);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && task instanceof PersonalTask) {
                    taskApiService.deletePersonalTask((PersonalTask) task, new TaskApiService.TaskResponseListener() {
                        @Override
                        public void onResponse(List<Object> tasks) {
                            removeTask(holder.getAdapterPosition());
                        }

                        @Override
                        public void onError(String message) {
                            // Handle error
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void updateTasks(List<Object> newTaskList) {
        this.taskList = newTaskList;
        notifyDataSetChanged();
    }

    public void addTask(Object task) {
        this.taskList.add(task);
        notifyItemInserted(taskList.size() - 1);
    }

    public void removeTask(int position) {
        this.taskList.remove(position);
        notifyItemRemoved(position);
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder implements ViewHolder {
        TextView title, description, dueDate;
        CheckBox checkBox;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.task_title);
            description = itemView.findViewById(R.id.task_description);
            dueDate = itemView.findViewById(R.id.task_due_date);
            checkBox = itemView.findViewById(R.id.checkBoxTask);
        }

        @Override
        public void bind(Object obj) {
            // Implement binding logic if needed
        }
    }
}