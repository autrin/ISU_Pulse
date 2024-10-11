package com.coms309.isu_pulse_frontend.ui.home;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.DialogFragment;
import com.coms309.isu_pulse_frontend.adapters.TaskListAdapter;
import com.coms309.isu_pulse_frontend.adapters.WeeklyCalendarAdapter;
import com.coms309.isu_pulse_frontend.api.TaskApiService;
import com.coms309.isu_pulse_frontend.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private TextView textViewTasksDueTodayTitle;
    private TextView textViewAnnouncementTitle;
    private TaskListAdapter taskAdapter;
    private Button buttonAddTask;

    private List<ListTaskObject> tasksDueToday = new ArrayList<>();
    private List<String> events = new ArrayList<>();
    private List<String> announcements = new ArrayList<>();
    private ListView listviwTasksDueToday;
    private TaskApiService taskApiService;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        textViewAnnouncementTitle = binding.TextViewAnnouncementTitle;
        textViewAnnouncementTitle.setText("Announcements");
        textViewAnnouncementTitle.setTextSize(25);
        textViewAnnouncementTitle.setTypeface(null, Typeface.BOLD);

        textViewTasksDueTodayTitle = binding.textViewTasksDueToday;
        textViewTasksDueTodayTitle.setText("Tasks Due Today");
        textViewTasksDueTodayTitle.setTextSize(25);
        textViewTasksDueTodayTitle.setTypeface(null, Typeface.BOLD);

        RecyclerView recyclerViewCalendar = binding.recyclerViewWeeklyCalendar;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        layoutManager.setReverseLayout(false);
        recyclerViewCalendar.setLayoutManager(layoutManager);

        List<String> days = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
        WeeklyCalendarAdapter calendarAdapter = new WeeklyCalendarAdapter(days, tasksDueToday, events);
        recyclerViewCalendar.setAdapter(calendarAdapter);

        RecyclerView recylcerViewTasksDueToday = binding.recylcerViewTasksDueToday;
        LinearLayoutManager layoutManagerTasks = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recylcerViewTasksDueToday.setLayoutManager(layoutManagerTasks);

        taskApiService = new TaskApiService(getContext());
        taskAdapter = new TaskListAdapter(tasksDueToday, taskApiService); // Pass the TaskApiService instance
        recylcerViewTasksDueToday.setAdapter(taskAdapter);

        buttonAddTask = binding.buttonAddTask;
        buttonAddTask.setText("Add Task");
        buttonAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddTaskDialog();
            }
        });

        populateTasksDue();

        return root;
    }

    private void openAddTaskDialog() {
        AddTaskDialog addTaskDialog = new AddTaskDialog(taskApiService);
        addTaskDialog.show(getChildFragmentManager(), "Add Task Dialog");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void populateTasksDue() {
        taskApiService.getTasksDueToday(new TaskApiService.TaskResponseListener() {
            @Override
            public void onResponse(List<ListTaskObject> tasks) {
                tasksDueToday.clear();
                tasksDueToday.addAll(tasks);
                taskAdapter.notifyDataSetChanged();
                binding.recyclerViewWeeklyCalendar.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                String errorMessage = message != null ? message : "Unknown error";
                Log.e("API Error", errorMessage);
            }
        });
    }
}