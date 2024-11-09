package com.coms309.isu_pulse_frontend.student_display;



import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.api.CourseService;
import com.coms309.isu_pulse_frontend.api.FriendService;
import com.coms309.isu_pulse_frontend.friend_functional.FriendProfile;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;
import com.coms309.isu_pulse_frontend.ui.home.Course;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
    private Context context;
    private List<Student> studentList;

    public StudentAdapter(Context context, List<Student> studentList) {
        this.context = context;
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_item, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);
        String fullName = student.getFirstName() + " " + student.getLastName();
        holder.nameTextView.setText(fullName);
        FriendService friendService = new FriendService(context);
        CourseService courseService = new CourseService(context);

        courseService.getMutualCourses(UserSession.getInstance().getNetId(), student.getNetId(),
                new CourseService.GetMutualCoursesCallback() {
                    @Override
                    public void onSuccess(List<Course> courses) {
                        int mutualCoursesCount = courses.size();
                        holder.mutualCoursesTextView.setText(mutualCoursesCount + " mutual courses");
                    }

                    @Override
                    public void onError(String error) {
                        holder.mutualCoursesTextView.setText("0 mutual courses");
                    }
                });
        
        friendService.getFriendsInCommon(UserSession.getInstance().getNetId(), student.getNetId(),
                response -> {
                    int mutualFriendsCount = response.length();
                    holder.mutualFriendsTextView.setText(mutualFriendsCount + " mutual friends");
                },
                error -> holder.mutualFriendsTextView.setText("0 mutual friends"));
        holder.viewProfileButton.setOnClickListener(v -> {
            // Handle button click here
            Intent intent = new Intent(context, FriendProfile.class);
            // Pass the friend's netId as an extra to FriendProfile
            intent.putExtra("netId", student.getNetId());
            // Start FriendProfile activity
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView viewProfileButton;
        TextView mutualFriendsTextView;
        TextView mutualCoursesTextView;

        StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.friend_name); // Assuming friend_name is the ID in student_item.xml
            viewProfileButton = itemView.findViewById(R.id.viewfriendbutton);
            mutualFriendsTextView = itemView.findViewById(R.id.mutual_friends);
            mutualCoursesTextView = itemView.findViewById(R.id.mutual_courses);
        }
    }


}

