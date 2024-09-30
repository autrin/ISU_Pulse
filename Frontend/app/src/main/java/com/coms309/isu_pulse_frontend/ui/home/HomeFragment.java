package com.coms309.isu_pulse_frontend.ui.home;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.coms309.isu_pulse_frontend.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private TextView textViewTasksDueTodayTitle;
    private TextView textViewAnnouncementTitle;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        textViewAnnouncementTitle = binding.TextViewAnnouncementTitle;
        textViewAnnouncementTitle.setText("Announcements");
        textViewAnnouncementTitle.setTextSize(25);
        textViewAnnouncementTitle.setTypeface(null, Typeface.BOLD); // make it bold

        textViewTasksDueTodayTitle = binding.textViewTasksDueToday;
        textViewTasksDueTodayTitle.setText("Tasks Due Today");
        textViewTasksDueTodayTitle.setTextSize(25);
        textViewTasksDueTodayTitle.setTypeface(null, Typeface.BOLD);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}