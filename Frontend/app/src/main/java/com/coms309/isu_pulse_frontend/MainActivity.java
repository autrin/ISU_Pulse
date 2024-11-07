package com.coms309.isu_pulse_frontend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.coms309.isu_pulse_frontend.databinding.ActivityMainBinding;
import com.coms309.isu_pulse_frontend.loginsignup.LoginActivity;
import com.coms309.isu_pulse_frontend.loginsignup.SignupActivity;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;
import com.coms309.isu_pulse_frontend.proifle_activity.ProfileActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is already logged in
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String netId = sharedPreferences.getString("netId", null);

        if (netId != null) {
            // User is already logged in; proceed to profile activity
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
            return;
        } else {
            // No saved session; show login screen
            setContentView(R.layout.intro);

            // Initialize login and sign-up buttons only after setting the intro layout
            signInButton = findViewById(R.id.signInButton);
            signUpButton = findViewById(R.id.signUpButton);

            signInButton.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            });

            signUpButton.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
            });
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(view ->
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show()
        );

        DrawerLayout drawer = binding.drawerLayout;
        navigationView = binding.navView;

        // Passing each menu ID as a set of IDs because each menu should be considered as top-level destinations
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_Profile, R.id.nav_courses)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Check user role and set up navigation view accordingly
        String userRole = UserSession.getInstance(this).getUserRole();
        if ("TEACHER".equals(userRole)) {
            setupTeacherMenu();
        } else {
            setupStudentMenu();
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

    // Define teacher-specific menu setup
    private void setupTeacherMenu() {
        Menu menu = navigationView.getMenu();
        menu.clear();
        getMenuInflater().inflate(R.menu.teacher_main_drawer, menu); // Custom menu for teachers
    }

    // Define student-specific menu setup
    private void setupStudentMenu() {
        Menu menu = navigationView.getMenu();
        menu.clear();
        getMenuInflater().inflate(R.menu.student_main_drawer, menu); // Custom menu for students
    }

    // Getter for navigationView (for testing purposes)
    public NavigationView getNavigationView() {
        return navigationView;
    }
}
