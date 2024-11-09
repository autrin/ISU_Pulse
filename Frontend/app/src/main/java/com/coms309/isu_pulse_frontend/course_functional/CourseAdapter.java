package com.coms309.isu_pulse_frontend.course_functional;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.schedule.Schedule;
import com.coms309.isu_pulse_frontend.ui.home.Course;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    private List<Schedule> courseList;
    private Context context;



    public CourseAdapter(List<Schedule> courseList, Context context) {
        this.courseList = courseList;
        this.context = context;
    }

    @Override
    public CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_item, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CourseViewHolder holder, int position) {
        Schedule course = courseList.get(position);
        holder.courseCodeTextView.setText(course.getCourse().getCode());
        holder.creditsTextView.setText(String.valueOf(course.getCourse().getCredits()));
        holder.courseTitleTextView.setText(course.getCourse().getTitle());
        holder.sectionTextView.setText(course.getSection());
        holder.recurringPatternTextView.setText(course.getRecurringPattern());
        holder.startTimeTextView.setText(course.getStartTime());
        holder.endTimeTextView.setText(course.getEndTime());

    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView courseCodeTextView;
        TextView courseTitleTextView;
        TextView creditsTextView;
        TextView sectionTextView;
        TextView recurringPatternTextView;
        TextView startTimeTextView;
        TextView endTimeTextView;


        CourseViewHolder(View itemView) {
            super(itemView);
            courseCodeTextView = itemView.findViewById(R.id.courseCodeTextView);
            courseTitleTextView = itemView.findViewById(R.id.courseNameTextView);
            creditsTextView = itemView.findViewById(R.id.creditsTextView);
            sectionTextView = itemView.findViewById(R.id.sectionTextView);
            recurringPatternTextView = itemView.findViewById(R.id.recurringPatternTextView);
            startTimeTextView = itemView.findViewById(R.id.startTimeTextView);
            endTimeTextView = itemView.findViewById(R.id.endTimeTextView);
        }
    }
}