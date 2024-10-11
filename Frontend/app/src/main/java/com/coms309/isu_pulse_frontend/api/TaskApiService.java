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
import com.coms309.isu_pulse_frontend.ui.home.Course;
import com.coms309.isu_pulse_frontend.ui.home.Department;
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

    private static final String URL_STRING_REQ_VM = "https://65e24ca1-c2ef-4182-97c5-5133a65636e4.mock.pstmn.io/tasksdue";
    private Context context;

    public TaskApiService(Context context) {
        this.context = context;
    }

    public interface TaskResponseListener {
        void onResponse(List<ListTaskObject> tasks);

        void onError(String message);
    }

    public void getTasksDueToday(TaskResponseListener listener) {
        JsonArrayRequest jsonArrReq = new JsonArrayRequest(Request.Method.GET, URL_STRING_REQ_VM, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("Response: ", response.toString());
                List<ListTaskObject> tasks = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        // Extracting task-related properties
                        String tId = jsonObject.getString("tId");
                        int section = jsonObject.getInt("section");
                        String title = jsonObject.getString("title");
                        String description = jsonObject.getString("description");
                        Date dueDate = Date.valueOf(jsonObject.getString("dueDate").split("T")[0]);
                        String taskType = jsonObject.getString("taskType");

                        // Extracting course-related properties
                        JSONObject courseJson = jsonObject.getJSONObject("course");
                        String courseCode = courseJson.getString("code");
                        String courseTitle = courseJson.getString("title");
                        String courseDescription = courseJson.getString("description");
                        int courseCredits = courseJson.getInt("credits");
                        int courseNumSections = courseJson.getInt("numSections");

                        // Extracting department-related properties
                        JSONObject departmentJson = courseJson.getJSONObject("department");
                        String departmentName = departmentJson.getString("name");
                        String departmentLocation = departmentJson.getString("location");
                        int departmentId = departmentJson.getInt("did");

                        Department department = new Department(departmentName, departmentLocation, departmentId);
                        Course course = new Course(courseCode, courseTitle, courseDescription, courseCredits, courseNumSections, department, courseJson.getInt("cid"));
                        ListTaskObject task = new ListTaskObject(tId, section, title, description, dueDate, taskType, course, department);
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

        Volley.newRequestQueue(context).add(jsonArrReq);
    }

    public void updateTask(ListTaskObject task) {
        String url = URL_STRING_REQ_VM;
        JSONObject body = new JSONObject();
        try {
            body.put("tId", task.gettId());
            body.put("section", task.getSection());
            body.put("title", task.getTitle());
            body.put("description", task.getDescription());
            body.put("dueDate", task.getDueDate().toString());
            body.put("taskType", task.getTaskType());

            JSONObject courseJson = new JSONObject();
            courseJson.put("code", task.getCourse().getCode());
            courseJson.put("title", task.getCourse().getTitle());
            courseJson.put("description", task.getCourse().getDescription());
            courseJson.put("credits", task.getCourse().getCredits());
            courseJson.put("numSections", task.getCourse().getNumSections());
            courseJson.put("cid", task.getCourse().getcId());

            JSONObject departmentJson = new JSONObject();
            departmentJson.put("name", task.getDepartment().getName());
            departmentJson.put("location", task.getDepartment().getLocation());
            departmentJson.put("did", task.getDepartment().getDid());

            courseJson.put("department", departmentJson);
            body.put("course", courseJson);
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

    public void createTask(ListTaskObject task) {
        JSONObject body = new JSONObject();
        try {
            body.put("tId", task.gettId());
            body.put("section", task.getSection());
            body.put("title", task.getTitle());
            body.put("description", task.getDescription());
            body.put("dueDate", task.getDueDate().toString());
            body.put("taskType", task.getTaskType());

            JSONObject courseJson = new JSONObject();
            courseJson.put("code", task.getCourse().getCode());
            courseJson.put("title", task.getCourse().getTitle());
            courseJson.put("description", task.getCourse().getDescription());
            courseJson.put("credits", task.getCourse().getCredits());
            courseJson.put("numSections", task.getCourse().getNumSections());
            courseJson.put("cid", task.getCourse().getcId());

            JSONObject departmentJson = new JSONObject();
            departmentJson.put("name", task.getDepartment().getName());
            departmentJson.put("location", task.getDepartment().getLocation());
            departmentJson.put("did", task.getDepartment().getDid());

            courseJson.put("department", departmentJson);
            body.put("course", courseJson);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_STRING_REQ_VM, body, new Response.Listener<JSONObject>() {
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

    public void deleteTask(ListTaskObject task) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, URL_STRING_REQ_VM + "/" + task.gettId(), null, new Response.Listener<JSONObject>() {
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
        });

        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }
}