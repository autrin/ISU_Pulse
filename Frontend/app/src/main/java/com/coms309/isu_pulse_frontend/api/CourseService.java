package com.coms309.isu_pulse_frontend.api;

import static com.coms309.isu_pulse_frontend.api.Constants.BASE_URL;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.coms309.isu_pulse_frontend.ui.home.Course;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseService {
    private RequestQueue requestQueue;

    public CourseService(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public interface EnrollCallback {
        void onSuccess(boolean enrolled);
        void onError(String error);
    }

    public interface GetEnrolledCoursesCallback {
        void onSuccess(List<Course> courses);
        void onError(String error);
    }

    public interface RemoveEnrollCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    public void getEnrolledCourses(String studentid, final GetEnrolledCoursesCallback callback) {
        String url = BASE_URL + "enroll/getEnroll/" + studentid;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<Course> courses = new ArrayList<>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject courseJson = response.getJSONObject(i);
                                JSONObject departmentJson = courseJson.getJSONObject("department");

                                Course course = new Course(
                                        courseJson.getString("code"),
                                        courseJson.getString("title"),
                                        courseJson.getString("description"),
                                        courseJson.getInt("credits"),
                                        courseJson.getInt("numSections"),
                                        departmentJson.getString("name"),
                                        departmentJson.getString("location"),
                                        departmentJson.getInt("did"),
                                        courseJson.getInt("cid")
                                );
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
                        callback.onError("Error fetching enrolled courses: " + error.getMessage());
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

    public void enrollInCourse(String sId, int courseId, final EnrollCallback callback) {
        String url = BASE_URL + "enroll/addEnroll/" + sId + "?course=" + courseId;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        boolean enrolled = Boolean.parseBoolean(response);
                        callback.onSuccess(enrolled);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError("Error enrolling in course: " + error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("netId", sId);
                params.put("course", String.valueOf(courseId));
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void removeEnroll(String studentid, int courseid, final RemoveEnrollCallback callback) {
        String url = BASE_URL + "enroll/removeEnroll/" + studentid + "?c_id=" + courseid;

        // Assuming the backend returns a success message
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                callback::onSuccess,
                error -> {
                    String errorMsg = "Error removing enrollment: ";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String body;
                        try {
                            body = new String(error.networkResponse.data, "UTF-8");
                        } catch (Exception e) {
                            body = "Unable to parse error response.";
                        }
                        errorMsg += body;
                    } else {
                        errorMsg += error.getMessage();
                    }
                    callback.onError(errorMsg);
                }
        );

        requestQueue.add(stringRequest);
    }
}