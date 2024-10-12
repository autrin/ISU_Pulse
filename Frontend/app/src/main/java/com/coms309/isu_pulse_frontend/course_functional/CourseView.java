package com.coms309.isu_pulse_frontend.course_functional;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.api.CourseService;
import com.coms309.isu_pulse_frontend.proifle_activity.ProfileActivity;
import com.coms309.isu_pulse_frontend.ui.home.Course;

import java.util.ArrayList;
import java.util.List;

public class CourseView extends AppCompatActivity implements CourseAdapter.OnCourseDeleteListener {

    private ImageButton backButton;
    private RecyclerView recyclerViewCourses;
    private List<Course> courseList;
    private CourseAdapter adapter;
    private CourseService courseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_view);

        recyclerViewCourses = findViewById(R.id.recyclerViewCourses);
        recyclerViewCourses.setLayoutManager(new LinearLayoutManager(this));

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(CourseView.this, ProfileActivity.class);
            startActivity(intent);
        });

        courseList = new ArrayList<>();
        adapter = new CourseAdapter(courseList, this);
        recyclerViewCourses.setAdapter(adapter);

        courseService = new CourseService(this);

        String studentId = getCurrentStudentId();
        fetchEnrolledCourses(studentId);
    }

    private String getCurrentStudentId() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        return prefs.getString("studentId", "bachnguyen"); // Default to "bachnguyen" if not found
    }

    private void fetchEnrolledCourses(String sId) {
        courseService.getEnrolledCourses(sId, new CourseService.GetEnrolledCoursesCallback() {
            @Override
            public void onSuccess(List<Course> courses) {
                runOnUiThread(() -> {
                    if (courses.isEmpty()) {
                        Toast.makeText(CourseView.this, "No enrolled courses found.", Toast.LENGTH_SHORT).show();
                    } else {
                        courseList.clear();
                        courseList.addAll(courses);
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(CourseView.this, error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    @Override
    public void onCourseDelete(int position, Course course) {
        String studentId = getCurrentStudentId();
        courseService.removeEnroll(studentId, course.getcId(), new CourseService.RemoveEnrollCallback() {
            @Override
            public void onSuccess(String message) {
                runOnUiThread(() -> {
                    courseList.remove(position);
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(CourseView.this, "Course removed successfully", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(CourseView.this, "Error removing course: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}