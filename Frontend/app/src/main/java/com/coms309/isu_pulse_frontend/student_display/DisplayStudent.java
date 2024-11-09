package com.coms309.isu_pulse_frontend.student_display;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.VolleyError;
import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.api.StudentService;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;
import com.coms309.isu_pulse_frontend.ui.home.HomeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class DisplayStudent extends AppCompatActivity {

    private ImageView backButton;
    private EditText searchBar;
    private Button searchButton;
    private Spinner sortSpinner;
    private RecyclerView recyclerView;
    private StudentAdapter studentAdapter;
    private List<Student> studentList;
    private List<Student> filteredList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_list);

        backButton = findViewById(R.id.back_button_);
        searchButton = findViewById(R.id.search_button);
        searchBar = findViewById(R.id.search_bar);
        sortSpinner = findViewById(R.id.sort_spinner);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplayStudent.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.students_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        studentList = new ArrayList<>();
        filteredList = new ArrayList<>();

        studentAdapter = new StudentAdapter(this, filteredList);
        recyclerView.setAdapter(studentAdapter);

        fetchStudents();

        // Set up the sort spinner with options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_alphabetically_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);

        // Set listener for sort option selection
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortStudents(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchBar.getText().toString().trim();
                if (!TextUtils.isEmpty(query)) {
                    filterStudents(query);
                } else {
                    resetStudentList();
                }
            }
        });
    }

    private void resetStudentList() {
        filteredList.clear();
        filteredList.addAll(studentList); // Show all students
        studentAdapter.notifyDataSetChanged();
    }

    private void filterStudents(String query) {
        filteredList.clear();
        for (Student student : studentList) {
            String fullName = student.getFirstName() + " " + student.getLastName();
            if (fullName.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(student);
            }
        }
        studentAdapter.notifyDataSetChanged();
    }

    private void fetchStudents() {
        String studentNetId = UserSession.getInstance().getNetId();
        StudentService studentService = new StudentService(this);
        studentService.getAllStudents(new StudentService.VolleyCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject studentJson = response.getJSONObject(i);
                        String firstName = studentJson.getString("firstName");
                        String lastName = studentJson.getString("lastName");
                        String netId = studentJson.getString("netId");

                        Student student = new Student(firstName, lastName, netId);
                        if (!student.getNetId().equals(studentNetId)) {
                            studentList.add(student);
                        }
                    }
                    resetStudentList();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(DisplayStudent.this, "Error parsing student data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(DisplayStudent.this, "Failed to fetch students", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sortStudents(int sortOption) {
        if (sortOption == 0) {
            // Sort A-Z
            filteredList.sort((s1, s2) -> s1.getFirstName().compareToIgnoreCase(s2.getFirstName()));
        } else if (sortOption == 1) {
            // Sort Z-A
            filteredList.sort((s1, s2) -> s2.getFirstName().compareToIgnoreCase(s1.getFirstName()));
        }
        studentAdapter.notifyDataSetChanged();
    }
}
