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
import com.coms309.isu_pulse_frontend.model.PersonalTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A dialog fragment that allows users to edit task details such as title, description, and due date.
 */
public class EditTaskDialog extends DialogFragment {

    private TaskApiService taskApiService;
    private Object task;
    private TaskListAdapter taskListAdapter;
    private WeeklyCalendarAdapter weekCalendarAdapter;

    /**
     * Constructs an instance of {@link EditTaskDialog}.
     *
     * @param taskApiService      the API service for task-related operations
     * @param task                the task to be edited (can be of type {@link PersonalTask} or other)
     * @param taskListAdapter     the adapter for updating the task list view
     * @param weekCalendarAdapter the adapter for updating the weekly calendar view
     */
    public EditTaskDialog(TaskApiService taskApiService, Object task, TaskListAdapter taskListAdapter, WeeklyCalendarAdapter weekCalendarAdapter) {
        this.taskApiService = taskApiService;
        this.task = task;
        this.taskListAdapter = taskListAdapter;
        this.weekCalendarAdapter = weekCalendarAdapter;
    }

    /**
     * Inflates the layout for the dialog and initializes the view components.
     *
     * @param inflater  the {@link LayoutInflater} to use for inflating the layout
     * @param container the parent {@link ViewGroup} into which the fragment's view is to be added
     * @param savedInstanceState the saved instance state (if any)
     * @return the root {@link View} of the inflated layout
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_task, container, false);

        // Initialize UI components
        EditText editTextTitle = view.findViewById(R.id.editTextTitle);
        EditText editTextDescription = view.findViewById(R.id.editTextDescription);
        EditText editTextDueDate = view.findViewById(R.id.editTextDueDate);
        Button buttonSubmit = view.findViewById(R.id.buttonSubmit);

        // Populate fields if the task is of type PersonalTask
        if (task instanceof PersonalTask) {
            PersonalTask personalTask = (PersonalTask) task;
            editTextTitle.setText(personalTask.getTitle());
            editTextDescription.setText(personalTask.getDescription());
            editTextDueDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(personalTask.getDueDate())));
        }

        // Set up the submit button's click listener
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input from the fields
                String title = editTextTitle.getText().toString();
                String description = editTextDescription.getText().toString();
                String dueDateString = editTextDueDate.getText().toString();
                Long dueDateTimestamp = null;

                // Parse the due date input
                try {
                    dueDateTimestamp = new SimpleDateFormat("yyyy-MM-dd").parse(dueDateString).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // Update the task if it is of type PersonalTask
                if (task instanceof PersonalTask) {
                    PersonalTask personalTask = (PersonalTask) task;
                    personalTask.setTitle(title);
                    personalTask.setDescription(description);
                    personalTask.setDueDate(dueDateTimestamp);

                    // Update the task via the API and refresh the adapters
                    taskApiService.updatePersonalTask(personalTask);
                    taskListAdapter.notifyDataSetChanged();
                    weekCalendarAdapter.notifyDataSetChanged();
                }

                // Dismiss the dialog
                dismiss();
            }
        });

        return view;
    }
}
