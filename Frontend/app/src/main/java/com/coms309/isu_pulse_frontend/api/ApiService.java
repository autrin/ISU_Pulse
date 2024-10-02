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

    private static final String URL_STRING_REQ = "http://10.0.2.2:8080/tasksduetoday";
    private Context context;

    public ApiService(Context context) {
        this.context = context;
    }

    public interface TaskResponseListener {
        void onResponse(List<ListTaskObject> tasks);
        void onError(String message);
    }


}