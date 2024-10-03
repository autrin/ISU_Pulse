package com.coms309.isu_pulse_frontend.ui.home;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coms309.isu_pulse_frontend.adapters.TaskListAdapter;
import com.coms309.isu_pulse_frontend.adapters.WeeklyCalendarAdapter;
import com.coms309.isu_pulse_frontend.api.ApiService;
import com.coms309.isu_pulse_frontend.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private TextView textViewTasksDueTodayTitle;
    private TextView textViewAnnouncementTitle;
    private TaskListAdapter taskAdapter;

    private List<ListTaskObject> tasksDueToday = new ArrayList<>();
    private List<String> events = new ArrayList<>();
    private List<String> announcements = new ArrayList<>();
    private ListView listviwTasksDueToday;
    private ApiService apiService;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        textViewAnnouncementTitle = binding.TextViewAnnouncementTitle;
        textViewAnnouncementTitle.setText("Announcements");
        textViewAnnouncementTitle.setTextSize(25);
        textViewAnnouncementTitle.setTypeface(null, Typeface.BOLD); // make it bold

        textViewTasksDueTodayTitle = binding.textViewTasksDueToday;
        textViewTasksDueTodayTitle.setText("Tasks Due Today");
        textViewTasksDueTodayTitle.setTextSize(25);
        textViewTasksDueTodayTitle.setTypeface(null, Typeface.BOLD);

        // Create a weekly calendar that will show the tasks and events for each day on the calendar
        RecyclerView recyclerView = binding.recyclerViewWeeklyCalendar;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        List<String> days = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
        WeeklyCalendarAdapter adapter = new WeeklyCalendarAdapter(days, tasksDueToday, events);
        recyclerView.setAdapter(adapter);

        taskAdapter = new TaskListAdapter(tasksDueToday); // Create a new adapter with the tasks due today
//        listviwTasksDueToday.setAdapter((android.widget.ListAdapter) taskAdapter);
        // TODO: Fix the display of the tasks due today
        apiService = new ApiService(getContext());
        populateTasksDueToday();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void populateTasksDueToday() {
        apiService.getTasksDueToday(new ApiService.TaskResponseListener() {
            @Override
            public void onResponse(List<ListTaskObject> tasks) {
                // Handle the response and update the UI
                for (ListTaskObject task : tasks) {
                    tasksDueToday.add(task);
                }
                // Notify the adapter that the data has changed
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