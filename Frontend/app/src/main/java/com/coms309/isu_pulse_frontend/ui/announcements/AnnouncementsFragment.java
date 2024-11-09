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
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;
import com.coms309.isu_pulse_frontend.model.Announcement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementsFragment extends Fragment implements AnnouncementWebSocketClient.WebSocketListener {

    private RecyclerView recyclerView;
    private AnnouncementListAdapter adapter;
    private List<Announcement> announcementList;
    private AnnouncementWebSocketClient announcementClient;
    private static final String TAG = "AnnouncementWebSocket";

    public static AnnouncementsFragment newInstance(long courseId) {
        courseId = 2L; // hardcoded course ID for testing
        AnnouncementsFragment fragment = new AnnouncementsFragment();
        Bundle args = new Bundle();
        args.putLong("courseId", courseId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_announcements, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewAnnouncements);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        announcementList = new ArrayList<>();
        adapter = new AnnouncementListAdapter(getContext(), announcementList, false);
        recyclerView.setAdapter(adapter);

        // Initialize and connect the WebSocket
        String netId = UserSession.getInstance(getContext()).getNetId();
        String userType = UserSession.getInstance(getContext()).getUserType();
        announcementClient = new AnnouncementWebSocketClient(this);
        announcementClient.connectWebSocket(netId, userType);

        return view;
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
                    onMessageReceived(message);
                }
            });
        } else {
            Log.e(TAG, "WebSocket client is not initialized");
            Toast.makeText(getContext(), "WebSocket client is not initialized", Toast.LENGTH_SHORT).show();
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (announcementClient != null) {
            announcementClient.disconnectWebSocket();
        }
    }
}
