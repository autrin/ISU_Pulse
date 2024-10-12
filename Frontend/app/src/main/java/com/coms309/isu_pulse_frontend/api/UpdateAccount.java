package com.coms309.isu_pulse_frontend.api;

import static com.coms309.isu_pulse_frontend.api.Constants.BASE_URL;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class UpdateAccount {
    public interface VolleyCallback {
        void onSuccess(String result);

        void onError(String message);
    }

    public void updateUserPassword(
            String netId,
            String newPassword,
            Context context,
            final VolleyCallback callback
    ) {
        String url = BASE_URL + "users/updatepw/" + netId + "?newPassword=" + newPassword;
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest putRequest = new StringRequest(
                Request.Method.PUT, url,
                callback::onSuccess,
                error -> {
                    callback.onError(error.toString());
                    error.printStackTrace();
                }
        );
        queue.add(putRequest);
    }




}
