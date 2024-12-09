package com.coms309.isu_pulse_frontend.api;

import static com.coms309.isu_pulse_frontend.api.Constants.BASE_URL;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;
import com.coms309.isu_pulse_frontend.model.CourseTask;
import com.coms309.isu_pulse_frontend.model.PersonalTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class TaskApiService {

    private String netId;
    private Context context;
    private RequestQueue requestQueue;

    public TaskApiService(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
        this.netId = UserSession.getInstance(context).getNetId();
    }

    public interface TaskResponseListener {
        void onResponse(List<Object> tasks);
        void onError(String message);
    }

    public void getTasksIn2days(final TaskResponseListener listener) {
        String url = BASE_URL + "task/getTaskByUserIn2days/" + netId;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("API Response", response.toString());  // Log the response
                        List<Object> tasks = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                // Extract task details
                                long id = jsonObject.getLong("id");
                                String title = jsonObject.getString("title");
                                String description = jsonObject.getString("description");
                                Date dueDate = Date.valueOf(jsonObject.getString("dueDate").split("T")[0]);
                                String taskType = jsonObject.getString("taskType");

                                CourseTask task = new CourseTask(id, title, description, dueDate, taskType);
                                tasks.add(task);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        fetchPersonalTasks(tasks, listener);
                        listener.onResponse(tasks);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("API Error", error.toString());
                        listener.onError(error.toString());
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

    private void fetchPersonalTasks(final List<Object> tasks, final TaskResponseListener listener) {
        String personalTasksUrl = BASE_URL + "personalTask/getPersonalTasks/" + netId;
        JsonArrayRequest personalTasksRequest = new JsonArrayRequest(Request.Method.GET, personalTasksUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Personal Tasks API", response.toString());  // Log personal tasks response
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                int taskId = jsonObject.getInt("id");
                                String title = jsonObject.getString("title");
                                String description = jsonObject.getString("description");
                                String dueDateString = jsonObject.getString("dueDate");  // Fetch date as a string
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                java.util.Date utilDate = dateFormat.parse(dueDateString); // Parse using java.util.Date
                                Date sqlDate = new Date(utilDate.getTime()); // Convert to java.sql.Date
                                long dueDateMillis = sqlDate.getTime();// Convert java.sql.Date to a long (milliseconds since epoch)

                                String userNetId = jsonObject.getJSONObject("user").getString("netId");
                                if (!userNetId.equals(netId)) {
                                    Toast.makeText(context, "userNetId mismatch between the json obj and userSession", Toast.LENGTH_SHORT).show();
                                    Log.e("getPersonalTasks API Error", "userNetId mismatch between the json obj and userSession");
                                }
                                PersonalTask task = new PersonalTask(taskId, title, description, dueDateMillis, userNetId);
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

//    public void getLastPersonalTask(final TaskResponseListener listener) {
//        String url = BASE_URL + "personalTask/getLastPersonalTaskID/" + netId;
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            long id = response.getInt("lastTaskId");
//                            List<Object> idList = new ArrayList<>();
//                            idList.add(id);
//                            listener.onResponse(idList);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            listener.onError("Failed to parse the response from the server.");
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        String errorMessage = error.getMessage() != null ? error.getMessage() : "Unknown error occurred while fetching the last task ID.";
//                        Log.e("API Error", errorMessage);
//                        listener.onError("Error fetching the last task ID: " + errorMessage);
//                    }
//                });
//
//        requestQueue.add(jsonObjectRequest);
//    }

    public void createPersonalTask(PersonalTask task) {
        // Ensure task properties are non-null
        if (task.getTitle() == null || task.getDescription() == null || task.getDueDate() == null) {
            Log.e("API Error", "Task properties cannot be null");
            return;
        }
        // Construct the URL with netId, title, description, and dueDateTimestamp
        String url = BASE_URL + "personalTask/addPersonalTask/" + netId +
                "?title=" + task.getTitle() +
                "&description=" + task.getDescription() +
                "&dueDateTimestamp=" + task.getDueDate();

        // Create the new task
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response: ", response);
                        Toast.makeText(context, "Task created successfully", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = error.getMessage() != null ? error.getMessage() : "Unknown error in CreatePersonalTask()";
                        Log.e("API Error", errorMessage);
                    }
                });

        // Add the request to the request queue
        requestQueue.add(stringRequest);
    }

    public void updatePersonalTask(PersonalTask task) {
        if (task.getId() == 0) { // ! Do I need to check for null?
            Log.e("API Error", "Task ID cannot be null or empty for updating.");
            return;
        }

        String url = BASE_URL + "personalTask/updatePersonalTask/" + netId +
                "?taskId=" + task.getId() +
                "&title=" + task.getTitle() +
                "&description=" + task.getDescription() +
                "&dueDateTimestamp=" + task.getDueDate(); // Unix timestamp

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
        String url = BASE_URL + "personalTask/deletePersonalTask/" + netId + "?taskId=" + task.getId();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response: ", response.toString());
                        listener.onResponse(null);  // Update UI on successful deletion
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = error.getMessage() != null ? error.getMessage() : "Unknown error in deletePersonalTask()";
                        Log.e("API Error", errorMessage);
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    public void deleteCourseTask(CourseTask task, final TaskResponseListener listener) {
        String url = BASE_URL + "deleteCourseTask/" + netId + "/" + task.getId();
        JSONObject body = new JSONObject();
        try {
            body.put("title", task.getTitle());
            body.put("description", task.getDescription());
            body.put("dueDate", task.getDueDate().toString());
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