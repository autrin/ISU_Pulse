package com.coms309.isu_pulse_frontend.chat_system;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.loginsignup.User;

import java.util.List;

public class GroupChatAddingMember extends AppCompatActivity {
    private TextView cancel;
    private TextView create;
    private TextView newGroup;
    private EditText searchBar;
    private EditText groupName;
    private RecyclerView groupChatAddingMemberRecyclerView;
    private List<User> userList;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_adding_screen);

        cancel = findViewById(R.id.btn_cancel);
        create = findViewById(R.id.btn_create);
        newGroup = findViewById(R.id.new_group);
        searchBar = findViewById(R.id.search_bar);

        groupName = findViewById(R.id.group_name);
//        groupName.setVisibility(View.GONE);
        cancel.setClickable(true);
        create.setText("Save");
        create.setClickable(true);
        create.setTextColor(Color.BLUE);
        newGroup.setText("New Members");

        groupChatAddingMemberRecyclerView = findViewById(R.id.friend_list);

        cancel.setOnClickListener(v -> {
            Intent intent = new Intent(GroupChatAddingMember.this, ChatList.class);
            startActivity(intent);
        });

        create.setOnClickListener(v -> {

        });


    }
}
