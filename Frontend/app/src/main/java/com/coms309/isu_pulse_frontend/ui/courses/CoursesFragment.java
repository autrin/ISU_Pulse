package com.coms309.isu_pulse_frontend.ui.courses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.adapters.CourseListAdapter;
import com.coms309.isu_pulse_frontend.databinding.FragmentCoursesBinding;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;
import com.coms309.isu_pulse_frontend.model.Course;

import java.util.ArrayList;
import java.util.List;

public class CoursesFragment extends Fragment {

    private FragmentCoursesBinding binding;
    private RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCoursesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Get the user role
        String userRole = UserSession.getInstance(getContext()).getUserType();

        // Set up RecyclerView
        recyclerView = binding.recyclerViewCourses;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Use sample data instead of getCourses()
        List<Course> sampleCourses = getSampleCourses();
        CourseListAdapter adapter = new CourseListAdapter(sampleCourses, userRole, this::navigateToCourseDetail);
        recyclerView.setAdapter(adapter);

        return root;
    }

    // Function to navigate to CourseDetailFragment, only used by teachers
    private void navigateToCourseDetail(Long courseId) {
        Bundle args = new Bundle();
        args.putLong("courseId", courseId);

        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
        navController.navigate(R.id.action_coursesFragment_to_courseDetailFragment, args);
    }

    // Clean up binding
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Temporary method to generate sample courses
    private List<Course> getSampleCourses() {
        List<Course> courses = new ArrayList<>();
        courses.add(new Course(1L, "Introduction to Computer Science", "Section A"));
        courses.add(new Course(2L, "Data Structures", "Section B"));
        courses.add(new Course(3L, "Algorithms", "Section C"));
        courses.add(new Course(4L, "Database Systems", "Section D"));
        courses.add(new Course(5L, "Operating Systems", "Section E"));
        return courses;
    }
}


