package com.coms309.isu_pulse_frontend.course_functional;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.api.CourseService;

public class CourseItem extends AppCompatActivity {
    private ImageButton deleteButton;
    private String studentId;
    private int courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_item);


        // Retrieve studentId and courseId from Intent extras
        Intent intent = getIntent();
        if (intent != null) {
            studentId = intent.getStringExtra("studentId");
            courseId = intent.getIntExtra("courseId", -1);
            if (studentId == null || courseId == -1) {
                Toast.makeText(this, "Invalid course or student information.", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity if data is missing
                return;
            }
        } else {
            Toast.makeText(this, "No data provided.", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if Intent is null
            return;
        }

        // Initialize the delete button and set its click listener
        deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CourseView.courseService.removeEnroll(studentId, courseId, new CourseService.RemoveEnrollCallback() {
                    @Override
                    public void onSuccess(String message) {
                        // Notify the user of success and finish the activity with result
                        Toast.makeText(CourseItem.this, "Course removed successfully.", Toast.LENGTH_SHORT).show();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("deletedCourseId", courseId);
                        for (int i = 0; i < CourseView.courseList.size(); i++) {
                            if (CourseView.courseList.get(i).getcId() == courseId) {
                                CourseView.courseList.remove(i);
                                CourseView.adapter.notifyItemRemoved(i);
                            }
                        }
                        CourseView.adapter = new CourseAdapter(CourseView.courseList);
                        CourseView.recyclerViewCourses.setAdapter(CourseView.adapter);
                        CourseView.adapter.notifyDataSetChanged();

                        setResult(RESULT_OK, resultIntent);
                        finish(); // Close the activity and return to CourseView
                    }

                    @Override
                    public void onError(String error) {
                        // Notify the user of the error
                        Toast.makeText(CourseItem.this, "Error removing course: " + error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

}
