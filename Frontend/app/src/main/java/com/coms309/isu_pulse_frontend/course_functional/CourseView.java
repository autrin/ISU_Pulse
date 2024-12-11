package com.coms309.isu_pulse_frontend.course_functional;

/**
 * This activity handles displaying a list of courses that a user is enrolled in.
 * It allows navigation back to the ProfileActivity or to the AddCourse activity to add a new course.
 * It also integrates with the CourseService to fetch and display course data dynamically.
 *
 * @author ntbach
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.api.CourseService;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;
import com.coms309.isu_pulse_frontend.profile_activity.ProfileActivity;
import com.coms309.isu_pulse_frontend.schedule.Schedule;
import java.util.ArrayList;
import java.util.List;

/**
 * The CourseView activity displays a list of courses the user is enrolled in.
 */
public class CourseView extends AppCompatActivity  {

    /**
     * Back button to navigate to the ProfileActivity.
     */
    private ImageButton backButton;

    /**
     * RecyclerView to display the list of enrolled courses.
     */
    private RecyclerView recyclerViewCourses;

    /**
     * List to hold the course schedule objects.
     */
    private List<Schedule> courseList;

    /**
     * Adapter to bind the course data to the RecyclerView.
     */
    private CourseAdapter adapter;

    /**
     * Service object to handle API calls for fetching courses.
     */
    private CourseService courseService;

    /**
     * Button to navigate to the AddCourse activity.
     */
    private Button addclass;

    /**
     * Initializes the activity, sets up UI components, and fetches data.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down, this contains the saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_view);

        // Set up the RecyclerView for displaying the list of courses.
        recyclerViewCourses = findViewById(R.id.recyclerViewCourses);
        recyclerViewCourses.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the "Add Class" button and set its click listener.
        addclass = findViewById(R.id.buttonAddClass);
        addclass.setOnClickListener(view -> {
            // Navigate to AddCourse activity when the button is clicked.
            Intent intent = new Intent(CourseView.this, AddCourse.class);
            startActivity(intent);
        });

        // Initialize the back button and set its click listener.
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> {
            // Navigate back to ProfileActivity when the back button is clicked.
            Intent intent = new Intent(CourseView.this, ProfileActivity.class);
            startActivity(intent);
        });

        // Initialize the course list and set up the adapter for RecyclerView.
        courseList = new ArrayList<>();
        adapter = new CourseAdapter(courseList, this);
        recyclerViewCourses.setAdapter(adapter);

        // Initialize the CourseService for fetching course data.
        courseService = new CourseService(this);

        // Retrieve the current student's ID and fetch their enrolled courses.
        String studentId = getCurrentStudentId();
        fetchEnrolledCourses(studentId);
    }

    /**
     * Retrieves the current student's ID using UserSession or SharedPreferences.
     * If no ID is found, it defaults to "ntbach".
     *
     * @return The student's ID as a string.
     */
    private String getCurrentStudentId() {
        // Try retrieving the student ID from the UserSession singleton.
        String studentId = UserSession.getInstance().getNetId();
        if (studentId != null) {
            return studentId;
        }

        // Fallback to SharedPreferences if UserSession doesn't provide the ID.
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return prefs.getString("studentId", "ntbach"); // Default to "ntbach" if not found. // TODO: return error if not found
    }

    /**
     * Fetches the list of courses the student is enrolled in using the CourseService.
     * Updates the UI with the fetched data or displays a message if no courses are found.
     *
     * @param studentId The ID of the student whose courses are to be fetched.
     */
    private void fetchEnrolledCourses(String studentId) {
        // Call the API to get enrolled courses.
        courseService.getEnrolledCoursesById(studentId, new CourseService.GetEnrolledCoursesCallback() {
            @Override
            public void onSuccess(List<Schedule> courses) {
                // Run on the main UI thread to update the RecyclerView.
                runOnUiThread(() -> {
                    if (courses.isEmpty()) {
                        // Notify the user if no courses are found.
                        Toast.makeText(CourseView.this, "No enrolled courses found.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Update the course list and refresh the adapter.
                        courseList.clear();
                        courseList.addAll(courses);
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(String error) {
                // Show an error message to the user on the main UI thread.
                runOnUiThread(() -> {
                    Toast.makeText(CourseView.this, error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /*
    // Uncomment this method to enable deleting a course directly from the list.
    @Override
    public void onCourseDelete(int position, Course course) {
        // Get the current student ID for API call.
        String studentId = getCurrentStudentId();

        // Call the service to remove the enrollment for the selected course.
        courseService.removeEnrollById(studentId, course.getcId(), new CourseService.RemoveEnrollCallback() {
            @Override
            public void onSuccess(String message) {
                runOnUiThread(() -> {
                    // Remove the course from the list and notify the adapter.
                    courseList.remove(position);
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(CourseView.this, "Course removed successfully", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String error) {
                // Show an error message to the user on failure.
                runOnUiThread(() -> {
                    Toast.makeText(CourseView.this, "Error removing course: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    */
}
