package com.coms309.isu_pulse_frontend.proifle_activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.coms309.isu_pulse_frontend.MainActivity;
import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.course_functional.CourseView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private MaterialButton editProfile;
    private MaterialButton logout;
    private ShapeableImageView profileImage;
    private TextView coursesTextView;
    private TextView friendsTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        TextView coursesTextView = findViewById(R.id.coursesTextView);
        TextView friendsTextView = findViewById(R.id.friendsTextView);
//        setSupportActionBar(toolbar);
//        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        MaterialButton editProfile = findViewById(R.id.updateProfileButton);

        MaterialButton logout = findViewById(R.id.logoutButton);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle back button press
//                onBackPressed();
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        coursesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, CourseView.class);
                startActivity(intent);
            }
        });



        String imageUrl = "https://firebasestorage.googleapis.com/v0/b/coms-309-image-storage.appspot.com/o/images%2Fbaonguyen.jpg?alt=media&token=9a992e70-2847-4007-b03c-90b622029b0d";
        profileImage = findViewById(R.id.profileImage);
        Glide.with(this)
                .load(imageUrl)
                .into(profileImage);





    }
}
