package com.coms309.isu_pulse_frontend.course_functional;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.api.CourseService;
import com.coms309.isu_pulse_frontend.proifle_activity.ProfileActivity;
import com.coms309.isu_pulse_frontend.ui.home.Course;
import com.coms309.isu_pulse_frontend.ui.home.Department;

import java.util.ArrayList;
import java.util.List;

public class CourseView extends AppCompatActivity {

    private ImageButton backButton;
    private RecyclerView recyclerViewCourses;
    private List<Course> courseList;
    private CourseAdapter adapter;
    private CourseService courseService;

    @Override
    protected void onCreate(Bundle savedInstanceState){

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
//        courseList.add(new Course("COMS 309", "Mobile Application Development", "Description 1", 3, 2, "Department 1", "Location 1", 10, 10));
//        courseList.add(new Course("COMS 331", "Mobile Application Development", "Description 1", 3, 2, "Department 1", "Location 1", 10, 10));


        CourseAdapter adapter = new CourseAdapter(courseList);
        recyclerViewCourses.setAdapter(adapter);
        courseService = new CourseService(this);

        fetchEnrolledCourses("bachnguyen");


    }

    private void fetchEnrolledCourses(String sId) {
        // Optional: Show a loading indicator (e.g., ProgressBar)
        // progressBar.setVisibility(View.VISIBLE);

        courseService.getEnrolledCourses(sId, new CourseService.GetEnrolledCoursesCallback() {
            @Override
            public void onSuccess(List<Course> courses) {
                // Optional: Hide the loading indicator
                // progressBar.setVisibility(View.GONE);

                if (courses.isEmpty()) {
                    Toast.makeText(CourseView.this, "No enrolled courses found.", Toast.LENGTH_SHORT).show();
                } else {
                    courseList.clear();
                    courseList.addAll(courses);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(String error) {
                // Optional: Hide the loading indicator
                // progressBar.setVisibility(View.GONE);

                Toast.makeText(CourseView.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }



}
