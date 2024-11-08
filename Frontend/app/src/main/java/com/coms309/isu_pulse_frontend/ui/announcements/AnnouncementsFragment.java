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

    public static AnnouncementsFragment newInstance(Long courseId) {
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
        adapter = new AnnouncementListAdapter(announcementList, false);
        recyclerView.setAdapter(adapter);

        // Initialize and connect the WebSocket
        String netId = UserSession.getInstance(getContext()).getNetId();
        String userType = UserSession.getInstance(getContext()).getUserType();
        announcementClient = new AnnouncementWebSocketClient(this);
        announcementClient.connectWebSocket(netId, userType);

        return view;
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


    private void handleHistoryAction(JSONObject jsonMessage) throws JSONException {
        JSONArray announcementsArray = jsonMessage.getJSONArray("announcements");
        announcementList.clear();  // Clear any existing announcements

        // Loop through each announcement in the JSON array and add it to the list
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
            announcementList.add(announcement);  // Add each announcement to the list
        }

        // Notify the adapter that the data set has changed to update the RecyclerView
        adapter.notifyDataSetChanged();
    }

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

        // Add the new announcement at the top of the list
        announcementList.add(0, newAnnouncement);
        adapter.notifyItemInserted(0);
        recyclerView.scrollToPosition(0);  // Scroll to the top to show the new announcement
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (announcementClient != null) {
            announcementClient.disconnectWebSocket();
        }
    }
}
