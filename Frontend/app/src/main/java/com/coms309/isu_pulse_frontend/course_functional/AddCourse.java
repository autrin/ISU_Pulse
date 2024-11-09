package com.coms309.isu_pulse_frontend.course_functional;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.api.CourseService;

public class AddCourse extends AppCompatActivity {

    private ImageButton addButton;
    private CourseService courseService;
    private ImageButton backButton;
    private ImageButton addButton1;
    private ImageButton addButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_course);

        // Initialize the CourseService with the current context
        courseService = new CourseService(this);
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(AddCourse.this, CourseView.class);
            startActivity(intent);
        });

//        addButton = findViewById(R.id.addButton);
//        addButton1 = findViewById(R.id.addButton1);
//        addButton2 = findViewById(R.id.addButton2);
//
//        addButton.setOnClickListener(view -> {
//            courseService.enrollInCourse("bachnguyen", 16, new CourseService.EnrollCallback() {
//                @Override
//                public void onSuccess(String message) {
//                    Toast.makeText(AddCourse.this, "Enrolled successfully!", Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void onError(String error) {
//                    Toast.makeText(AddCourse.this, "Error: " + error, Toast.LENGTH_LONG).show();
//                }
//            });
//        });
//
//
//        addButton1.setOnClickListener(view -> {
//            courseService.enrollInCourse("bachnguyen", 15, new CourseService.EnrollCallback() {
//                @Override
//                public void onSuccess(String message) {
//                    Toast.makeText(AddCourse.this, "Enrolled successfully!", Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void onError(String error) {
//                    Toast.makeText(AddCourse.this, "Error: " + error, Toast.LENGTH_LONG).show();
//                }
//            });
//        });
//
//        addButton2.setOnClickListener(view -> {
//            courseService.enrollInCourse("bachnguyen", 17, new CourseService.EnrollCallback() {
//                @Override
//                public void onSuccess(String message) {
//                    Toast.makeText(AddCourse.this, "Enrolled successfully!", Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void onError(String error) {
//                    Toast.makeText(AddCourse.this, "Error: " + error, Toast.LENGTH_LONG).show();
//                }
//            });
//        });
    }
}
