package com.coms309.isu_pulse_frontend.api;

import static com.coms309.isu_pulse_frontend.api.Constants.BASE_URL;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class CourseService {
    private RequestQueue requestQueue;

//    public CourseService(Context context) {
//        requestQueue = Volley.newRequestQueue(context);
//    }

    public interface EnrollCallback {
        void onSuccess(boolean enrolled);
        void onError(String error);
    }

    public void enrollInCourse(String netId, int courseId, final EnrollCallback callback) {
        String url = BASE_URL + "enroll/addEnroll";

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
                params.put("netId", netId);
                params.put("course", String.valueOf(courseId));
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }
}