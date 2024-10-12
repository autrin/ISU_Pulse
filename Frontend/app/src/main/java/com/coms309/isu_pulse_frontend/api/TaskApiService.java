package com.coms309.isu_pulse_frontend.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.coms309.isu_pulse_frontend.ui.home.Course;
import com.coms309.isu_pulse_frontend.ui.home.CourseTask;
import com.coms309.isu_pulse_frontend.ui.home.Department;
import com.coms309.isu_pulse_frontend.ui.home.PersonalTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class TaskApiService {

    private static final String BASE_URL = "http://coms-3090-042.class.las.iastate.edu:8080";
    private static final String NET_ID = "n001";
    private Context context;
    private RequestQueue requestQueue;

    public TaskApiService(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    public interface TaskResponseListener {
        void onResponse(List<Object> tasks);
        void onError(String message);
    }

    public void getTasksDueToday(final TaskResponseListener listener) {
        String url = BASE_URL + "/task" +"/getTaskByUserIn2days/" + "n001";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<Object> tasks = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                // Extract task details
                                String tId = jsonObject.getString("tId");
                                int section = jsonObject.getInt("section");
                                String title = jsonObject.getString("title");
                                String description = jsonObject.getString("description");
                                Date dueDate = Date.valueOf(jsonObject.getString("dueDate").split("T")[0]);
                                String taskType = jsonObject.getString("taskType");

                                // Extract course details
                                JSONObject courseJson = jsonObject.getJSONObject("course");
                                String courseCode = courseJson.getString("code");
                                String courseTitle = courseJson.getString("title");
                                String courseDescription = courseJson.getString("description");
                                int courseCredits = courseJson.getInt("credits");
                                int courseNumSections = courseJson.getInt("numSections");

                                // Extract department details
                                JSONObject departmentJson = courseJson.getJSONObject("department");
                                String departmentName = departmentJson.getString("name");
                                String departmentLocation = departmentJson.getString("location");
                                int departmentId = departmentJson.getInt("did");

                                Department department = new Department(departmentName, departmentLocation, departmentId);
                                Course course = new Course(courseCode, courseTitle, courseDescription, courseCredits, courseNumSections, department, courseJson.getInt("cid"));
                                CourseTask task = new CourseTask(tId, section, title, description, dueDate, taskType, course, department);
                                tasks.add(task);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        fetchPersonalTasks(tasks, listener);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = error.getMessage() != null ? error.getMessage() : "Unknown error";
                        Log.e("API Error", errorMessage);
                        listener.onError(errorMessage);
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

    private void fetchPersonalTasks(final List<Object> tasks, final TaskResponseListener listener) {
        String personalTasksUrl = BASE_URL + "/personalTask/getPersonalTasks/" + NET_ID;
        JsonArrayRequest personalTasksRequest = new JsonArrayRequest(Request.Method.GET, personalTasksUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String title = jsonObject.getString("title");
                                String description = jsonObject.getString("description");
                                long dueDate = jsonObject.getLong("dueDate");
                                String userNetId = jsonObject.getString("userNetId");

                                PersonalTask task = new PersonalTask(null, title, description, dueDate, userNetId);
                                tasks.add(task);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        listener.onResponse(tasks);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = error.getMessage() != null ? error.getMessage() : "Unknown error";
                        Log.e("API Error", errorMessage);
                        listener.onError(errorMessage);
                    }
                });

        requestQueue.add(personalTasksRequest);
    }

    public void getLastPersonalTask(final TaskResponseListener listener) { // For later to get the last task id
        String url = BASE_URL + "/personalTask/getLastPersonalTask/" + NET_ID;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int id = response.getInt("personalTaskId");
                            List<Object> idList = new ArrayList<>();
                            idList.add(id);
                            listener.onResponse(idList);
                        } catch (Exception e) {
                            e.printStackTrace();
                            listener.onError("Failed to parse response");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = error.getMessage() != null ? error.getMessage() : "Unknown error";
                        Log.e("API Error", errorMessage);
                        listener.onError(errorMessage);
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    public void createPersonalTask(PersonalTask task) {
        String url = BASE_URL + "/personalTask/addPersonalTask/" + NET_ID + "?title=" + task.getTitle() + "&description=" + task.getDescription() + "&dueDateTimestamp=" + task.getDueDate();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response: ", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = error.getMessage() != null ? error.getMessage() : "Unknown error";
                        Log.e("API Error", errorMessage);
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    public void updatePersonalTask(PersonalTask task) {
        String url = BASE_URL + "/personalTask/updatePersonalTask/" + NET_ID + "?taskId=" + task.getId() +
                "&title=" + task.getTitle() + "&description=" + task.getDescription() +
                "&dueDateTimestamp=" + task.getDueDate();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response: ", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = error.getMessage() != null ? error.getMessage() : "Unknown error";
                        Log.e("API Error", errorMessage);
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    public void deletePersonalTask(PersonalTask task, final TaskResponseListener listener) {
        String url = BASE_URL + "/personalTask/deletePersonalTask/" + NET_ID + "/" + task.getId();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response: ", response.toString());
                        listener.onResponse(null);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = error.getMessage() != null ? error.getMessage() : "Unknown error";
                        Log.e("API Error", errorMessage);
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }
    public void deleteCourseTask(CourseTask task, final TaskResponseListener listener) {
        String url = BASE_URL + "/deleteCourseTask/" + NET_ID + "/" + task.gettId();

        JSONObject body = new JSONObject();
        try {
            body.put("title", task.getTitle());
            body.put("description", task.getDescription());
            body.put("dueDate", task.getDueDate().toString());
            body.put("courseId", task.getCourse().getcId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response: ", response.toString());
                        listener.onResponse(null);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = error.getMessage() != null ? error.getMessage() : "Unknown error";
                        Log.e("API Error", errorMessage);
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }
}