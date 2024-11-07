package com.coms309.isu_pulse_frontend.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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

    public AnnouncementListAdapter(List<Announcement> announcements) {
        this.announcements = announcements;
    }

    @NonNull
    @Override
    public AnnouncementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.announcement_item, parent, false);
        return new AnnouncementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementViewHolder holder, int position) {
        Announcement announcement = announcements.get(position);

        // Set the content and timestamp
        holder.announcementContent.setText(announcement.getContent());

        // Format the timestamp
        String formattedDate = formatDate(announcement.getTimestamp());
        holder.announcementTimestamp.setText(formattedDate);

        // Set the seen status of the announcement
        holder.announcementSeenCheckbox.setChecked(announcement.isSeenStatus());

        // Listener for checkbox to update seen status
        holder.announcementSeenCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            announcement.setSeenStatus(isChecked);
            // Optional: Notify your backend of the seen status change, if necessary.
        });
    }

    @Override
    public int getItemCount() {
        return announcements.size();
    }

    private String formatDate(String timestamp) {
        // Expected input format: "2024-11-05T22:12:16.860-06:00"
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());

        try {
            Date date = inputFormat.parse(timestamp);
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return timestamp;  // Fallback to original if parsing fails
        }
    }

    public static class AnnouncementViewHolder extends RecyclerView.ViewHolder {
        TextView announcementContent, announcementTimestamp;
        CheckBox announcementSeenCheckbox;

        public AnnouncementViewHolder(@NonNull View itemView) {
            super(itemView);
            announcementContent = itemView.findViewById(R.id.announcement_content);
            announcementTimestamp = itemView.findViewById(R.id.announcement_timestamp);
            announcementSeenCheckbox = itemView.findViewById(R.id.checkBoxAnnouncement);
        }
    }
}
