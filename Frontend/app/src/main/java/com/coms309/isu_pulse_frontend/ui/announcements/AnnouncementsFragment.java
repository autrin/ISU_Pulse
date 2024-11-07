package com.coms309.isu_pulse_frontend.ui.announcements;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.adapters.AnnouncementListAdapter;
import com.coms309.isu_pulse_frontend.model.Announcement;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementsFragment extends Fragment {

    private RecyclerView recyclerView;
    private AnnouncementListAdapter adapter;
    private List<Announcement> announcements;

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
        View root = inflater.inflate(R.layout.fragment_announcements, container, false);

        // Initialize RecyclerView
        recyclerView = root.findViewById(R.id.recyclerViewAnnouncements);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Retrieve courseId if necessary
        long courseId = getArguments() != null ? getArguments().getLong("courseId") : -1;
        announcements = fetchAnnouncements(courseId);

        // Set up the adapter
        adapter = new AnnouncementListAdapter(announcements, false); // Assuming this is a student view
        recyclerView.setAdapter(adapter);

        return root;
    }

    private List<Announcement> fetchAnnouncements(long courseId) {
        // Placeholder: Fetch or load announcements here, potentially from a ViewModel or data source.
        List<Announcement> sampleAnnouncements = new ArrayList<>();
        sampleAnnouncements.add(new Announcement(1L, "Exam postponed to next week", courseId, "facultyNetId", "2024-11-05T22:12:16.860-06:00", false, "Sample Course"));
        sampleAnnouncements.add(new Announcement(2L, "Class canceled due to weather", courseId, "facultyNetId", "2024-11-06T10:12:16.860-06:00", false, "Sample Course"));
        return sampleAnnouncements;
    }
}
