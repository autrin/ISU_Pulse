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
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;
import com.coms309.isu_pulse_frontend.model.Announcement;
import com.coms309.isu_pulse_frontend.model.Course;
import com.coms309.isu_pulse_frontend.model.Schedule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for interacting with the Faculty API.
 * This service provides methods to fetch faculty schedules and announcements.
 */
public class FacultyApiService {

    private static final String TAG = "FacultyApiService";
    private static final String BASE_URL_Faculty = BASE_URL + "faculty/schedules/";
    private RequestQueue requestQueue;

    /**
     * Constructs a new FacultyApiService with the provided context.
     *
     * @param context the application context
     */
    public FacultyApiService(Context context) {
        this.requestQueue = Volley.newRequestQueue(context);
    }

    /**
     * Fetches faculty schedules for a given faculty member identified by their NetID.
     *
     * @param netId    the NetID of the faculty member
     * @param listener the callback listener to handle the response
     */
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

    /**
     * Parses a JSON object into a Schedule object.
     *
     * @param jsonObject the JSON object representing a schedule
     * @return the parsed Schedule object
     * @throws Exception if an error occurs during parsing
     */
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

    /**
     * Listener interface for handling schedule responses.
     */
    public interface ScheduleResponseListener {
        /**
         * Called when schedules are successfully fetched.
         *
         * @param schedules the list of schedules
         */
        void onResponse(List<Schedule> schedules);

        /**
         * Called when an error occurs during the request.
         *
         * @param message the error message
         */
        void onError(String message);
    }

    /**
     * Fetches announcements for a given schedule.
     *
     * @param scheduleId the ID of the schedule
     * @param netId      the NetID of the user
     * @param listener   the callback listener to handle the response
     */
    public void getAnnouncementsBySchedule(long scheduleId, String netId, final AnnouncementResponseListener listener) {
        String url = BASE_URL + "announcements/schedule/" + scheduleId;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    List<Announcement> announcements = new ArrayList<>();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject announcementObj = response.getJSONObject(i);

                            // Access the schedule object and get the schedule ID
                            JSONObject scheduleObj = announcementObj.getJSONObject("schedule");
                            long extractedScheduleId = scheduleObj.getLong("id");

                            Announcement announcement = new Announcement(
                                    announcementObj.getLong("id"),
                                    announcementObj.getString("content"),
                                    extractedScheduleId,
                                    netId,
                                    announcementObj.getString("timestamp"),
                                    ""
                            );
                            announcements.add(announcement);
                        }
                        listener.onResponse(announcements);
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing announcements", e);
                        listener.onError("Parsing error");
                    }
                },
                error -> listener.onError("Network error")
        );

        requestQueue.add(jsonArrayRequest);
    }

}
