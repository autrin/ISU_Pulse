package com.coms309.isu_pulse_frontend.ui.calendar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.coms309.isu_pulse_frontend.MainActivity;
import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.api.CourseService;
import com.coms309.isu_pulse_frontend.chat_system.ChatActivity;
import com.coms309.isu_pulse_frontend.chat_system.ChatList;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;
import com.coms309.isu_pulse_frontend.schedule.Schedule;

import java.util.List;

public class ClassCalendar extends AppCompatActivity {
    private GridLayout calendarGrid;
    private CourseService courseService;
    private ImageButton backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);
        calendarGrid = findViewById(R.id.calendar_grid);
        backButton = findViewById(R.id.backButton);

        // Initialize hour labels (same as before)
        for (int hour = 8; hour <= 19; hour++) {
            TextView hourLabel = new TextView(this);
            if (hour == 12) {
                hourLabel.setText("12 PM");
            } else if (hour > 12) {
                hourLabel.setText((hour - 12) + " PM");
            } else {
                hourLabel.setText(hour + " AM");
            }
            hourLabel.setGravity(Gravity.CENTER);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.rowSpec = GridLayout.spec((hour - 8) * 2 + 1); // Map to rows
            params.columnSpec = GridLayout.spec(0); // First column for hours
            params.height = 130;
            hourLabel.setLayoutParams(params);

            calendarGrid.addView(hourLabel);
        }
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ClassCalendar.this, MainActivity.class);
            startActivity(intent);
        });

        // Fetch enrolled courses and display them
        String netId = UserSession.getInstance().getNetId();
        courseService = new CourseService(this);
        courseService.getEnrolledCoursesById(netId, new CourseService.GetEnrolledCoursesCallback() {
            @Override
            public void onSuccess(List<Schedule> courses) {
                runOnUiThread(() -> {
                    for (Schedule course : courses) {
                        addCourseBlock(course); // Add each course block to the calendar
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(ClassCalendar.this, "Error loading courses: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    /**
     * Adds a course block to the calendar grid.
     *
     * @param course The course schedule to be displayed.
     */
    private void addCourseBlock(Schedule course) {
        try {
            // Parse start and end times (assumes time is in "HH:mm:ss" format)
            int startHour = Integer.parseInt(course.getStartTime().split(":")[0]);
            int startMinute = Integer.parseInt(course.getStartTime().split(":")[1]);
            int endHour = Integer.parseInt(course.getEndTime().split(":")[0]);
            int endMinute = Integer.parseInt(course.getEndTime().split(":")[1]);

            // Calculate row for start and end times
            int rowStart = (startHour - 8) * 2 + (startMinute >= 30 ? 1 : 0) + 1; // +1 to adjust for header
            int rowEnd = (endHour - 8) * 2 + (endMinute > 30 ? 2 : (endMinute > 0 ? 1 : 0)) + 1; // +1 to adjust for header
            int rowSpan = Math.max(rowEnd - rowStart, 1); // Ensure the block spans at least 1 row

            // Map recurring pattern (e.g., "MWF") to columns
            char[] days = course.getRecurringPattern().toCharArray();

            for (char day : days) {
                int dayColumn = mapDayToColumn(String.valueOf(day)); // Map day to column index
                if (dayColumn == 0) continue; // Skip invalid days

                // Create the course block
                TextView classBlock = new TextView(this);
                classBlock.setText(course.getCourse().getCode()); // Course code as text
                classBlock.setBackgroundColor(Color.BLUE);
                classBlock.setGravity(Gravity.CENTER);
                classBlock.setTextColor(Color.WHITE);

                // Layout parameters for the block
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(rowStart, rowSpan); // Start row and span
                params.columnSpec = GridLayout.spec(dayColumn); // Column for the specific day
                params.setMargins(8, 8, 8, 8); // Optional spacing for better aesthetics
                params.height = 130; // Adjust row height (100 pixels or dp)
                classBlock.setLayoutParams(params);

                calendarGrid.addView(classBlock); // Add to the grid
            }
        } catch (Exception e) {
            Log.e("ClassCalendar", "Error adding course block: " + e.getMessage());
        }
    }

    /**
     * Maps the recurring pattern abbreviations (e.g., "M", "W") to a column index.
     *
     * @param day The day of the week abbreviation.
     * @return The corresponding column index.
     */
    private int mapDayToColumn(String day) {
        switch (day.trim()) {
            case "M":
                return 1; // Monday
            case "T":
                return 2; // Tuesday
            case "W":
                return 3; // Wednesday
            case "R":
                return 4; // Thursday
            case "F":
                return 5; // Friday
            default:
                return 0; // Invalid day (e.g., weekend or empty string)
        }
    }

}
