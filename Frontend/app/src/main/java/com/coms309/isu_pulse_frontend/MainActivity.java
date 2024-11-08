package com.coms309.isu_pulse_frontend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
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
            // User is already logged in; proceed to main activity layout
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            // Set up Toolbar
            setSupportActionBar(binding.appBarMain.toolbar);

            // Set up floating action button (if needed)
            binding.appBarMain.fab.setOnClickListener(view ->
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null)
                            .setAnchorView(R.id.fab).show()
            );

            // Drawer and navigation setup
            DrawerLayout drawer = binding.drawerLayout;
            navigationView = binding.navView;

            // Passing each menu ID as a set of IDs because each menu should be considered as top-level destinations
//            mAppBarConfiguration = new AppBarConfiguration.Builder(
//                    R.id.nav_home, R.id.nav_Profile, R.id.nav_courses)
//                    .setOpenableLayout(drawer)
//                    .build();
            // Check user role and set up navigation view accordingly
            String userRole = UserSession.getInstance(this).getUserType();
            if ("TEACHER".equals(userRole)) {
                // Teacher: Include Courses page in the menu
                mAppBarConfiguration = new AppBarConfiguration.Builder(
                        R.id.nav_home, R.id.nav_Profile, R.id.nav_courses)  // Include Courses for Teacher
                        .setOpenableLayout(binding.drawerLayout)
                        .build();
                setupTeacherMenu();
            } else {
                // Student: Exclude Courses page from the menu
                mAppBarConfiguration = new AppBarConfiguration.Builder(
                        R.id.nav_home, R.id.nav_Profile)  // Exclude Courses for Student
                        .setOpenableLayout(binding.drawerLayout)
                        .build();
                setupStudentMenu();
            }

            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);

            // Check user role and set up navigation view accordingly
//            String userRole = UserSession.getInstance(this).getUserType();
            if ("TEACHER".equals(userRole)) {
                setupTeacherMenu();
            } else {
                setupStudentMenu();
            }

            // Set the default fragment only if navigating from login with the "navigateToHome" flag
            if (getIntent().getBooleanExtra("navigateToHome", false)) {
                navController.navigate(R.id.nav_home);
            } else {
                // Default to home fragment when there’s no intent from login
                navController.navigate(R.id.nav_home);
            }


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
        menu.clear();  // Clear existing menu items
        getMenuInflater().inflate(R.menu.teacher_main_drawer, menu); // Custom menu for teachers
    }

    // Define student-specific menu setup
    private void setupStudentMenu() {
        Menu menu = navigationView.getMenu();
        menu.clear();  // Clear existing menu items
        getMenuInflater().inflate(R.menu.student_main_drawer, menu); // Custom menu for students
    }

    // Getter for navigationView (for testing purposes)
    public NavigationView getNavigationView() {
        return navigationView;
    }
}
