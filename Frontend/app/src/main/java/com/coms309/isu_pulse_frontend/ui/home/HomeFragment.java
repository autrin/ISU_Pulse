package com.coms309.isu_pulse_frontend.ui.home;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coms309.isu_pulse_frontend.adapters.AnnouncementListAdapter;
import com.coms309.isu_pulse_frontend.adapters.TaskListAdapter;
import com.coms309.isu_pulse_frontend.adapters.WeeklyCalendarAdapter;
import com.coms309.isu_pulse_frontend.api.TaskApiService;
import com.coms309.isu_pulse_frontend.databinding.FragmentHomeBinding;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;
import com.coms309.isu_pulse_frontend.model.Announcement;
import com.coms309.isu_pulse_frontend.model.PersonalTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private TextView textViewTasksDueTodayTitle;
    private TextView textViewAnnouncementTitle;
    private TaskListAdapter taskAdapter;
    private Button buttonAddTask;
    private RecyclerView recyclerViewCalendar;
    private RecyclerView recyclerViewTasksDueToday;
    private RecyclerView recyclerViewAnnouncements;
    private AnnouncementListAdapter announcementAdapter;

    private List<Object> tasksDueToday = new ArrayList<>();
    private List<String> events = new ArrayList<>();
    private List<Announcement> announcements = new ArrayList<>(); // Use Announcement model for announcements
    private TaskApiService taskApiService;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        // Set Dashboard title based on user role
        String userRole = UserSession.getInstance(getContext()).getUserRole();
        TextView dashboardTitle = binding.dashboardTitle;
        if ("TEACHER".equals(userRole)) {
            dashboardTitle.setText("Teacher Dashboard");
        } else {
            dashboardTitle.setText("Student Dashboard");
        }
        // Set up Announcement title
        textViewAnnouncementTitle = binding.announcementTitle;
        textViewAnnouncementTitle.setText("Announcements");
        textViewAnnouncementTitle.setTextSize(25);
        textViewAnnouncementTitle.setTypeface(null, Typeface.BOLD);

        // Set up Tasks Due Today title
        textViewTasksDueTodayTitle = binding.textViewTasksDueToday;
        textViewTasksDueTodayTitle.setText("Tasks Due Today");
        textViewTasksDueTodayTitle.setTextSize(25);
        textViewTasksDueTodayTitle.setTypeface(null, Typeface.BOLD);

        // Set up Weekly Calendar RecyclerView
        recyclerViewCalendar = binding.recyclerViewWeeklyCalendar;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCalendar.setLayoutManager(layoutManager);
        List<String> days = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
        WeeklyCalendarAdapter calendarAdapter = new WeeklyCalendarAdapter(days, tasksDueToday, events);
        recyclerViewCalendar.setAdapter(calendarAdapter);

        // Set up Tasks Due Today RecyclerView
        recyclerViewTasksDueToday = binding.recyclerViewTasksDueToday;
        LinearLayoutManager layoutManagerTasks = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewTasksDueToday.setLayoutManager(layoutManagerTasks);
        taskApiService = new TaskApiService(getContext());
        taskAdapter = new TaskListAdapter(tasksDueToday, taskApiService, calendarAdapter);
        recyclerViewTasksDueToday.setAdapter(taskAdapter);

        // Add Task Button setup
        buttonAddTask = binding.buttonAddTask;
        buttonAddTask.setText("Add Task");
        buttonAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddTaskDialog();
            }
        });

        populateTasksDue();

        // Set up Announcements RecyclerView (without populating announcements from API for now)
        recyclerViewAnnouncements = binding.recyclerViewAnnouncements;
        LinearLayoutManager layoutManagerAnnouncements = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewAnnouncements.setLayoutManager(layoutManagerAnnouncements);
        announcementAdapter = new AnnouncementListAdapter(announcements, "TEACHER".equals(UserSession.getInstance(getContext()).getUserRole()));
        recyclerViewAnnouncements.setAdapter(announcementAdapter);

        // Commented out for now to avoid errors related to missing methods in TaskApiService
        populateAnnouncements();

        return root;
    }

    private void openAddTaskDialog() {
        AddTaskDialog addTaskDialog = new AddTaskDialog(taskApiService, taskAdapter, this);
        addTaskDialog.show(getChildFragmentManager(), "Add Task Dialog");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void populateTasksDue() {
        taskApiService.getTasksIn2days(new TaskApiService.TaskResponseListener() {
            @Override
            public void onResponse(List<Object> tasks) {
                tasksDueToday.clear();
                tasksDueToday.addAll(tasks);
                taskAdapter.notifyDataSetChanged();
                recyclerViewCalendar.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                Log.e("API Error", message != null ? message : "Unknown error");
            }
        });
    }

    // Placeholder populateAnnouncements method to avoid errors
    private void populateAnnouncements() {
        /**
         *         // Call to the API to get announcements for the current course/schedule
         *         taskApiService.getAnnouncements(new TaskApiService.AnnouncementResponseListener() {
         *             @Override
         *             public void onResponse(List<Announcement> fetchedAnnouncements) {
         *                 announcements.clear();
         *                 announcements.addAll(fetchedAnnouncements);
         *                 announcementAdapter.notifyDataSetChanged();
         *             }
         *
         *             @Override
         *             public void onError(String message) {
         *                 Log.e("API Error", message != null ? message : "Unknown error");
         *             }
         *         });
         */

        // Temporary placeholder code
        announcements.clear();
        // For now, manually add a sample announcement to avoid UI breakage
        announcements.add(new Announcement(1L, "Sample Announcement", 1L, "facultyNetId", "2024-11-07T10:00:00.000-06:00", "CourseName"));
        announcementAdapter.notifyDataSetChanged();
    }

    public void addNewTask(PersonalTask newTask) {
        tasksDueToday.add(newTask);
        taskAdapter.notifyItemInserted(tasksDueToday.size() - 1);
        recyclerViewTasksDueToday.scrollToPosition(tasksDueToday.size() - 1);
        recyclerViewCalendar.getAdapter().notifyDataSetChanged();
    }
}
