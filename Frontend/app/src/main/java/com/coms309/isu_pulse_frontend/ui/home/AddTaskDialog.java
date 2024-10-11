package com.coms309.isu_pulse_frontend.ui.home;

import android.app.Dialog;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;

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
        EditText editTextTaskType = view.findViewById(R.id.editTextTaskType);
        EditText editTextSection = view.findViewById(R.id.editTextSection);
        EditText editTextCourseCode = view.findViewById(R.id.editTextCourseCode);
        EditText editTextCourseTitle = view.findViewById(R.id.editTextCourseTitle);
        EditText editTextCourseDescription = view.findViewById(R.id.editTextCourseDescription);
        EditText editTextCourseCredits = view.findViewById(R.id.editTextCourseCredits);
        EditText editTextCourseNumSections = view.findViewById(R.id.editTextCourseNumSections);
        EditText editTextDepartmentName = view.findViewById(R.id.editTextDepartmentName);
        EditText editTextDepartmentLocation = view.findViewById(R.id.editTextDepartmentLocation);
        EditText editTextDepartmentId = view.findViewById(R.id.editTextDepartmentId);
        EditText editTextcId = view.findViewById(R.id.editTextcId);
        Button buttonSubmit = view.findViewById(R.id.buttonSubmit);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTitle.getText().toString();
                String description = editTextDescription.getText().toString();
                String dueDateString = editTextDueDate.getText().toString();
                String taskType = editTextTaskType.getText().toString();
                int section = Integer.parseInt(editTextSection.getText().toString());
                String courseCode = editTextCourseCode.getText().toString();
                String courseTitle = editTextCourseTitle.getText().toString();
                String courseDescription = editTextCourseDescription.getText().toString();
                int courseCredits = Integer.parseInt(editTextCourseCredits.getText().toString());
                int courseNumSections = Integer.parseInt(editTextCourseNumSections.getText().toString());
                String departmentName = editTextDepartmentName.getText().toString();
                String departmentLocation = editTextDepartmentLocation.getText().toString();
                int departmentId = Integer.parseInt(editTextDepartmentId.getText().toString());
                int cId = Integer.parseInt(editTextcId.getText().toString());

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date sqlDueDate = null;
                try {
                    java.util.Date utilDueDate = dateFormat.parse(dueDateString);
                    sqlDueDate = new Date(utilDueDate.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Department department = new Department(departmentName, departmentLocation, departmentId);
                Course course = new Course(courseCode, courseTitle, courseDescription, courseCredits, courseNumSections, department, cId);
                ListTaskObject newTask = new ListTaskObject("newTask", section, title, description, sqlDueDate, taskType, course, department);

                taskApiService.createTask(newTask);
                homeFragment.addNewTask(newTask); // Add the new task to the HomeFragment
                dismiss();
            }
        });

        return view;
    }
}