package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

public class ImageReqActivity extends AppCompatActivity {

    private Button btnImageReq;
    private ImageView imageView;

    private static final String URL_IMAGE = "http://10.0.2.2:8080/images/1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_req);

        btnImageReq = findViewById(R.id.btnImageReq);
        imageView = findViewById(R.id.imgView);

        btnImageReq.setOnClickListener(v -> {
            Toast.makeText(this, "Requesting image...", Toast.LENGTH_SHORT).show();
            makeImageRequest();
        });
    }

    private void makeImageRequest() {
        ImageRequest imageRequest = new ImageRequest(
                URL_IMAGE,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imageView.setImageBitmap(response);
                        Toast.makeText(getApplicationContext(), "Image loaded successfully", Toast.LENGTH_SHORT).show();
                    }
                },
                0,
                0,
                ImageView.ScaleType.FIT_XY,
                Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Image Request", "Error: " + error.toString());
                        Toast.makeText(getApplicationContext(), "Failed to load image", Toast.LENGTH_LONG).show();
                    }
                }
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(imageRequest);
    }
}
