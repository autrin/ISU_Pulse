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
import com.coms309.isu_pulse_frontend.api.AnnouncementResponseListener;
import com.coms309.isu_pulse_frontend.api.AnnouncementWebSocketClient;
import com.coms309.isu_pulse_frontend.api.FacultyApiService;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;
import com.coms309.isu_pulse_frontend.model.Announcement;
import com.coms309.isu_pulse_frontend.model.Schedule;

import org.json.JSONArray;
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
    private long courseId = 2L; // hardcoded course ID for testing
    private long scheduleId = 7L; // hardcoded schedule ID for testing

    public static TeacherAnnouncementsFragment newInstance(long scheduleId) {
        TeacherAnnouncementsFragment fragment = new TeacherAnnouncementsFragment();
        Bundle args = new Bundle();
        args.putLong("scheduleId", scheduleId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Fetch the scheduleId from the arguments
        if (getArguments() != null) {
            scheduleId = getArguments().getLong("scheduleId", -1);
        }
        // Fetch the scheduleId
        getScheduleId();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        if (getArguments() != null) {
            scheduleId = getArguments().getLong("scheduleId", 7L); // hardcoded schedule ID for testing
        }

        View view = inflater.inflate(R.layout.teacher_announcement, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewAnnouncements);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        announcementList = new ArrayList<>();
        adapter = new AnnouncementListAdapter(getContext(), announcementList, true);
        recyclerView.setAdapter(adapter);
        // Load announcements for this schedule when the fragment starts
        if (scheduleId != -1) {
            loadAnnouncementsForSchedule(scheduleId);
        } else {
            Log.e("TeacherAnnouncementsFragment", "Error: Schedule ID is not available");
            scheduleId = 7L; // hardcoded schedule ID for testing
        }
        announcementContent = view.findViewById(R.id.editTextAnnouncementContent);
        Button submitButton = view.findViewById(R.id.buttonSubmitAnnouncement);

        submitButton.setOnClickListener(v -> {
            String content = announcementContent.getText().toString();
            if (!content.isEmpty()) {
                Log.d(TAG, "Submit button clicked with content: " + content);

                if (scheduleId != -1) {
//                    webSocketClient.sendActionMessage("new_announcement", scheduleId, content, null);
                    // Proceed to send the announcement
                    webSocketClient.postAnnouncement(scheduleId, content);
                    Toast.makeText(getContext(), "Announcement sent!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Announcement sent with content: " + content);
//                    announcementContent.setText("");
                } else {
                    Log.e(TAG, "Error: Schedule ID not available yet");
                    Toast.makeText(getContext(), "Schedule ID not available yet", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.w(TAG, "Empty content, not sending announcement");
                Toast.makeText(getContext(), "Content cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        initializeWebSocket();

        return view;
    }

    private void loadAnnouncementsForSchedule(long scheduleId) {
        String netId = UserSession.getInstance(getContext()).getNetId();  // Retrieve netId dynamically
        FacultyApiService apiService = new FacultyApiService(getContext());
        apiService.getAnnouncementsBySchedule(scheduleId, netId, new AnnouncementResponseListener() {
            @Override
            public void onResponse(List<Announcement> announcements) {
                // Update the announcement list and adapter
                announcementList.clear();
                announcementList.addAll(announcements);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), "Error loading announcements: " + message, Toast.LENGTH_SHORT).show();
            }
        });
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
        long courseId = getArguments() != null ? getArguments().getLong("courseId", 2L) : 2L; // hardcoded courseId for testing. default should be -1

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
            webSocketClient.sendActionMessage("new_announcement", scheduleId, content, 0);
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

    @Override
    public void onStart() {
        super.onStart();

        // Initialize the WebSocket client
        AnnouncementWebSocketClient webSocketClient = UserSession.getInstance(getContext()).getWebSocketClient();
        if (webSocketClient != null) {
            webSocketClient.setListener(new AnnouncementWebSocketClient.WebSocketListener() {
                @Override
                public void onMessageReceived(String message) {
                    // Handle incoming WebSocket messages
                    handleWebSocketMessage(message);
                }
            });
        } else {
            Log.e(TAG, "WebSocket client is not initialized");
            Toast.makeText(getContext(), "WebSocket client is not initialized", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleWebSocketMessage(String message) {
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
            Log.e(TAG, "Received non-JSON message: " + message, e);
        }
    }

    // Example functions for handling each action:
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
        adapter.notifyDataSetChanged();
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
        adapter.notifyItemInserted(0);
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

}
