package com.coms309.isu_pulse_frontend.ui.home;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coms309.isu_pulse_frontend.adapters.AnnouncementListAdapter;
import com.coms309.isu_pulse_frontend.adapters.TaskListAdapter;
import com.coms309.isu_pulse_frontend.adapters.WeeklyCalendarAdapter;
import com.coms309.isu_pulse_frontend.api.AnnouncementWebSocketClient;
import com.coms309.isu_pulse_frontend.api.TaskApiService;
import com.coms309.isu_pulse_frontend.databinding.FragmentHomeBinding;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;
import com.coms309.isu_pulse_frontend.model.Announcement;
import com.coms309.isu_pulse_frontend.model.PersonalTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment implements AnnouncementWebSocketClient.WebSocketListener {

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
    private List<Announcement> announcementList = new ArrayList<>();
    private TaskApiService taskApiService;
    private AnnouncementWebSocketClient announcementClient;
    private static final String TAG = "HomeFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        // Set Dashboard title based on user role
        String userRole = UserSession.getInstance(getContext()).getUserType();
        TextView dashboardTitle = binding.dashboardTitle;
        if ("FACULTY".equals(userRole)) {
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

        // Set up Announcements RecyclerView (for Home/Dashboard view)
        recyclerViewAnnouncements = binding.recyclerViewAnnouncements;
        LinearLayoutManager layoutManagerAnnouncements = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewAnnouncements.setLayoutManager(layoutManagerAnnouncements);

        // Pass 'false' for the isCourseView parameter since this is the dashboard
        announcementAdapter = new AnnouncementListAdapter(getContext(), announcementList, false);
        recyclerViewAnnouncements.setAdapter(announcementAdapter);

        populateAnnouncements(); // Ensure announcements are fetched via WebSocket

        return root;
    }

    private void openAddTaskDialog() {
        AddTaskDialog addTaskDialog = new AddTaskDialog(taskApiService, taskAdapter, this);
        addTaskDialog.show(getChildFragmentManager(), "Add Task Dialog");
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

    // Remove or modify populateAnnouncements() based on your actual implementation
    private void populateAnnouncements() {
        // Ideally, fetch announcements via WebSocket or REST API
        // For now, using placeholder data
        announcementList.clear();
        announcementList.add(new Announcement(1L, "Sample Announcement", 1L, "facultyNetId", "2024-11-07T10:00:00.000-06:00", "CourseName"));
        announcementAdapter.notifyDataSetChanged();
    }

    public void addNewTask(PersonalTask newTask) {
        tasksDueToday.add(newTask);
        taskAdapter.notifyItemInserted(tasksDueToday.size() - 1);
        recyclerViewTasksDueToday.scrollToPosition(tasksDueToday.size() - 1);
        recyclerViewCalendar.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Access the singleton with Context
        UserSession userSession = UserSession.getInstance(getContext());
        announcementClient = userSession.getWebSocketClient();
        if (announcementClient != null) {
            announcementClient.setListener(this);
            Log.d(TAG, "WebSocket client set as listener.");
            // Optionally, fetch initial announcements if needed
        } else {
            Log.e(TAG, "WebSocket client is not initialized");
//            Toast.makeText(getContext(), "WebSocket client is not initialized", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMessageReceived(String message) {
        try {
            JSONObject jsonMessage = new JSONObject(message);
            String action = jsonMessage.getString("action");

            switch (action) {
                case "history":
                    handleHistoryAction(jsonMessage);
                    break;
                case "new":
                    handleNewAnnouncement(jsonMessage);
                    break;
                case "confirmation":
                    handleConfirmation(jsonMessage);
                    break;
                case "error":
                    handleError(jsonMessage);
                    break;
                default:
                    Log.w(TAG, "Unknown action: " + action);
            }
        } catch (JSONException e) {
            Log.d(TAG, "Received non-JSON message: " + message);
        }
    }

    private void handleHistoryAction(JSONObject jsonMessage) throws JSONException {
        JSONArray announcementsArray = jsonMessage.getJSONArray("announcements");
        announcementList.clear();

        for (int i = 0; i < announcementsArray.length(); i++) {
            JSONObject announcementJson = announcementsArray.getJSONObject(i);
            Announcement announcement = new Announcement(
                    announcementJson.getLong("id"),
                    announcementJson.getString("content"),
                    announcementJson.getLong("scheduleId"),
                    announcementJson.getString("facultyNetId"),
                    announcementJson.getString("timestamp"),
                    ""
            );
            announcementList.add(announcement);
        }

        announcementAdapter.notifyDataSetChanged();
    }

    private void handleNewAnnouncement(JSONObject jsonMessage) throws JSONException {
        JSONObject announcementJson = jsonMessage.getJSONObject("announcement");
        Announcement newAnnouncement = new Announcement(
                announcementJson.getLong("id"),
                announcementJson.getString("content"),
                announcementJson.getLong("scheduleId"),
                announcementJson.getString("facultyNetId"),
                announcementJson.getString("timestamp"),
                ""
        );

        announcementList.add(0, newAnnouncement);
        announcementAdapter.notifyItemInserted(0);
    }

    private void handleConfirmation(JSONObject jsonMessage) throws JSONException {
        String confirmationMessage = jsonMessage.getString("message");
        Toast.makeText(getContext(), "Confirmation: " + confirmationMessage, Toast.LENGTH_SHORT).show();
    }

    private void handleError(JSONObject jsonMessage) throws JSONException {
        String errorMessage = jsonMessage.getString("message");
        Log.e(TAG, "Error: " + errorMessage);
        Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (announcementClient != null) {
            announcementClient.disconnectWebSocket();
            announcementClient = null;
        }
        binding = null; // Avoid memory leaks by releasing the binding reference
    }
}
