package com.coms309.isu_pulse_frontend.chat_system;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.api.GroupChatApiService;
import com.coms309.isu_pulse_frontend.loginsignup.User;

import java.util.List;

public class GroupChatAddingMember extends AppCompatActivity {
    private TextView cancel;
    private TextView create;
    private TextView newGroup;
    private EditText searchBar;
    private EditText groupName;
    private RecyclerView groupChatAddingMemberRecyclerView;
    private List<User> userList;
    private GroupChatApiService groupChatApiService;

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

        groupChatApiService = new GroupChatApiService(this);

        groupChatAddingMemberRecyclerView = findViewById(R.id.friend_list);

        cancel.setOnClickListener(v -> {
            Intent intent = new Intent(GroupChatAddingMember.this, ChatList.class);
            startActivity(intent);
        });

        create.setOnClickListener(v -> {
            if (!groupName.getText().toString().trim().isEmpty()) {
                saveGroup();
            }

            Intent intent = new Intent(GroupChatAddingMember.this, ChatList.class);
            startActivity(intent);
        });


    }

    private void saveGroup(){
        String groupNameText = groupName.getText().toString().trim();
        Long groupId = getIntent().getLongExtra("groupId", -1);
        Toast.makeText(GroupChatAddingMember.this, "Group id: " + groupId, Toast.LENGTH_SHORT).show();
        Toast.makeText(GroupChatAddingMember.this, "Group name: " + groupNameText, Toast.LENGTH_SHORT).show();
        groupChatApiService.modifyGroupChat(groupId, groupNameText, new GroupChatApiService.GroupChatCallback() {
            @Override
            public void onSuccess(String response) {
                Toast.makeText(GroupChatAddingMember.this, "Group name modified successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(GroupChatAddingMember.this, ChatList.class);
                startActivity(intent);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(GroupChatAddingMember.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
