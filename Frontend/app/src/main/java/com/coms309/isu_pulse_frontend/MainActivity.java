package com.coms309.isu_pulse_frontend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.coms309.isu_pulse_frontend.api.UpdateAccount;
import com.coms309.isu_pulse_frontend.api.WeatherApiService;
import com.coms309.isu_pulse_frontend.chat_system.ChatList;
import com.coms309.isu_pulse_frontend.databinding.ActivityMainBinding;
import com.coms309.isu_pulse_frontend.loginsignup.LoginActivity;
import com.coms309.isu_pulse_frontend.loginsignup.SignupActivity;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;
import com.coms309.isu_pulse_frontend.model.Profile;
import com.coms309.isu_pulse_frontend.profile_activity.ProfileActivity;
import com.coms309.isu_pulse_frontend.student_display.DisplayStudent;
import com.coms309.isu_pulse_frontend.ui.ask_ai.AskAiActivity;
import com.coms309.isu_pulse_frontend.ui.calendar.ClassCalendar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private Button signInButton;
    private Button signUpButton;
    private NavigationView navigationView;
    private TextView tempTextView;
    private WeatherApiService weatherApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is already logged in
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String netId = sharedPreferences.getString("netId", null);

        if (netId != null) {
            // User is logged in; set up main layout with navigation drawer
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            setSupportActionBar(binding.appBarMain.toolbar);
            binding.appBarMain.fab.setOnClickListener(view ->
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
            );

            DrawerLayout drawer = binding.drawerLayout;
            navigationView = binding.navView;

            // Configure navigation based on user role
            String userRole = UserSession.getInstance(this).getUserType();
            if ("FACULTY".equals(userRole)) {
                mAppBarConfiguration = new AppBarConfiguration.Builder(
                        R.id.nav_home, R.id.nav_profile, R.id.nav_courses, R.id.nav_ask_ai)
                        .setOpenableLayout(drawer)
                        .build();
                setupTeacherMenu();
            } else {
                mAppBarConfiguration = new AppBarConfiguration.Builder(
                        R.id.nav_home, R.id.nav_students, R.id.nav_chatting, R.id.nav_profile, R.id.nav_ask_ai, R.id.nav_calendar, R.id.nav_logout)
                        .setOpenableLayout(drawer)
                        .build();
                setupStudentMenu();
            }

            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);

            // Handle specific menu selections
            navigationView.setNavigationItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_students) {
                    startActivity(new Intent(MainActivity.this, DisplayStudent.class));
                    drawer.closeDrawers();
                    return true;
                } else if (id == R.id.nav_chatting) {
                    startActivity(new Intent(MainActivity.this, ChatList.class));
                    drawer.closeDrawers();
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    drawer.closeDrawers();
                    return true;
                } else if (id == R.id.nav_logout) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    drawer.closeDrawers();
                    return true;
                } else if (id == R.id.nav_ask_ai) {
                    startActivity(new Intent(MainActivity.this, AskAiActivity.class));
                    drawer.closeDrawers();
                    return true;
                } else if (id == R.id.nav_calendar) {
                    startActivity(new Intent(MainActivity.this, ClassCalendar.class));
                    drawer.closeDrawers();
                    return true;
                } else {
                    return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
                }
            });

            if (getIntent().getBooleanExtra("navigateToHome", false)) {
                navController.navigate(R.id.nav_home);
            }

            View headerView = navigationView.getHeaderView(0);
            tempTextView = headerView.findViewById(R.id.temperature);
            // Fetch weather data
            weatherApiService = new WeatherApiService(this);
            weatherApiService.fetchTemperature(new WeatherApiService.GetWeatherCallback() {
                @Override
                public void onSuccess(String temperature) {
                    // Update UI on main thread
                    runOnUiThread(() -> tempTextView.setText(temperature));
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> tempTextView.setText("N/A"));
                }
            });

            TextView firstNameHeader = headerView.findViewById(R.id.firstNameTextViewHeader);
            TextView lastNameHeader = headerView.findViewById(R.id.lastNameTextViewHeader);
            ImageView profileImageHeader = headerView.findViewById(R.id.imageView);

            UpdateAccount.fetchProfileData(netId, this, new UpdateAccount.ProfileCallback() {
                @Override
                public void onSuccess(Profile profile) {
                    // Update UI on main thread
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


                        // Load profile image using Glide
                        Glide.with(MainActivity.this)
                                .load(profile.getProfilePictureUrl())
                                .placeholder(R.mipmap.ic_launcher_round)
                                .into(profileImageHeader);
                    });
                }

                @Override
                public void onError(VolleyError error) {
                    // Handle error, maybe set defaults
                    runOnUiThread(() -> {
                        firstNameHeader.setText("");
                        lastNameHeader.setText("");
                    });
                }
            });

        } else {
            // No saved session; show login/sign-up screen
            setContentView(R.layout.intro);
            signInButton = findViewById(R.id.signInButton);
            signUpButton = findViewById(R.id.signUpButton);

            signInButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));
            signUpButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, SignupActivity.class)));
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

    private void setupTeacherMenu() {
        Menu menu = navigationView.getMenu();
        menu.clear();
        getMenuInflater().inflate(R.menu.teacher_main_drawer, menu);
    }

    private void setupStudentMenu() {
        Menu menu = navigationView.getMenu();
        menu.clear();
        getMenuInflater().inflate(R.menu.student_main_drawer, menu);
    }

    public NavigationView getNavigationView() {
        return navigationView;
    }
}
