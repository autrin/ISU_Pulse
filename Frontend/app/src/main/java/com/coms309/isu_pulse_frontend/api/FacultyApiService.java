package com.coms309.isu_pulse_frontend.api;

import static com.coms309.isu_pulse_frontend.api.Constants.BASE_URL;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.coms309.isu_pulse_frontend.model.Announcement;
import com.coms309.isu_pulse_frontend.model.Course;
import com.coms309.isu_pulse_frontend.model.Schedule;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FacultyApiService {

    private static final String TAG = "FacultyApiService";
    private static final String BASE_URL_Faculty = BASE_URL + "faculty/schedules/";
    private RequestQueue requestQueue;

    public FacultyApiService(Context context) {
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public void getFacultySchedules(String netId, final ScheduleResponseListener listener) {
        String url = BASE_URL_Faculty + netId;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<Schedule> schedules = new ArrayList<>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject scheduleObj = response.getJSONObject(i);
                                Schedule schedule = parseSchedule(scheduleObj);
                                schedules.add(schedule);
                            }
                            listener.onResponse(schedules);
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing JSON response", e);
                            listener.onError("Parsing error");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Volley error", error);
                        listener.onError("Network error");
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

    private Schedule parseSchedule(JSONObject jsonObject) throws Exception {
        long scheduleId = jsonObject.getLong("id");
        JSONObject courseObj = jsonObject.getJSONObject("course");
        Course course = new Course(
                courseObj.getLong("id"),
                courseObj.getString("title"),
                courseObj.getString("code")
        );
        return new Schedule(
                scheduleId,
                course,
                jsonObject.getString("section"),
                jsonObject.getString("recurringPattern"),
                jsonObject.getString("startTime"),
                jsonObject.getString("endTime")
        );
    }


    public interface ScheduleResponseListener {
        void onResponse(List<Schedule> schedules);

        void onError(String message);
    }

//    public void getAnnouncementsBySchedule(long scheduleId, final AnnouncementResponseListener listener) {
//        String url = BASE_URL + "announcements/schedule/" + scheduleId;
//
//        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
//                Request.Method.GET,
//                url,
//                null,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        List<Announcement> announcements = new ArrayList<>();
//                        try {
//                            for (int i = 0; i < response.length(); i++) {
//                                JSONObject announcementObj = response.getJSONObject(i);
//                                Announcement announcement = new Announcement(
//                                        announcementObj.getLong("id"),
//                                        announcementObj.getString("content"),
//                                        announcementObj.getLong("scheduleId"),
//                                        announcementObj.getString("facultyNetId"),
//                                        announcementObj.getString("timestamp"),
//                                        ""
//                                );
//                                announcements.add(announcement);
//                            }
//                            listener.onResponse(announcements);
//                        } catch (JSONException e) {
//                            Log.e(TAG, "Error parsing announcements", e);
//                            listener.onError("Parsing error");
//                        }
//                    }
//                },
//                error -> listener.onError("Network error")
//        );
//
//        requestQueue.add(jsonArrayRequest);
//    }



}
