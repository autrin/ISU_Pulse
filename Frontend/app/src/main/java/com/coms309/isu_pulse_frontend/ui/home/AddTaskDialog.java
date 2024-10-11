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
import com.coms309.isu_pulse_frontend.api.TaskApiService;

public class AddTaskDialog extends DialogFragment {

    private TaskApiService taskApiService;
    private TaskListAdapter taskListAdapter;
    private HomeFragment homeFragment;

    public AddTaskDialog(TaskApiService taskApiService, TaskListAdapter taskListAdapter, HomeFragment homeFragment) {
        this.taskApiService = taskApiService;
        this.taskListAdapter = taskListAdapter;
        this.homeFragment = homeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_task, container, false);

        EditText editTextTitle = view.findViewById(R.id.editTextTitle);
        EditText editTextDescription = view.findViewById(R.id.editTextDescription);
        EditText editTextDueDate = view.findViewById(R.id.editTextDueDate);
        Button buttonSubmit = view.findViewById(R.id.buttonSubmit);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTitle.getText().toString();
                String description = editTextDescription.getText().toString();
                String dueDateString = editTextDueDate.getText().toString();

                PersonalTask newTask = new PersonalTask(title, description, dueDateString, "n001");
                taskApiService.createPersonalTask(newTask);
                homeFragment.addNewTask(newTask);
                dismiss();
            }
        });

        return view;
    }
}