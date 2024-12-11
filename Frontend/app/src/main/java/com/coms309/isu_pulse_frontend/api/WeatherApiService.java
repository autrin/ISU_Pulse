package com.coms309.isu_pulse_frontend.api;

import static com.coms309.isu_pulse_frontend.api.Constants.BASE_URL;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherApiService {
    private final com.android.volley.RequestQueue requestQueue;
    private final Context context;

    public WeatherApiService(Context context) {
        this.context = context.getApplicationContext();
        requestQueue = Volley.newRequestQueue(this.context);
    }

    public interface GetWeatherCallback {
        void onSuccess(String temperature);

        void onError(String error);
    }

    /**
     * Fetches the weather data from the backend and returns the formatted temperature string
     * via the callback.
     *
     * @param callback Callback interface for success or error responses.
     */
    public void fetchTemperature(GetWeatherCallback callback) {
        String url = BASE_URL + "weather"; // Ensure BASE_URL ends with '/', if not, adjust accordingly

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        // Attempt to read temperature in Celsius
                        double tempCelsius = jsonObject.optDouble("temperature", Double.NaN);
                        Log.d("WeatherApiService", "Temperature in Celsius: " + tempCelsius);
                        if (Double.isNaN(tempCelsius)) {
                            callback.onError("Temperature not available");
                            return;
                        }

                        // Convert Celsius to Fahrenheit
                        double tempFahrenheit = (tempCelsius * 9.0 / 5.0) + 32.0;
                        int tempFInt = (int) Math.floor(tempFahrenheit);

                        // Return formatted temperature (e.g., "19°F")
                        callback.onSuccess(tempFInt + "°F");

                    } catch (JSONException e) {
                        Log.e("WeatherApiService", "JSON parse error", e);
                        callback.onError("Error parsing weather data.");
                    }
                },
                error -> {
                    // Handle Volley error
                    String errorMessage = parseVolleyError(error);
                    callback.onError(errorMessage);
                }
        );

        requestQueue.add(stringRequest);
    }

    /**
     * Helper method to parse VolleyErrors into human-readable strings.
     */
    private String parseVolleyError(VolleyError error) {
        if (error.getMessage() != null) {
            return error.getMessage();
        }
        return "Unknown error occurred while fetching weather.";
    }
}
