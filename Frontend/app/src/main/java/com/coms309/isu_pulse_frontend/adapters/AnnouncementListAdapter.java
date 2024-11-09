package com.coms309.isu_pulse_frontend.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.api.AnnouncementWebSocketClient;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;
import com.coms309.isu_pulse_frontend.model.Announcement;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AnnouncementListAdapter extends RecyclerView.Adapter<AnnouncementListAdapter.AnnouncementViewHolder> {

    private List<Announcement> announcements;
    private boolean isTeacherView;
    private Context context;

    public AnnouncementListAdapter(Context context, List<Announcement> announcements, boolean isTeacherView) {
        this.context = context;
        this.announcements = announcements;
        this.isTeacherView = isTeacherView;
    }

    @Override
    public AnnouncementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.announcement_item, parent, false);
        return new AnnouncementViewHolder(view, false);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementViewHolder holder, int position) {
        Announcement announcement = announcements.get(position);

        holder.announcementContent.setText(announcement.getContent());
        holder.announcementCourse.setText(announcement.getCourseName());
        holder.announcementTimestamp.setText(formatDate(announcement.getTimestamp()));

        String userType = UserSession.getInstance(holder.itemView.getContext()).getUserType();

        // Show buttons only for teachers
        if ("FACULTY".equals(userType)) {
            holder.teacherActionsLayout.setVisibility(View.VISIBLE);

            holder.buttonUpdateAnnouncement.setOnClickListener(v -> editAnnouncement(announcement, position));
            holder.buttonDeleteAnnouncement.setOnClickListener(v -> deleteAnnouncement(announcement, position));
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

    private void editAnnouncement(Announcement announcement, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Announcement");

        final EditText input = new EditText(context);
        input.setText(announcement.getContent());
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newContent = input.getText().toString();
            if (!newContent.isEmpty()) {
                announcement.setContent(newContent);
                notifyItemChanged(position);

                // Use WebSocket to send update
                UserSession.getInstance(context).getWebSocketClient()
                        .updateAnnouncement(announcement.getId(), newContent);
                Toast.makeText(context, "Announcement updated", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void deleteAnnouncement(Announcement announcement, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Announcement");
        builder.setMessage("Are you sure you want to delete this announcement?");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            announcements.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, announcements.size());

            // Use WebSocket to send delete request
            UserSession.getInstance(context).getWebSocketClient()
                    .deleteAnnouncement(announcement.getId());
            Toast.makeText(context, "Announcement deleted", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    public static class AnnouncementViewHolder extends RecyclerView.ViewHolder {
        TextView announcementContent, announcementTimestamp, announcementCourse;
        LinearLayout teacherActionsLayout;
        Button buttonUpdateAnnouncement, buttonDeleteAnnouncement;

        public AnnouncementViewHolder(@NonNull View itemView, boolean isTeacherView) {
            super(itemView);

            if ("FACULTY".equals(UserSession.getInstance(itemView.getContext()).getUserType())) {
                // Initialize views for teacher layout
                announcementContent = itemView.findViewById(R.id.announcement_content);
                announcementTimestamp = itemView.findViewById(R.id.announcement_timestamp);
                announcementCourse = itemView.findViewById(R.id.announcement_course);
                teacherActionsLayout = itemView.findViewById(R.id.teacher_actions_layout);
                buttonUpdateAnnouncement = itemView.findViewById(R.id.button_update_announcement);
                buttonDeleteAnnouncement = itemView.findViewById(R.id.button_delete_announcement);
            } else {
                // Initialize views for student layout
                announcementContent = itemView.findViewById(R.id.announcement_content);
                announcementTimestamp = itemView.findViewById(R.id.announcement_timestamp);
                announcementCourse = itemView.findViewById(R.id.announcement_course);
                teacherActionsLayout = itemView.findViewById(R.id.teacher_actions_layout);
            }
        }
    }

}
