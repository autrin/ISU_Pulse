package com.coms309.isu_pulse_frontend.adapters;
// TODO: Complete this class

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.api.TaskApiService;
import com.coms309.isu_pulse_frontend.ui.home.CourseTask;
import com.coms309.isu_pulse_frontend.ui.home.EditTaskDialog;
import com.coms309.isu_pulse_frontend.ui.home.PersonalTask;
import com.coms309.isu_pulse_frontend.viewholders.ViewHolder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AnnouncementListAdapter extends RecyclerView.Adapter<AnnouncementListAdapter.AnnouncementViewHolder> {
    private List<Object> announcementList;
    private WeeklyCalendarAdapter weekCalendarAdapter;

    public AnnouncementListAdapter(List<Object> announcementList, WeeklyCalendarAdapter weekCalendarAdapter) {
        this.announcementList = announcementList;
        this.weekCalendarAdapter = weekCalendarAdapter;
    }

    @NonNull
    @Override
    public AnnouncementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_announcement, parent, false);
        return new AnnouncementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementViewHolder holder, int position) {
        Object announcement = announcementList.get(position);
        Log.d("Announcement Data", "Position: " + position + ", Announcement: " + announcement.toString());

    }

    @Override
    public int getItemCount() {
        return announcementList.size();
    }

    public void updateannouncements(List<Object> newannouncementList) {
        this.announcementList = newannouncementList;
//        notifyDataSetChanged();
    }

    public void addannouncement(Object announcement) {
        this.announcementList.add(announcement);
//        notifyItemInserted(announcementList.size() - 1);
    }

    public void removeannouncement(int position) {
        this.announcementList.remove(position);
//        notifyItemRemoved(position);
    }

    public static class AnnouncementViewHolder extends RecyclerView.ViewHolder implements ViewHolder {
        TextView content, course, timestamp;
        CheckBox checkBox;

        public AnnouncementViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.announcement_content);
            course = itemView.findViewById(R.id.announcement_course);
            timestamp = itemView.findViewById(R.id.announcement_timestamp);
            checkBox = itemView.findViewById(R.id.checkBoxAnnouncement);
        }

        @Override
        public void bind(Object obj) {
        }
    }
}
