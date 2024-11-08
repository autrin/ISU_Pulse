package com.coms309.isu_pulse_frontend.ui.courses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

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

    public static CourseDetailFragment newInstance(Long courseId) {
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
            courseId = getArguments().getLong("courseId", -1);
            binding.courseTitle.setText("Course ID: " + courseId);
        }

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

        // Check user role to decide which fragment to load
        String userRole = UserSession.getInstance(getContext()).getUserType();
        if ("FACULTY".equals(userRole)) {
            fragment = new TeacherAnnouncementsFragment();
        } else {
            fragment = AnnouncementsFragment.newInstance(courseId);  // If you need to pass courseId for students
        }

        getChildFragmentManager().beginTransaction()
                .replace(R.id.courseDetailContent, fragment)
                .commit();
    }

    // Uncomment these methods and the corresponding code in `onItemSelected`
    // once PeopleFragment and TasksFragment are implemented

    /*
    // Helper method to show PeopleFragment
    private void showPeopleFragment() {
        PeopleFragment peopleFragment = PeopleFragment.newInstance(courseId);
        replaceFragment(peopleFragment);
    }

    // Helper method to show TasksFragment
    private void showTasksFragment() {
        TasksFragment tasksFragment = TasksFragment.newInstance(courseId);
        replaceFragment(tasksFragment);
    }
    */

    // Utility method to replace the current fragment in the container
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(binding.courseDetailContent.getId(), fragment);
        transaction.addToBackStack(null); // Optional: adds the transaction to the back stack
        transaction.commit();
    }
}
