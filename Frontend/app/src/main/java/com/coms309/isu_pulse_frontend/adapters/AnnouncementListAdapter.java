package com.coms309.isu_pulse_frontend.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;
import com.coms309.isu_pulse_frontend.model.Announcement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A RecyclerView adapter for displaying and managing a list of announcements.
 * Supports actions like editing and deleting announcements for faculty users.
 */
public class AnnouncementListAdapter extends RecyclerView.Adapter<AnnouncementListAdapter.AnnouncementViewHolder> {

    private List<Announcement> announcements;
    private boolean isTeacherView;
    private Context context;

    /**
     * Constructs an {@link AnnouncementListAdapter}.
     *
     * @param context        the context for inflating views and displaying dialogs
     * @param announcements  the list of announcements to be displayed
     * @param isTeacherView  indicates if the adapter is being used in the teacher's view
     */
    public AnnouncementListAdapter(Context context, List<Announcement> announcements, boolean isTeacherView) {
        this.context = context;
        this.announcements = announcements;
        this.isTeacherView = isTeacherView;
    }

    /**
     * Creates and returns a new {@link AnnouncementViewHolder} for displaying an announcement.
     *
     * @param parent   the parent view group
     * @param viewType the type of the view
     * @return a new instance of {@link AnnouncementViewHolder}
     */
    @Override
    public AnnouncementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.announcement_item, parent, false);
        return new AnnouncementViewHolder(view, false);
    }

    /**
     * Binds data to the {@link AnnouncementViewHolder}.
     *
     * @param holder   the view holder
     * @param position the position of the item in the list
     */
    @Override
    public void onBindViewHolder(@NonNull AnnouncementViewHolder holder, int position) {
        Announcement announcement = announcements.get(position);

        holder.announcementContent.setText(announcement.getContent());
        holder.announcementCourse.setText(announcement.getCourseName());
        holder.announcementTimestamp.setText(formatDate(announcement.getTimestamp()));

        String userType = UserSession.getInstance(holder.itemView.getContext()).getUserType();

        // Show buttons for teachers
        if ("FACULTY".equals(userType)) {
            holder.teacherActionsLayout.setVisibility(View.VISIBLE);
            holder.buttonUpdateAnnouncement.setOnClickListener(v -> editAnnouncement(announcement, position));
            holder.buttonDeleteAnnouncement.setOnClickListener(v -> deleteAnnouncement(announcement, position));
        } else {
            holder.teacherActionsLayout.setVisibility(View.GONE);
        }
    }

    /**
     * Returns the total number of announcements.
     *
     * @return the size of the announcements list
     */
    @Override
    public int getItemCount() {
        return announcements.size();
    }

    /**
     * Formats the timestamp of an announcement for display.
     *
     * @param timestamp the timestamp string in ISO 8601 format
     * @return the formatted timestamp string
     */
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

    /**
     * Displays a dialog for editing an announcement's content.
     *
     * @param announcement the announcement to edit
     * @param position     the position of the announcement in the list
     */
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

                // Notify backend via WebSocket
                UserSession.getInstance(context).getWebSocketClient()
                        .updateAnnouncement(announcement.getId(), newContent);
                Toast.makeText(context, "Announcement updated", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /**
     * Displays a dialog for deleting an announcement.
     *
     * @param announcement the announcement to delete
     * @param position     the position of the announcement in the list
     */
    private void deleteAnnouncement(Announcement announcement, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Announcement");
        builder.setMessage("Are you sure you want to delete this announcement?");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            announcements.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, announcements.size());

            // Notify backend via WebSocket
            UserSession.getInstance(context).getWebSocketClient()
                    .deleteAnnouncement(announcement.getId());
            Toast.makeText(context, "Announcement deleted", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /**
     * ViewHolder class for displaying a single announcement.
     */
    public static class AnnouncementViewHolder extends RecyclerView.ViewHolder {
        TextView announcementContent, announcementTimestamp, announcementCourse;
        LinearLayout teacherActionsLayout;
        Button buttonUpdateAnnouncement, buttonDeleteAnnouncement;

        /**
         * Constructs an {@link AnnouncementViewHolder}.
         *
         * @param itemView      the view representing a single announcement
         * @param isTeacherView indicates if the view is for teachers
         */
        public AnnouncementViewHolder(@NonNull View itemView, boolean isTeacherView) {
            super(itemView);

            announcementContent = itemView.findViewById(R.id.announcement_content);
            announcementTimestamp = itemView.findViewById(R.id.announcement_timestamp);
            announcementCourse = itemView.findViewById(R.id.announcement_course);
            teacherActionsLayout = itemView.findViewById(R.id.teacher_actions_layout);

            if ("FACULTY".equals(UserSession.getInstance(itemView.getContext()).getUserType())) {
                buttonUpdateAnnouncement = itemView.findViewById(R.id.button_update_announcement);
                buttonDeleteAnnouncement = itemView.findViewById(R.id.button_delete_announcement);
            }
        }
    }
}
