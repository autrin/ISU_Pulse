package com.coms309.isu_pulse_frontend.ui.courses;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.databinding.FragmentCourseDetailBinding;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;
import com.coms309.isu_pulse_frontend.ui.announcements.AnnouncementsFragment;
import com.coms309.isu_pulse_frontend.ui.announcements.TeacherAnnouncementsFragment;

public class CourseDetailFragment extends Fragment {

    private FragmentCourseDetailBinding binding;
    private long courseId;
    private static final String TAG = "CourseDetailFragment";
    private long scheduleId = 7L; // hardcoded schedule ID for testing

    public static CourseDetailFragment newInstance(long courseId) {
        courseId = 2L; // hardcoded course ID for testing
        CourseDetailFragment fragment = new CourseDetailFragment();
        Bundle args = new Bundle();
        args.putLong("courseId", courseId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCourseDetailBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (getArguments() != null) {
//            courseId = getArguments().getLong("courseId", -1);
//            binding.courseTitle.setText("Course ID: " + courseId);
            scheduleId = getArguments().getLong("scheduleId", 7L); // hardcoded schedule ID for testing
            binding.courseTitle.setText("Schedule ID: " + scheduleId); // Display schedule ID for reference
            // Load announcements for this schedule
//            loadAnnouncementsForSchedule(scheduleId);
        }

        if (scheduleId == -1) {
            Log.e(TAG, "Invalid scheduleId received");
            Toast.makeText(getContext(), "Error: Invalid schedule ID", Toast.LENGTH_SHORT).show();
            return root;
        }
        binding.courseTitle.setText("Schedule ID: " + scheduleId); // Display schedule ID for reference

        // Setup Dropdown (Spinner) listener
        binding.courseDetailDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // Show Announcements Fragment
                        showAnnouncementsFragment();
                        break;
                    case 1:
                        // Placeholder for People Fragment (not implemented yet)
                        // Uncomment the line below once PeopleFragment is implemented
                        // showPeopleFragment();
                        break;
                    case 2:
                        // Placeholder for Tasks Fragment (not implemented yet)
                        // Uncomment the line below once TasksFragment is implemented
                        // showTasksFragment();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showAnnouncementsFragment() {
        Fragment fragment;
        String userRole = UserSession.getInstance(getContext()).getUserType();

        // Decide which fragment to show based on user type
        if ("FACULTY".equals(userRole)) {
//            Toast.makeText(getContext(), "Showing Teacher Announcements Fragment", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Showing Teacher Announcements Fragment. User Role: " + userRole);
            fragment = TeacherAnnouncementsFragment.newInstance(scheduleId); // Ensure courseId is set correctly
        } else {
            fragment = AnnouncementsFragment.newInstance(scheduleId);
        }

        // Replace the content with the selected fragment
        getChildFragmentManager().beginTransaction()
                .replace(R.id.courseDetailContent, fragment)
                .commit();
    }

    // Utility method to replace the current fragment in the container
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(binding.courseDetailContent.getId(), fragment);
        transaction.addToBackStack(null); // Optional: adds the transaction to the back stack
        transaction.commit();
    }


}
