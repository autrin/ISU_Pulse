package com.coms309.isu_pulse_frontend.course_functional;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.ui.home.Course;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<Course> courseList;

    public CourseAdapter(List<Course> courseList) {
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_item, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.courseCodeTextView.setText(course.getCode());
        holder.courseNameTextView.setText(course.getTitle());
        holder.deleteButton.setOnClickListener(v -> {
            courseList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, courseList.size());
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView courseCodeTextView, courseNameTextView;
        ImageButton deleteButton;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseCodeTextView = itemView.findViewById(R.id.courseCodeTextView);
            courseNameTextView = itemView.findViewById(R.id.courseNameTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}