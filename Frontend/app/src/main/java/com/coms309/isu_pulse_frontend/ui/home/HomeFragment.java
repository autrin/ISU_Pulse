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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coms309.isu_pulse_frontend.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private TextView textViewTasksDueTodayTitle;
    private TextView textViewAnnouncementTitle;

    private List<String> tasksDueToday = new ArrayList<>();
    private List<String> events = new ArrayList<>();
    private List<String> announcements = new ArrayList<>();

    public final String URL_STRING_REQ = "http://localhost:8080/tasksduetoday";

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

        // Create a weekly calendar that will show the tasks and events for each day on the calendar
        RecyclerView recyclerView = binding.recyclerViewWeeklyCalendar;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        List<String> days = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
        WeeklyCalendarAdapter adapter = new WeeklyCalendarAdapter(days, tasksDueToday, events);
        recyclerView.setAdapter(adapter);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void populateTasksDueToday(){
        // First roundtrip to the server to get the tasks due today
        // tasksDueToday = getTasksDueToday();
    }
    public void getTasksDueToday(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_STRING_REQ, new Response.Listener<String>() {
            @Override
            public void onResponse(String response){
                VolleyLog.d("Response: " + response.toString());
                tasksDueToday.add(response);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                VolleyLog.e("Error: " + error.getMessage());
            }
        });
    }
}