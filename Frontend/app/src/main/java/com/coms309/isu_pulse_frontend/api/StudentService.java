package com.coms309.isu_pulse_frontend.api;

import static com.coms309.isu_pulse_frontend.api.Constants.BASE_URL;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;

public class StudentService {

    private final RequestQueue requestQueue;

    public StudentService(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public void getAllStudents(final VolleyCallback callback) {
        String url = BASE_URL + "users/allStudents";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

    public interface VolleyCallback {
        void onSuccess(JSONArray result);
        void onError(VolleyError error);
    }
}
