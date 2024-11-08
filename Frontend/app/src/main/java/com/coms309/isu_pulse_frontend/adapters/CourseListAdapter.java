package com.coms309.isu_pulse_frontend.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.model.Course;

import java.util.List;

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.CourseViewHolder> {

    private List<Course> courses;
    private OnCourseClickListener onCourseClickListener;
    private String userRole;

    public interface OnCourseClickListener {
        void onCourseClick(Long courseId);
    }

    public CourseListAdapter(List<Course> courses, String userRole, OnCourseClickListener onCourseClickListener) {
        this.courses = courses;
        this.userRole = userRole;
        this.onCourseClickListener = onCourseClickListener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutRes = R.layout.course_item_teacher;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.courseTitle.setText(course.getTitle());
        holder.courseSection.setText(course.getSection());

        holder.itemView.setOnClickListener(v -> {
            if (onCourseClickListener != null) {
                onCourseClickListener.onCourseClick(course.getcId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }


    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView courseTitle, courseSection;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseTitle = itemView.findViewById(R.id.courseTitle);
            courseSection = itemView.findViewById(R.id.courseSection);
        }
    }

}
