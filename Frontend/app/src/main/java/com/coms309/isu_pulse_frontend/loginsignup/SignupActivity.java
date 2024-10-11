package com.coms309.isu_pulse_frontend.loginsignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.coms309.isu_pulse_frontend.MainActivity;
import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.api.AuthenticationService;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SignupActivity extends AppCompatActivity {
    private EditText netId, firstname, lastname, email, password, retypepassword;
    private TextView enter, signin;
    private Spinner usertype;
    private ImageView profileImage;
    private Button uploadImageButton;
    private Uri imageUri;

    private StorageReference storageReference;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        FirebaseApp.initializeApp(this);
        storageReference = FirebaseStorage.getInstance().getReference();

        firstname = findViewById(R.id.firstname_isu_pulse);
        lastname = findViewById(R.id.lastname_isu_pulse);
        email = findViewById(R.id.email_isu_pulse);
        netId = findViewById(R.id.netid_isu_pulse);
        password = findViewById(R.id.password_isu_pulse);
        retypepassword = findViewById(R.id.confirm_password_isu_pulse);
        enter = findViewById(R.id.enter_isu_pulse);
        signin = findViewById(R.id.sign_in_isu_pulse);
        usertype = findViewById(R.id.usertype_isu_pulse);
        profileImage = findViewById(R.id.profile_image);
        uploadImageButton = findViewById(R.id.upload_image_button);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.user_roles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        usertype.setAdapter(adapter);

        uploadImageButton.setOnClickListener(v -> showImagePickerOptions());

        enter.setOnClickListener(view -> {
            AuthenticationService apiService = new AuthenticationService();
            String firstNameInput = firstname.getText().toString();
            String lastNameInput = lastname.getText().toString();
            String emailInput = email.getText().toString();
            String netIdInput = netId.getText().toString();
            String passwordInput = password.getText().toString();
            String hashedPassword = PasswordHasher.hashPassword(passwordInput);
            String retypePasswordInput = retypepassword.getText().toString();

            // Sanitize netIdInput to remove any invalid characters
            netIdInput = netIdInput.replaceAll("[^a-zA-Z0-9]", "");

            if (firstNameInput.isEmpty() || lastNameInput.isEmpty() || emailInput.isEmpty() || netIdInput.isEmpty() || passwordInput.isEmpty() || retypePasswordInput.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            } else if (passwordInput.length() < 8) {
                Toast.makeText(SignupActivity.this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
            } else if (!passwordInput.equals(retypePasswordInput)) {
                Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else if (imageUri == null) {
                Toast.makeText(SignupActivity.this, "Please upload a profile image", Toast.LENGTH_SHORT).show();
            } else {
                String finalNetIdInput = netIdInput;
                uploadImageToFirebase(netIdInput, (downloadUrl) -> {
                    if (downloadUrl != null) {
                        // Proceed with signup using the download URL
                        apiService.registerNewUser(
                                finalNetIdInput,
                                firstNameInput,
                                lastNameInput,
                                emailInput,
                                hashedPassword,
                                downloadUrl,
                                usertype.getSelectedItem().toString(),
                                SignupActivity.this,
                                new AuthenticationService.VolleyCallback() {
                                    @Override
                                    public void onSuccess(JSONObject result) {
                                        Toast.makeText(SignupActivity.this, "Signup successful!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onError(String message) {
                                        Toast.makeText(SignupActivity.this, "Signup failed: " + message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );
                        } else {
                                Toast.makeText(SignupActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        signin.setOnClickListener(view -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void showImagePickerOptions() {
        String[] options = {"Take Photo", "Choose from Gallery"};
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Choose your profile picture");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                dispatchTakePictureIntent();
            } else if (which == 1) {
                pickImageFromGallery();
            }
        });
        builder.show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Error occurred while creating the file", Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(this, "com.coms309.isu_pulse_frontend.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private File createImageFile() throws IOException {
        // Create an image file name with a timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                profileImage.setImageURI(imageUri);
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                imageUri = data.getData();
                profileImage.setImageURI(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(String netIdInput, FirebaseUploadCallback callback) {
        if (imageUri != null) {
            // Sanitize netIdInput to remove any invalid characters
            netIdInput = netIdInput.replaceAll("[^a-zA-Z0-9]", "");

            StorageReference ref = storageReference.child("images/" + netIdInput + "." + "jpg");

            ref.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Retrieve the download URL directly from the taskSnapshot
                        taskSnapshot.getStorage().getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    String downloadUrl = uri.toString();
                                    callback.onUploadSuccess(downloadUrl);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(SignupActivity.this, "Failed to get download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(SignupActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }


    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        if (uri != null && cR != null) {
            return mime.getExtensionFromMimeType(cR.getType(uri));
        }
        return null;
    }

    interface FirebaseUploadCallback {
        void onUploadSuccess(String downloadUrl);
    }
}