package com.coms309.isu_pulse_frontend.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.coms309.isu_pulse_frontend.ui.home.ListTaskObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class ApiService {

    private static final String URL_STRING_REQ = "https://ae21ce63-e030-423f-aa6f-a80597a900cf.mock.pstmn.io";
    private static final String URL_STRING_REQ_VM = "http://coms-3090-042.class.las.iastate.edu:8080/task/getTaskByUserIn2days/n001";
    private Context context;

    public ApiService(Context context) {
        this.context = context;
    }

    public interface TaskResponseListener {
        void onResponse(List<ListTaskObject> tasks);
        void onError(String message);
    }

    public void getTasksDueToday(TaskResponseListener listener) {
        String url = URL_STRING_REQ + "/tasksduetoday";
        JsonArrayRequest jsonArrReq = new JsonArrayRequest(Request.Method.GET, URL_STRING_REQ_VM, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("Response: ", response.toString());
                List<ListTaskObject> tasks = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        // Extracting task-related properties
                        Long cId = jsonObject.getLong("cId");
                        Long tId = jsonObject.getLong("tId");
                        String section = jsonObject.getString("section");
                        String title = jsonObject.getString("title");
                        String description = jsonObject.getString("description");
                        Date dueDate = Date.valueOf(jsonObject.getString("dueDate"));
                        String taskType = jsonObject.getString("taskType");

                        // Extracting additional fields
                        String courseName = jsonObject.getString("courseName");
                        int credits = jsonObject.getInt("credits");
                        int mediumTerm = jsonObject.getInt("mediumTerm");
                        String roomName = jsonObject.getString("roomName");
                        String building = jsonObject.getString("building");

                        // Creating the task object with updated fields
                        ListTaskObject task = new ListTaskObject(cId, tId, section, title, description, dueDate, taskType, courseName, credits, mediumTerm, roomName, building);
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
}