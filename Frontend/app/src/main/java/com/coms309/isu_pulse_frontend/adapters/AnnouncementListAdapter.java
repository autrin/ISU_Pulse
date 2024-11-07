package com.coms309.isu_pulse_frontend.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.model.Announcement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AnnouncementListAdapter extends RecyclerView.Adapter<AnnouncementListAdapter.AnnouncementViewHolder> {

    private List<Announcement> announcements;
    private boolean isTeacherView;

    public AnnouncementListAdapter(List<Announcement> announcements, boolean isTeacherView) {
        this.announcements = announcements;
        this.isTeacherView = isTeacherView;
    }

    @NonNull
    @Override
    public AnnouncementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutRes = isTeacherView ? R.layout.teacher_announcement : R.layout.announcement_item;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
        return new AnnouncementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementViewHolder holder, int position) {
        Announcement announcement = announcements.get(position);

        // Set announcement details
        holder.announcementContent.setText(announcement.getContent());
        holder.announcementCourse.setText(announcement.getCourseName());
        holder.announcementTimestamp.setText(formatDate(announcement.getTimestamp()));

        // Show buttons only for teachers
        if (isTeacherView) {
            holder.teacherActionsLayout.setVisibility(View.VISIBLE);
        } else {
            holder.teacherActionsLayout.setVisibility(View.GONE);
        }

//        // Set the seen status and handle checkbox changes
//        holder.announcementSeenCheckbox.setOnCheckedChangeListener(null); // Clear previous listener
//        holder.announcementSeenCheckbox.setChecked(announcement.isSeenStatus());
//        holder.announcementSeenCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            announcement.setSeenStatus(isChecked);
//            // Optional: Notify backend if needed
//        });
    }

    @Override
    public int getItemCount() {
        return announcements.size();
    }

    private String formatDate(String timestamp) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
        try {
            Date date = inputFormat.parse(timestamp);
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return timestamp;
        }
    }

    public static class AnnouncementViewHolder extends RecyclerView.ViewHolder {
        TextView announcementContent, announcementTimestamp, announcementCourse;
        //        CheckBox announcementSeenCheckbox;
        LinearLayout teacherActionsLayout; // Layout containing Update and Delete buttons

        public AnnouncementViewHolder(@NonNull View itemView) {
            super(itemView);
            announcementContent = itemView.findViewById(R.id.announcement_content);
            announcementTimestamp = itemView.findViewById(R.id.announcement_timestamp);
            announcementCourse = itemView.findViewById(R.id.announcement_course);
//            announcementSeenCheckbox = itemView.findViewById(R.id.checkBoxAnnouncement);
            teacherActionsLayout = itemView.findViewById(R.id.teacher_actions_layout); // Reference to buttons layout

        }
    }
}
