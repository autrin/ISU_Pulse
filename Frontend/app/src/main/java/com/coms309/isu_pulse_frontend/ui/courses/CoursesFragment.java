package com.coms309.isu_pulse_frontend.ui.courses;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.adapters.CourseListAdapter;
import com.coms309.isu_pulse_frontend.api.AnnouncementWebSocketClient;
import com.coms309.isu_pulse_frontend.api.FacultyApiService;
import com.coms309.isu_pulse_frontend.databinding.FragmentCoursesBinding;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;
import com.coms309.isu_pulse_frontend.model.Announcement;
import com.coms309.isu_pulse_frontend.model.Course;
import com.coms309.isu_pulse_frontend.model.Schedule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CoursesFragment extends Fragment implements AnnouncementWebSocketClient.WebSocketListener {

    private FragmentCoursesBinding binding;
    private RecyclerView recyclerView;
    private CourseListAdapter adapter;
    private List<Course> courses = new ArrayList<>();
    private List<Announcement> courseAnnouncements = new ArrayList<>(); // Temporary list for announcements
    private TextView emptyStateTextView;
    private AnnouncementWebSocketClient announcementClient;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCoursesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Get the user role
        String userRole = UserSession.getInstance(getContext()).getUserType();

        // Set up RecyclerView
        recyclerView = binding.recyclerViewCourses;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CourseListAdapter(courses, userRole, this::navigateToCourseDetail);
        recyclerView.setAdapter(adapter);

        // Empty state view
        emptyStateTextView = binding.emptyStateTextView;

        // Fetch courses from backend
        fetchCoursesFromBackend();

        return root;
    }

    // Fetch courses from backend
    private void fetchCoursesFromBackend() {
        FacultyApiService facultyApiService = new FacultyApiService(getContext());
        String facultyNetId = UserSession.getInstance(getContext()).getNetId();

        facultyApiService.getFacultySchedules(facultyNetId, new FacultyApiService.ScheduleResponseListener() {
            @Override
            public void onResponse(List<Schedule> schedules) {
                courses.clear();
                for (Schedule schedule : schedules) {
                    courses.add(schedule.getCourse());
                }
                adapter.notifyDataSetChanged();

                // Show empty state if no courses are found
                if (courses.isEmpty()) {
                    emptyStateTextView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyStateTextView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), "Error fetching courses: " + message, Toast.LENGTH_SHORT).show();
                emptyStateTextView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });
    }

    // Navigate to CourseDetailFragment
    private void navigateToCourseDetail(Long courseId) {
        String userRole = UserSession.getInstance(getContext()).getUserType();
        if ("FACULTY".equals(userRole)) {
            Bundle args = new Bundle();
            args.putLong("courseId", courseId);

            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_coursesFragment_to_courseDetailFragment, args);
        }
    }

    // WebSocket connection management
    @Override
    public void onStart() {
        super.onStart();
        if (announcementClient == null) {
            String netId = UserSession.getInstance(getContext()).getNetId();
            String userType = UserSession.getInstance(getContext()).getUserType();

            announcementClient = new AnnouncementWebSocketClient(this);
            announcementClient.connectWebSocket(netId, userType);
        }
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

    @Override
    public void onMessageReceived(String message) {
        try {
            // Try to parse the message as a JSON object
            JSONObject jsonMessage = new JSONObject(message);
            String action = jsonMessage.getString("action");

            switch (action) {
                case "history":
                    // Handle announcement history
                    handleHistoryAction(jsonMessage);
                    break;
                case "new":
                    // Handle a new announcement
                    handleNewAnnouncement(jsonMessage);
                    break;
                case "confirmation":
                    String confirmationMessage = jsonMessage.getString("message");
                    Log.d("WebSocket", "Confirmation: " + confirmationMessage);
                    break;
                case "error":
                    String errorMessage = jsonMessage.getString("message");
                    Log.e("WebSocket", "Error: " + errorMessage);
                    break;
                default:
                    Log.w("WebSocket", "Unknown action: " + action);
                    break;
            }
        } catch (JSONException e) {
            // If parsing fails, it may be a simple text message
            Log.d("WebSocket", "Received non-JSON message: " + message);
        }
    }

    // Handle the "history" action
    private void handleHistoryAction(JSONObject jsonMessage) throws JSONException {
        JSONArray announcementsArray = jsonMessage.getJSONArray("announcements");
        courseAnnouncements.clear();

        // Parse announcements and add them to the list
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
            courseAnnouncements.add(announcement);
        }

        Log.d("WebSocket", "Received announcement history: " + courseAnnouncements.size() + " items");
    }

    // Handle the "new" action
    private void handleNewAnnouncement(JSONObject jsonMessage) throws JSONException {
        JSONObject newAnnouncementJson = jsonMessage.getJSONObject("announcement");
        Announcement newAnnouncement = new Announcement(
                newAnnouncementJson.getLong("id"),
                newAnnouncementJson.getString("content"),
                newAnnouncementJson.getLong("scheduleId"),
                newAnnouncementJson.getString("facultyNetId"),
                newAnnouncementJson.getString("timestamp"),
                ""
        );

        courseAnnouncements.add(0, newAnnouncement);  // Add to top of the list for new announcements
        Log.d("WebSocket", "New announcement received: " + newAnnouncement.getContent());
    }
}
