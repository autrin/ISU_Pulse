package com.coms309.isu_pulse_frontend.ui.ask_ai;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.coms309.isu_pulse_frontend.R;

public class AskAiActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    Button searchButton;
    EditText searchBar;
    ImageButton addButton;
    TextView title;
    ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_screen);

        recyclerView = findViewById(R.id.recyclerView);
        searchButton = findViewById(R.id.search_button);
        searchBar = findViewById(R.id.search_bar);
        addButton = findViewById(R.id.add_button);
        title = findViewById(R.id.title);
        backButton = findViewById(R.id.back_button);
    }
}
