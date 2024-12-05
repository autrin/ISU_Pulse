package com.coms309.isu_pulse_frontend.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.api.TaskApiService;
import com.coms309.isu_pulse_frontend.model.CourseTask;
import com.coms309.isu_pulse_frontend.model.PersonalTask;
import com.coms309.isu_pulse_frontend.viewholders.ViewHolder;
import com.coms309.isu_pulse_frontend.ui.home.EditTaskDialog;

import java.util.List;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> {

    private List<Object> taskList;
    private TaskApiService taskApiService;
    private WeeklyCalendarAdapter weekCalendarAdapter;
    public TaskListAdapter(List<Object> taskList, TaskApiService taskApiService, WeeklyCalendarAdapter weekCalendarAdapter) {
        this.taskList = taskList;
        this.taskApiService = taskApiService;
        this.weekCalendarAdapter = weekCalendarAdapter;
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
        Log.d("Task Data", "Position: " + position + ", Task: " + task.toString());  // Log task data

        if (task instanceof CourseTask) {
            CourseTask courseTask = (CourseTask) task;
            holder.title.setText(courseTask.getTitle());
            holder.description.setText(courseTask.getDescription());
            holder.dueDate.setText(courseTask.getDueDate().toString());
        } else if (task instanceof PersonalTask) {
            PersonalTask personalTask = (PersonalTask) task;
            holder.title.setText(personalTask.getTitle());
            holder.description.setText(personalTask.getDescription());

            // Convert dueDate from milliseconds to date string
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = dateFormat.format(new Date(personalTask.getDueDate()));
            holder.dueDate.setText(formattedDate);
        }

        holder.buttonEditTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open edit dialog
                EditTaskDialog editTaskDialog = new EditTaskDialog(taskApiService, task, TaskListAdapter.this, weekCalendarAdapter);
                editTaskDialog.show(((FragmentActivity) v.getContext()).getSupportFragmentManager(), "EditTaskDialog");
            }
        });

        holder.checkBox.setOnCheckedChangeListener(null); // Remove previous listener
        holder.checkBox.setChecked(false);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (task instanceof PersonalTask) {
                        taskApiService.deletePersonalTask((PersonalTask) task, new TaskApiService.TaskResponseListener() {
                            @Override
                            public void onResponse(List<Object> tasks) {
                                removeTask(holder.getAdapterPosition());
                            }

                            @Override
                            public void onError(String message) {
                                Log.e("API Error occurred in TaskListAdapter, type PersonalTask", message);
                            }
                        });
                    } else if (task instanceof CourseTask) {
                        // Handle CourseTask deletion
                    }
                }
            }
        });
    }

    /**
     * else if (task instanceof CourseTask) {
     * //                        taskApiService.deleteCourseTask((CourseTask) task, new TaskApiService.TaskResponseListener() {
     * //                            @Override
     * //                            public void onResponse(List<Object> tasks) {
     * //                                removeTask(holder.getAdapterPosition());
     * //                            }
     * //
     * //                            @Override
     * //                            public void onError(String message) {
     * //                                Log.e("API Error occurred in TaskListAdapter, type CourseTask", message);
     * //                            }
     * //                        });
     *                     }
     *
     */
    public List<Object> getTaskList() {
        return taskList;
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
        public CheckBox checkBox;
        public Button buttonEditTask;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.task_title);
            description = itemView.findViewById(R.id.task_description);
            dueDate = itemView.findViewById(R.id.task_due_date);
            checkBox = itemView.findViewById(R.id.checkBoxTask);
            buttonEditTask = itemView.findViewById(R.id.buttonEditTask);
        }

        @Override
        public void bind(Object obj) {
        }
    }
}
