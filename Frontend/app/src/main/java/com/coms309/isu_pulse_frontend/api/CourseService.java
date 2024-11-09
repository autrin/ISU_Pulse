package com.coms309.isu_pulse_frontend.api;

import static com.coms309.isu_pulse_frontend.api.Constants.BASE_URL;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.coms309.isu_pulse_frontend.schedule.Schedule;
import com.coms309.isu_pulse_frontend.ui.home.Course;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CourseService {
    private RequestQueue requestQueue;

    public CourseService(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public interface EnrollCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    public interface GetEnrolledCoursesCallback {
        void onSuccess(List<Schedule> courses);
        void onError(String error);
    }

    public interface RemoveEnrollCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    public interface GetMutualCoursesCallback {
        void onSuccess(List<Course> courses);
        void onError(String error);
    }

    public void getMutualCourses(String user1NetId, String user2NetId, final GetMutualCoursesCallback callback) {
        String url = BASE_URL + "schedule/coursesInMutual?user1NetId=" + user1NetId + "&user2NetId=" + user2NetId;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<Course> courses = new ArrayList<>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject courseJson = response.getJSONObject(i);
                                JSONObject departmentJson = courseJson.getJSONObject("department");
                                Course course = new Course(courseJson.getString("code"),
                                        courseJson.getString("title"),
                                        courseJson.getString("description"),
                                        courseJson.getInt("credits"),
                                        departmentJson.getString("name"),
                                        departmentJson.getString("location"));
                                courses.add(course);
                            }
                            callback.onSuccess(courses);
                        } catch (JSONException e) {
                            callback.onError("Error parsing JSON response: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError("Error fetching mutual courses: " + error.getMessage());
                    }
                });

        requestQueue.add(jsonArrayRequest);

    }

    public void getEnrolledCoursesById(String studentId, final GetEnrolledCoursesCallback callback) {
        String url = BASE_URL + "enroll/getEnroll/" + studentId;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<Schedule> schedules = new ArrayList<>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject scheduleJson = response.getJSONObject(i);
                                JSONObject courseJson = scheduleJson.getJSONObject("course");
                                JSONObject departmentJson = courseJson.getJSONObject("department");
                                Course course = new Course(courseJson.getString("code"),
                                        courseJson.getString("title"),
                                        courseJson.getString("description"),
                                        courseJson.getInt("credits"),
                                        departmentJson.getString("name"),
                                        departmentJson.getString("location"));

                                Schedule schedule = new Schedule(
                                        course,
                                        scheduleJson.getString("section"),
                                        scheduleJson.getString("recurringPattern"),
                                        scheduleJson.getString("startTime"),
                                        scheduleJson.getString("endTime")
                                );
                                schedules.add(schedule);
                            }
                            callback.onSuccess(schedules);
                        } catch (JSONException e) {
                            callback.onError("Error parsing JSON response: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError("Error fetching enrolled courses: " + error.getMessage());
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

//    public void enrollInCourse(String studentId, int courseId, final EnrollCallback callback) {
//        String url = BASE_URL + "enroll/addEnroll/" + studentId + "?course=" + courseId;
//
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        callback.onSuccess(response);
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        callback.onError("Error enrolling in course: " + error.getMessage());
//                    }
//                });
//
//        requestQueue.add(stringRequest);
//    }

//    public void removeEnrollById(String studentId, int courseId, final RemoveEnrollCallback callback) {
//        String url = BASE_URL + "enroll/removeEnroll/" + studentId + "?c_id=" + courseId;
//
//        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        callback.onSuccess(response);
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        String errorMsg = "Error removing enrollment: ";
//                        if (error.networkResponse != null && error.networkResponse.data != null) {
//                            try {
//                                errorMsg += new String(error.networkResponse.data, "UTF-8");
//                            } catch (Exception e) {
//                                errorMsg += "Unable to parse error response.";
//                            }
//                        } else {
//                            errorMsg += error.getMessage();
//                        }
//                        callback.onError(errorMsg);
//                    }
//                });
//
//        requestQueue.add(stringRequest);
//    }
}