package com.coms309.isu_pulse_frontend.course_functional;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.api.CourseService;
import com.coms309.isu_pulse_frontend.model.Course;

public class CourseItem extends AppCompatActivity {
    private ImageButton deleteButton;
    private TextView courseCodeTextView;
    private TextView courseTitleTextView;
    private TextView courseDescriptionTextView;
    private TextView courseCreditsTextView;
    private TextView courseDepartmentTextView;

    private String studentId;
    private Course course;
    private CourseService courseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_item);

        // Initialize views
        deleteButton = findViewById(R.id.deleteButton);
        courseCodeTextView = findViewById(R.id.courseCodeTextView);

        // Initialize CourseService
        courseService = new CourseService(this);

        // Retrieve studentId and course from Intent extras
        Intent intent = getIntent();
        if (intent != null) {
            studentId = intent.getStringExtra("studentId");
            course = (Course) intent.getSerializableExtra("course");
            if (studentId == null || course == null) {
                Toast.makeText(this, "Invalid course or student information.", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity if data is missing
                return;
            }
        } else {
            Toast.makeText(this, "No data provided.", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if Intent is null
            return;
        }

        // Populate views with course data
        courseCodeTextView.setText(course.getCode());
        courseTitleTextView.setText(course.getTitle());



        // Set up delete button click listener
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCourse();
            }
        });
    }

    private void deleteCourse() {
        courseService.removeEnroll(studentId, course.getcId(), new CourseService.RemoveEnrollCallback() {
            @Override
            public void onSuccess(String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CourseItem.this, "Course removed successfully.", Toast.LENGTH_SHORT).show();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("deletedCourseId", course.getcId());
                        setResult(RESULT_OK, resultIntent);
                        finish(); // Close the activity and return to CourseView
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CourseItem.this, "Error removing course: " + error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
