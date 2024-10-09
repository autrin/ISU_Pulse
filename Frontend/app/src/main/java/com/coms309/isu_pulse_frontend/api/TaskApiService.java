package com.coms309.isu_pulse_frontend.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.coms309.isu_pulse_frontend.ui.home.ListTaskObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskApiService {

    private static final String URL_STRING_REQ = "https://65e24ca1-c2ef-4182-97c5-5133a65636e4.mock.pstmn.io/tasksdue";
    private static final String URL_STRING_REQ_VM = "http://coms-3090-042.class.las.iastate.edu:8080/task/n001"; //TODO: needs to use the user's id instead of the hardcoded one later
    private Context context;

    public TaskApiService(Context context) {
        this.context = context;
    }

    public interface TaskResponseListener {
        void onResponse(List<ListTaskObject> tasks);

        void onError(String message);
    }

    /**
     * Fetches tasks due today from the backend
     * @param listener the listener to handle the response
     */
    public void getTasksDueToday(TaskResponseListener listener) {
//        String url = URL_STRING_REQ + "/tasksduetoday";
        JsonArrayRequest jsonArrReq = new JsonArrayRequest(Request.Method.GET, URL_STRING_REQ_VM, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("Response: ", response.toString());
                List<ListTaskObject> tasks = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        // Extracting task-related properties
                        Long cId = jsonObject.getJSONObject("course").getLong("cid");
                        String tId = jsonObject.getString("tId");
                        String section = jsonObject.getString("section");
                        String title = jsonObject.getString("title");
                        String description = jsonObject.getString("description");
                        Date dueDate = Date.valueOf(jsonObject.getString("dueDate").split("T")[0]);
                        String taskType = jsonObject.getString("taskType");
                        String courseCode = jsonObject.getJSONObject("course").getString("code");
                        String courseTitle = jsonObject.getJSONObject("course").getString("title");
                        String departmentName = jsonObject.getJSONObject("course").getJSONObject("department").getString("name");
                        String departmentLocation = jsonObject.getJSONObject("course").getJSONObject("department").getString("location");

                        // Creating the task object with updated fields
                        ListTaskObject task = new ListTaskObject(cId, tId, section, title, description, dueDate, taskType, courseCode, courseTitle, departmentName, departmentLocation);
                        tasks.add(task);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                listener.onResponse(tasks);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = error.getMessage() != null ? error.getMessage() : "Unknown error";
                VolleyLog.e("Error: " + errorMessage);
                listener.onError(errorMessage);
            }
        });

        // Add the request to the RequestQueue
        Volley.newRequestQueue(context).add(jsonArrReq);
    }

    /**
     * Update the task with the given task object
     * @param task the task object to update
     */
    public void updateTask(ListTaskObject task) {
        String url = URL_STRING_REQ_VM;
        JSONObject body = new JSONObject();
        try {
            body.put("cId", task.getcId());
            body.put("tId", task.gettId());
            body.put("section", task.getSection());
            body.put("title", task.getTitle());
            body.put("description", task.getDescription());
            body.put("dueDate", task.getDueDate().toString());
            body.put("taskType", task.getTaskType());
            body.put("courseCode", task.getCourseCode());
            body.put("courseTitle", task.getCourseTitle());
            body.put("departmentName", task.getDepartmentName());
            body.put("departmentLocation", task.getDepartmentLocation());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response: ", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = error.getMessage() != null ? error.getMessage() : "Unknown error";
                VolleyLog.e("Error: " + errorMessage);
            }
        }) {
            @Override
            public byte[] getBody() {
                try {
                    return body.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", body.toString(), "utf-8");
                    return null;
                }
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }

    /**
     * Create a new task with the given task object using a POST request
     * @param task the task object to create
     */
    public void createTask(ListTaskObject task) {
        JSONObject body = new JSONObject();
        try{
            body.put("cId", task.getcId());
            body.put("tId", task.gettId());
            body.put("section", task.getSection());
            body.put("title", task.getTitle());
            body.put("description", task.getDescription());
            body.put("dueDate", task.getDueDate().toString());
            body.put("taskType", task.getTaskType());
            body.put("courseCode", task.getCourseCode());
            body.put("courseTitle", task.getCourseTitle());
            body.put("departmentName", task.getDepartmentName());
            body.put("departmentLocation", task.getDepartmentLocation());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_STRING_REQ_VM, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response: ", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error){
                String errorMessage = error.getMessage() != null ? error.getMessage(): "Unknown error";
                VolleyLog.e("Error: " + errorMessage);
            }
        }){
            @Override
            public byte[] getBody(){
                try{
                    return body.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", body.toString(), "utf-8");
                    return null;
                }
            }
            @Override
            public Map<String, String> getHeaders(){
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }

    /**
     * Delete the task with the given task object
     * @param task the task object to delete
     */
    public void deleteTask(ListTaskObject task){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, URL_STRING_REQ_VM + "/" + task.gettId(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response: ", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error){
                String errorMessage = error.getMessage() != null ? error.getMessage(): "Unknown error";
                VolleyLog.e("Error: " + errorMessage);
            }
        });
        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }
}