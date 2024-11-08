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
import com.coms309.isu_pulse_frontend.ui.profile.ProfileActivity;
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

            // Set up AppBarConfiguration based on user role
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

            // Override navigation for specific items
            navigationView.setNavigationItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_profile) {
                    // Launch ProfileActivity directly with an Intent
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    return true;
                } else {
                    // For other items, allow NavController to handle them
                    return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
                }
            });

            // Set the default fragment only if navigating from login with the "navigateToHome" flag
            if (getIntent().getBooleanExtra("navigateToHome", false)) {
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
