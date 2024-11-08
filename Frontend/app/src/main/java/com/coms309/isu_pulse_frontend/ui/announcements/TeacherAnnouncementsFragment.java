package com.coms309.isu_pulse_frontend.ui.announcements;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.adapters.AnnouncementListAdapter;
import com.coms309.isu_pulse_frontend.api.AnnouncementWebSocketClient;
import com.coms309.isu_pulse_frontend.api.FacultyApiService;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;
import com.coms309.isu_pulse_frontend.model.Announcement;
import com.coms309.isu_pulse_frontend.model.Schedule;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TeacherAnnouncementsFragment extends Fragment implements AnnouncementWebSocketClient.WebSocketListener {

    private RecyclerView recyclerView;
    private AnnouncementListAdapter adapter;
    private List<Announcement> announcementList;
    private AnnouncementWebSocketClient webSocketClient;
    private static final String TAG = "TeacherAnnouncementsFragment";
    private EditText announcementContent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");

        View view = inflater.inflate(R.layout.teacher_announcement, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewAnnouncements);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        announcementList = new ArrayList<>();
        adapter = new AnnouncementListAdapter(announcementList, true);
        recyclerView.setAdapter(adapter);

        announcementContent = view.findViewById(R.id.editTextAnnouncementContent);
        Button submitButton = view.findViewById(R.id.buttonSubmitAnnouncement);

        submitButton.setOnClickListener(v -> {
            String content = announcementContent.getText().toString();
            if (!content.isEmpty()) {
                Log.d(TAG, "Submit button clicked with content: " + content);

                // Replace scheduleId with an appropriate ID for the course
                long scheduleId = getScheduleId(); // Ensure this method retrieves a valid ID
                if (scheduleId != -1) {
                    webSocketClient.sendMessage("new_announcement", scheduleId, content);
                    Log.d(TAG, "Announcement sent with content: " + content);
                    announcementContent.setText("");
                } else {
                    Log.e(TAG, "Error: Course ID not available");
                    Toast.makeText(getContext(), "Error: Course ID not available", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.w(TAG, "Empty content, not sending announcement");
                Toast.makeText(getContext(), "Content cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        initializeWebSocket();

        return view;
    }

    private void initializeWebSocket() {
        // Fetch netId and userType from session or context
        String netId = UserSession.getInstance(getContext()).getNetId();
        String userType = UserSession.getInstance(getContext()).getUserType();

        if (netId == null || netId.isEmpty()) {
            Log.e(TAG, "Error: netId is null or empty");
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Initializing WebSocket client with netId: " + netId + " and userType: " + userType);

        webSocketClient = new AnnouncementWebSocketClient(this);
        webSocketClient.connectWebSocket(netId, userType);
    }


    @Override
    public void onMessageReceived(String message) {
        Log.d(TAG, "Received WebSocket message: " + message);

        // Check if the message is a JSON object
        if (message.trim().startsWith("{") && message.trim().endsWith("}")) {
            try {
                JSONObject jsonMessage = new JSONObject(message);
                String action = jsonMessage.optString("action", "unknown");

                if ("new_announcement".equals(action)) {
                    // Process the new announcement as usual
                    String content = jsonMessage.optString("content", "No content");
                    announcementList.add(new Announcement(
                            jsonMessage.optLong("id", -1L),
                            content,
                            jsonMessage.optLong("scheduleId", -1L),
                            jsonMessage.optString("facultyNetId", "No faculty NetId"),
                            jsonMessage.optString("timestamp", "No timestamp"),
                            jsonMessage.optString("extraField", "No extra field")
                    ));
                    adapter.notifyDataSetChanged();
                } else {
                    Log.w(TAG, "Unknown action received: " + action);
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing WebSocket message", e);
            }
        } else {
            // Handle non-JSON messages, like "Welcome batinov!"
            Log.d(TAG, "Received non-JSON message: " + message);
        }
    }

    private long getScheduleId() {
        // Assume courseId is passed as an argument from CourseDetailFragment or stored in a class variable
        long courseId = getArguments() != null ? getArguments().getLong("courseId", -1) : -1;

        if (courseId == -1) {
            Log.e(TAG, "Invalid course ID");
            return -1;
        }

        String netId = UserSession.getInstance(getContext()).getNetId();  // Ensure netId is available in the user session
        FacultyApiService apiService = new FacultyApiService(getContext());

        apiService.getFacultySchedules(netId, new FacultyApiService.ScheduleResponseListener() {
            @Override
            public void onResponse(List<Schedule> schedules) {
                for (Schedule schedule : schedules) {
                    if (schedule.getCourse().getcId() == courseId) {
                        long scheduleId = schedule.getCourse().getcId();
                        Log.d(TAG, "Found Schedule ID: " + scheduleId);
                        onScheduleIdRetrieved(scheduleId);  // Pass scheduleId to another function for processing
                        return;
                    }
                }
                Log.e(TAG, "No schedule found for the given course ID");
                onScheduleIdRetrieved(-1);  // Handle case when no matching schedule is found
            }

            @Override
            public void onError(String message) {
                Log.e(TAG, "Error fetching schedules: " + message);
                onScheduleIdRetrieved(-1);  // Handle errors
            }
        });

        return -1;  // Return a placeholder value; actual ID is retrieved asynchronously
    }

    private void onScheduleIdRetrieved(long scheduleId) {
        if (scheduleId != -1) {
            // Proceed with sending the announcement
            String content = announcementContent.getText().toString();
            webSocketClient.sendMessage("new_announcement", scheduleId, content);
            Log.d(TAG, "Announcement sent with content: " + content);
        } else {
            Toast.makeText(getContext(), "Error: Schedule ID not available", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView called");

        if (webSocketClient != null) {
            webSocketClient.disconnectWebSocket();
        }
    }
}
