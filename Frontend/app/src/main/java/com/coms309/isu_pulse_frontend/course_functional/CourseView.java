package com.coms309.isu_pulse_frontend.course_functional;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
    public static RecyclerView recyclerViewCourses;
    public static List<Course> courseList;
    static CourseAdapter adapter;
    public static CourseService courseService;
    private static final int DELETE_COURSE_REQUEST_CODE = 1;

    private String getCurrentStudentId() {
        // Replace this with your actual method of retrieving the student ID
        // For example, from SharedPreferences or a user session
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        return prefs.getString("studentId", "bachnguyen"); // Default to "bachnguyen" if not found
    }



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

//        fetchEnrolledCourses("bachnguyen");
        adapter = new CourseAdapter(courseList);
        recyclerViewCourses.setAdapter(adapter);
        courseService = new CourseService(this);

        String studentId = getCurrentStudentId();
        fetchEnrolledCourses(studentId);


    }

    private void fetchEnrolledCourses(String sId) {


        courseService.getEnrolledCourses(sId, new CourseService.GetEnrolledCoursesCallback() {
            @Override
            public void onSuccess(List<Course> courses) {


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


                Toast.makeText(CourseView.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onCourseItemClicked(Course course) {
        Intent intent = new Intent(CourseView.this, CourseItem.class);
        intent.putExtra("studentId", getCurrentStudentId());
        intent.putExtra("courseId", course.getcId());
        startActivityForResult(intent, DELETE_COURSE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DELETE_COURSE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            int deletedCourseId = data.getIntExtra("deletedCourseId", -1);
            if (deletedCourseId != -1) {
                removeCourseFromList(deletedCourseId);
            }
        }
    }

    public static void removeCourseFromList(int courseId) {
        for (int i = 0; i < courseList.size(); i++) {
            if (courseList.get(i).getcId() == courseId) {
                courseList.remove(i);
                adapter.notifyItemRemoved(i);
            }
        }
    }



}
