package com.coms309.isu_pulse_frontend.ui.courses;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.adapters.CourseListAdapter;
import com.coms309.isu_pulse_frontend.api.AnnouncementWebSocketClient;
import com.coms309.isu_pulse_frontend.api.FacultyApiService;
import com.coms309.isu_pulse_frontend.databinding.FragmentCoursesBinding;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;
import com.coms309.isu_pulse_frontend.model.Course;
import com.coms309.isu_pulse_frontend.model.Schedule;

import java.util.ArrayList;
import java.util.List;

public class CoursesFragment extends Fragment {

    private FragmentCoursesBinding binding;
    private RecyclerView courseRecyclerView;
    private CourseListAdapter courseAdapter;
    private List<Course> courses = new ArrayList<>();
    private TextView emptyStateTextView;
    private static final String TAG = "CoursesFragment";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCoursesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Set up Course RecyclerView
        setupRecyclerView();

        // Fetch courses from backend
        fetchCoursesFromBackend();

        return root;
    }

    private void setupRecyclerView() {
        courseRecyclerView = binding.recyclerViewCourses;
        courseRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        courseAdapter = new CourseListAdapter(courses, UserSession.getInstance(getContext()).getUserType(), this::navigateToCourseDetail);
        courseRecyclerView.setAdapter(courseAdapter);

        emptyStateTextView = binding.emptyStateTextView;
    }

    // Fetch courses from backend
    private void fetchCoursesFromBackend() {
        FacultyApiService facultyApiService = new FacultyApiService(getContext());
        String facultyNetId = UserSession.getInstance(getContext()).getNetId();

        facultyApiService.getFacultySchedules(facultyNetId, new FacultyApiService.ScheduleResponseListener() {
            @Override
            public void onResponse(List<Schedule> schedules) {
                courses.clear();
                for (Schedule schedule : schedules) {
                    courses.add(schedule.getCourse());
                }
                courseAdapter.notifyDataSetChanged();

                // Show empty state if no courses are found
                if (courses.isEmpty()) {
                    emptyStateTextView.setVisibility(View.VISIBLE);
                    courseRecyclerView.setVisibility(View.GONE);
                } else {
                    emptyStateTextView.setVisibility(View.GONE);
                    courseRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), "Error fetching courses: " + message, Toast.LENGTH_SHORT).show();
                emptyStateTextView.setVisibility(View.VISIBLE);
                courseRecyclerView.setVisibility(View.GONE);
            }
        });
    }

    // Navigate to CourseDetailFragment
    private void navigateToCourseDetail(long scheduleId) {
        scheduleId = 7L; // hardcoded schedule ID for testing
        if (scheduleId == 0) {
            Log.e(TAG, "scheduleId is null; cannot navigate to CourseDetailFragment");
            Toast.makeText(getContext(), "Error: scheduleId is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle args = new Bundle();
        args.putLong("scheduleId", scheduleId); // Ensure the ID is passed

        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
        navController.navigate(R.id.action_coursesFragment_to_courseDetailFragment, args);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}
