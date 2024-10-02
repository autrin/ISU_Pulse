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

    private static final String URL_STRING_REQ = "https://8a50931b-2282-4c0b-b239-6544d8d72ce3.mock.pstmn.io";
    private Context context;

    public ApiService(Context context) {
        this.context = context;
    }

    public interface TaskResponseListener {
        void onResponse(List<ListTaskObject> tasks);
        void onError(String message);
    }

    public void getTasksDueToday(TaskResponseListener listener) {
        JsonArrayRequest jsonArrReq = new JsonArrayRequest(Request.Method.GET, URL_STRING_REQ, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("Response: ", response.toString());
                List<ListTaskObject> tasks = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        Long cId = jsonObject.getLong("cId");
                        Long tId = jsonObject.getLong("tId");
                        String section = jsonObject.getString("section");
                        String title = jsonObject.getString("title");
                        String description = jsonObject.getString("description");
                        Date date = Date.valueOf(jsonObject.getString("date"));
                        String taskType = jsonObject.getString("taskType");
                        ListTaskObject task = new ListTaskObject(cId, tId, section, title, description, date, taskType);
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