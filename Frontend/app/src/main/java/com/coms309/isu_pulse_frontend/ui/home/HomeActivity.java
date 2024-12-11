package com.coms309.isu_pulse_frontend.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.coms309.isu_pulse_frontend.chat_system.ChatList;
import com.coms309.isu_pulse_frontend.MainActivity;
import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.api.UpdateAccount;
import com.coms309.isu_pulse_frontend.api.WeatherApiService;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;
import com.coms309.isu_pulse_frontend.model.Profile;
import com.coms309.isu_pulse_frontend.profile_activity.ProfileActivity;
import com.coms309.isu_pulse_frontend.student_display.DisplayStudent;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class HomeActivity extends androidx.appcompat.app.AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private NavigationView navigationView;
    private TextView tempTextView;
    private WeatherApiService weatherApiService;
    private TextView firstNameHeader;
    private TextView lastNameHeader;
    private ImageView profileImageHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is logged in; if not, redirect to MainActivity for login
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        if (sharedPreferences.getString("netId", null) == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        // Set the layout which contains the Drawer and NavigationView
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.toolbar));
        findViewById(R.id.fab).setOnClickListener(view ->
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
        );

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_students, R.id.nav_chatting, R.id.nav_profile, R.id.nav_logout)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent intent = null;

            if (id == R.id.nav_students) {
                intent = new Intent(HomeActivity.this, DisplayStudent.class);
            } else if (id == R.id.nav_chatting) {
                intent = new Intent(HomeActivity.this, ChatList.class);
            } else if (id == R.id.nav_profile) {
                intent = new Intent(HomeActivity.this, ProfileActivity.class);
            } else if (id == R.id.nav_logout) {
                intent = new Intent(HomeActivity.this, MainActivity.class);
            }

            if (intent != null) {
                startActivity(intent);
                drawer.closeDrawers();
                return true;
            }
            return false;
        });

        // Initialize header views and fetch data
        setupNavHeader();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // When returning to HomeActivity, update the nav header again
        setupNavHeader();
    }

    private void setupNavHeader() {
        View headerView = navigationView.getHeaderView(0);
        tempTextView = headerView.findViewById(R.id.temperature);
        firstNameHeader = headerView.findViewById(R.id.firstNameTextViewHeader);
        lastNameHeader = headerView.findViewById(R.id.lastNameTextViewHeader);
        profileImageHeader = headerView.findViewById(R.id.imageView);

        // Fetch and display weather
        weatherApiService = new WeatherApiService(this);
        weatherApiService.fetchTemperature(new WeatherApiService.GetWeatherCallback() {
            @Override
            public void onSuccess(String temperature) {
                runOnUiThread(() -> tempTextView.setText(temperature));
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> tempTextView.setText("N/A"));
            }
        });

        // Fetch and display profile data
        String netId = UserSession.getInstance(this).getNetId();
        if (netId != null) {
            UpdateAccount.fetchProfileData(netId, this, new UpdateAccount.ProfileCallback() {
                @Override
                public void onSuccess(Profile profile) {
                    runOnUiThread(() -> {
                        String firstName = profile.getFirstName();
                        String lastName = profile.getLastName();

                        if (firstName != null && !firstName.isEmpty()) {
                            firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
                        }

                        if (lastName != null && !lastName.isEmpty()) {
                            lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();
                        }

                        firstNameHeader.setText(firstName);
                        lastNameHeader.setText(lastName);


                        Glide.with(HomeActivity.this)
                                .load(profile.getProfilePictureUrl())
                                .placeholder(R.mipmap.ic_launcher_round)
                                .into(profileImageHeader);
                    });
                }

                @Override
                public void onError(VolleyError error) {
                    runOnUiThread(() -> {
                        firstNameHeader.setText("");
                        lastNameHeader.setText("");
                    });
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}
