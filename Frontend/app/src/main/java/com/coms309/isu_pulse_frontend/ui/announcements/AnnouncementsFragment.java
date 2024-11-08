package com.coms309.isu_pulse_frontend.ui.announcements;

import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

public class AnnouncementsFragment extends Fragment {

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
        View view = inflater.inflate(R.layout.teacher_announcement, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewAnnouncements);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Sample announcements for UI testing
        announcementList = new ArrayList<>();
        announcementList.add(new Announcement(2L, "Exam postponed to next week", 3L, "facultyNetID", "2024-11-05T10:00:00", "Sample Course"));

        // Use announcement_item.xml layout in adapter, not teacher_announcement.xml
        adapter = new AnnouncementListAdapter(announcementList, false); // Set to false to exclude teacher-specific layout
        recyclerView.setAdapter(adapter);

        Button postButton = view.findViewById(R.id.buttonSubmitAnnouncement);
        postButton.setOnClickListener(v -> {
            Long scheduleId = getArguments() != null ? getArguments().getLong("courseId") : null;
            String content = ((EditText) view.findViewById(R.id.editTextAnnouncementContent)).getText().toString();

            if (scheduleId == null || content.isEmpty()) {
                Toast.makeText(getContext(), "Please enter announcement content.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Send the post request through WebSocket
            announcementClient.postAnnouncement(scheduleId, content);

            // Clear the input field after sending the announcement
            ((EditText) view.findViewById(R.id.editTextAnnouncementContent)).setText("");
        });


        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize WebSocket client with faculty netId and userType
        String netId = UserSession.getInstance(getContext()).getNetId();
        announcementClient = new AnnouncementWebSocketClient();
        announcementClient.connectWebSocket(netId, "FACULTY");
    }

    private void postAnnouncement(long scheduleId, String content) {
        announcementClient.postAnnouncement(scheduleId, content);
    }

    private void updateAnnouncement(long announcementId, String content) {
        announcementClient.updateAnnouncement(announcementId, content);
    }

    private void deleteAnnouncement(long announcementId) {
        announcementClient.deleteAnnouncement(announcementId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        announcementClient.disconnectWebSocket();
    }

}
