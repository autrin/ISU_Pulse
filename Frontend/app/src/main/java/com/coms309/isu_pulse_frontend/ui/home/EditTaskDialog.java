package com.coms309.isu_pulse_frontend.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.adapters.TaskListAdapter;
import com.coms309.isu_pulse_frontend.adapters.WeeklyCalendarAdapter;
import com.coms309.isu_pulse_frontend.api.TaskApiService;
import com.coms309.isu_pulse_frontend.ui.home.PersonalTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class EditTaskDialog extends DialogFragment {

    private TaskApiService taskApiService;
    private Object task;
    private TaskListAdapter taskListAdapter;
    private WeeklyCalendarAdapter weekCalendarAdapter;

    public EditTaskDialog(TaskApiService taskApiService, Object task, TaskListAdapter taskListAdapter, WeeklyCalendarAdapter weekCalendarAdapter) {
        this.taskApiService = taskApiService;
        this.task = task;
        this.taskListAdapter = taskListAdapter;
        this.weekCalendarAdapter = weekCalendarAdapter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_task, container, false);

        EditText editTextTitle = view.findViewById(R.id.editTextTitle);
        EditText editTextDescription = view.findViewById(R.id.editTextDescription);
        EditText editTextDueDate = view.findViewById(R.id.editTextDueDate);
        Button buttonSubmit = view.findViewById(R.id.buttonSubmit);

        if (task instanceof PersonalTask) {
            PersonalTask personalTask = (PersonalTask) task;
            editTextTitle.setText(personalTask.getTitle());
            editTextDescription.setText(personalTask.getDescription());
            editTextDueDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(personalTask.getDueDate() * 1000)));
        }

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTitle.getText().toString();
                String description = editTextDescription.getText().toString();
                String dueDateString = editTextDueDate.getText().toString();
                long dueDateTimestamp = 0;
                try {
                    dueDateTimestamp = new SimpleDateFormat("yyyy-MM-dd").parse(dueDateString).getTime() / 1000;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (task instanceof PersonalTask) {
                    PersonalTask personalTask = (PersonalTask) task;
                    personalTask.setTitle(title);
                    personalTask.setDescription(description);
                    personalTask.setDueDate(dueDateTimestamp);
                    taskApiService.updatePersonalTask(personalTask);
                    taskListAdapter.notifyDataSetChanged();
                    weekCalendarAdapter.notifyDataSetChanged();
                }

                dismiss();
            }
        });

        return view;
    }
}