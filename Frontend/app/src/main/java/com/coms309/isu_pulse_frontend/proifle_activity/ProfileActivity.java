//package com.coms309.isu_pulse_frontend.proifle_activity;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.cardview.widget.CardView;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import com.bumptech.glide.Glide;
//import com.coms309.isu_pulse_frontend.R;
//import com.google.android.material.button.MaterialButton;
//import com.google.android.material.imageview.ShapeableImageView;
//import com.google.android.material.textfield.TextInputEditText;
//
//public class ProfileActivity extends AppCompatActivity {
//
//    private ShapeableImageView profileImage;
//    private TextView nameTextView;
//    private TextView usernameTextView;
//    private TextView postsTextView;
//    private TextView followersTextView;
//    private TextView followingTextView;
//    private MaterialButton updateProfileButton;
//    private MaterialButton deleteAccountButton;
//    private CardView deleteAccountConfirmation;
//    private TextInputEditText netIdInput;
//    private TextInputEditText passwordInput;
//    private MaterialButton cancelDeleteButton;
//    private MaterialButton confirmDeleteButton;
//
//    private static final int PERMISSION_REQUEST_CODE = 100;
//
//    // Replace with your actual image URI
//    private static final String IMAGE_URI_STRING = "content://com.google.android.apps.photos.contentprovider/0/1/mediakey%3A%2Flocal%253A9acb86de-1890-428b-9170-52777a9a37a0/ORIGINAL/NONE/image%2Fpng/2133543455";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.profile);
//
//        initViews();
//        setupListeners();
//        checkStoragePermission();
//    }
//
//    private void initViews() {
//        profileImage = findViewById(R.id.profileImage);
//        nameTextView = findViewById(R.id.nameTextView);
//        usernameTextView = findViewById(R.id.usernameTextView);
//        postsTextView = findViewById(R.id.postsTextView);
//        followersTextView = findViewById(R.id.followersTextView);
//        followingTextView = findViewById(R.id.followingTextView);
//        updateProfileButton = findViewById(R.id.updateProfileButton);
//        deleteAccountButton = findViewById(R.id.deleteAccountButton);
//        deleteAccountConfirmation = findViewById(R.id.deleteAccountConfirmation);
//        netIdInput = findViewById(R.id.netIdInput);
//        passwordInput = findViewById(R.id.passwordInput);
//        cancelDeleteButton = findViewById(R.id.cancelDeleteButton);
//        confirmDeleteButton = findViewById(R.id.confirmDeleteButton);
//    }
//
//    private void setupListeners() {
//        updateProfileButton.setOnClickListener(v -> onUpdateProfileClicked());
//        deleteAccountButton.setOnClickListener(v -> onDeleteAccountClicked());
//        cancelDeleteButton.setOnClickListener(v -> onCancelDeleteClicked());
//        confirmDeleteButton.setOnClickListener(v -> onConfirmDeleteClicked());
//    }
//
//    private void loadProfileData() {
//        // Load profile data from your data source (e.g., API, database)
//        nameTextView.setText("Nguyễn Tất Bách");
//        usernameTextView.setText("@_ntbachhh");
//        postsTextView.setText("0 posts");
//        followersTextView.setText("115 followers");
//        followingTextView.setText("354 following");
//
//        // Load profile image using Glide
//        Uri imageUri = Uri.parse(IMAGE_URI_STRING);
//
//        Glide.with(this)
//                .load(imageUri)
//                .placeholder(R.drawable.isu_logo)
//                .error(R.drawable.isu_logo)
//                .circleCrop()
//                .into(profileImage);
//    }
//
//    private void onUpdateProfileClicked() {
//        // Implement update profile functionality
//        Toast.makeText(this, "Update Profile clicked", Toast.LENGTH_SHORT).show();
//    }
//
//    private void onDeleteAccountClicked() {
//        deleteAccountConfirmation.setVisibility(View.VISIBLE);
//    }
//
//    private void onCancelDeleteClicked() {
//        deleteAccountConfirmation.setVisibility(View.GONE);
//        clearDeleteAccountInputs();
//    }
//
//    private void onConfirmDeleteClicked() {
//        String netId = netIdInput.getText().toString().trim();
//        String password = passwordInput.getText().toString().trim();
//
//        if (netId.isEmpty() || password.isEmpty()) {
//            Toast.makeText(this, "Please enter both NetID and Password", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Implement account deletion logic
//        Toast.makeText(this, "Account deletion initiated", Toast.LENGTH_SHORT).show();
//
//        deleteAccountConfirmation.setVisibility(View.GONE);
//        clearDeleteAccountInputs();
//    }
//
//    private void clearDeleteAccountInputs() {
//        netIdInput.setText("");
//        passwordInput.setText("");
//    }
//
//    // Permission handling
//    private void checkStoragePermission() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                    PERMISSION_REQUEST_CODE);
//        } else {
//            // Permission already granted, load profile data
//            loadProfileData();
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted, load profile data
//                loadProfileData();
//            } else {
//                // Permission denied, handle accordingly
//                Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//}
