package com.coms309.isu_pulse_frontend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.coms309.isu_pulse_frontend.chat_system.ChatList;
import com.coms309.isu_pulse_frontend.databinding.ActivityMainBinding;
import com.coms309.isu_pulse_frontend.loginsignup.LoginActivity;
import com.coms309.isu_pulse_frontend.loginsignup.SignupActivity;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;
import com.coms309.isu_pulse_frontend.profile_activity.ProfileActivity;
import com.coms309.isu_pulse_frontend.student_display.DisplayStudent;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
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
                        R.id.nav_home, R.id.nav_profile, R.id.nav_courses)
                        .setOpenableLayout(drawer)
                        .build();
                setupTeacherMenu();
            } else {
                mAppBarConfiguration = new AppBarConfiguration.Builder(
                        R.id.nav_home, R.id.nav_profile)
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
                } else {
                    return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
                }
            });

            if (getIntent().getBooleanExtra("navigateToHome", false)) {
                navController.navigate(R.id.nav_home);
            }

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
}
