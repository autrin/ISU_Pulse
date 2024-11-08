package com.coms309.isu_pulse_frontend.ui.courses;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.adapters.CourseListAdapter;
import com.coms309.isu_pulse_frontend.api.FacultyApiService;
import com.coms309.isu_pulse_frontend.databinding.FragmentCoursesBinding;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;
import com.coms309.isu_pulse_frontend.model.Course;
import com.coms309.isu_pulse_frontend.model.Schedule;

import java.util.ArrayList;
import java.util.List;

public class CoursesFragment extends Fragment {

    private FragmentCoursesBinding binding;
    private RecyclerView recyclerView;
    private CourseListAdapter adapter;
    private List<Course> courses = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCoursesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Get the user role
        String userRole = UserSession.getInstance(getContext()).getUserType();

        // Set up RecyclerView
        recyclerView = binding.recyclerViewCourses;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CourseListAdapter(courses, userRole, this::navigateToCourseDetail);
        recyclerView.setAdapter(adapter);

        // Fetch courses from backend
        fetchCoursesFromBackend();

        return root;
    }

    // Function to fetch courses using FacultyApiService
    private void fetchCoursesFromBackend() {
        FacultyApiService facultyApiService = new FacultyApiService(getContext());
        String facultyNetId = UserSession.getInstance(getContext()).getNetId();

        facultyApiService.getFacultySchedules(facultyNetId, new FacultyApiService.ScheduleResponseListener() {
            @Override
            public void onResponse(List<Schedule> schedules) {
                courses.clear();
                for (Schedule schedule : schedules) {
                    courses.add(schedule.getCourse()); // Assuming each Schedule contains a Course object
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), "Error fetching courses: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Function to navigate to CourseDetailFragment, only used by teachers
    private void navigateToCourseDetail(Long courseId) {
        String userRole = UserSession.getInstance(getContext()).getUserType();
        if ("FACULTY".equals(userRole)) {
            Bundle args = new Bundle();
            args.putLong("courseId", courseId);

            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_coursesFragment_to_courseDetailFragment, args);
        }
    }

    // Clean up binding
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
