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

import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    TextView announcementsTitle;
    TextView tasksDueTodayTitle;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        announcementsTitle = root.findViewById(R.id.textAnnouncementTitle);
        announcementsTitle.setText("Announcements");
        announcementsTitle.setTextSize(25);
        announcementsTitle.setTypeface(null, Typeface.BOLD);

        tasksDueTodayTitle = root.findViewById(R.id.textTasksDueTodayTitle);
        tasksDueTodayTitle.setText("Tasks Due Today");
        tasksDueTodayTitle.setTextSize(25);
        tasksDueTodayTitle.setTypeface(null, Typeface.BOLD);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}