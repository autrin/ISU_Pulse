package com.coms309.isu_pulse_frontend.course_functional;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.model.Course;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    private List<Course> courseList;
    private OnCourseDeleteListener deleteListener;

    public interface OnCourseDeleteListener {
        void onCourseDelete(int position, Course course);
    }

    public CourseAdapter(List<Course> courseList, OnCourseDeleteListener listener) {
        this.courseList = courseList;
        this.deleteListener = listener;
    }

    @Override
    public CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_item, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.courseCodeTextView.setText(course.getCode());
        holder.courseTitleTextView.setText(course.getTitle());
        holder.deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onCourseDelete(position, course);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView courseCodeTextView;
        TextView courseTitleTextView;
        ImageButton deleteButton;

        CourseViewHolder(View itemView) {
            super(itemView);
            courseCodeTextView = itemView.findViewById(R.id.courseCodeTextView);
            courseTitleTextView = itemView.findViewById(R.id.courseNameTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
