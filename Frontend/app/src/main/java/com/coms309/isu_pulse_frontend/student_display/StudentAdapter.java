package com.coms309.isu_pulse_frontend.student_display;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.api.CourseService;
import com.coms309.isu_pulse_frontend.api.FriendService;
import com.coms309.isu_pulse_frontend.api.UpdateAccount;
import com.coms309.isu_pulse_frontend.friend_functional.FriendProfile;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;
import com.coms309.isu_pulse_frontend.model.Profile;
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

        UpdateAccount.fetchProfileData(student.getNetId(), holder.itemView.getContext(), new UpdateAccount.ProfileCallback() {
            @Override
            public void onSuccess(Profile profile) {
                String imageUrl = profile.getProfilePictureUrl();  // Assume the Profile class has a method to get profile picture URL
                Glide.with(holder.itemView.getContext())
                        .load(imageUrl)
                        .into(holder.profileImageView);  // Set the profile image to the ImageView
            }

            @Override
            public void onError(VolleyError error) {
                // Handle the error, e.g., show a default image or log the error
                holder.profileImageView.setImageResource(R.drawable.isu_logo);
            }
        });

        // Fetch mutual courses
        courseService.getMutualCourses(UserSession.getInstance().getNetId(), student.getNetId(),
                new CourseService.GetMutualCoursesCallback() {
                    @Override
                    public void onSuccess(List<Course> courses) {
                        int mutualCoursesCount = courses.size();
                        holder.mutualCoursesTextView.setText(mutualCoursesCount + " mutual courses");

                        // Add popup for mutual courses
                        holder.mutualCoursesTextView.setOnClickListener(v -> {
                            View popupView = LayoutInflater.from(context).inflate(R.layout.popup_layout, null);

                            // Create the PopupWindow
                            PopupWindow popupWindow = new PopupWindow(popupView,
                                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

                            // Set data in popup
                            TextView popupTitle = popupView.findViewById(R.id.popupTitle);
                            TextView popupContent = popupView.findViewById(R.id.popupContent);

                            popupTitle.setText("Mutual Courses");
                            StringBuilder coursesBuilder = new StringBuilder();
                            for (Course course : courses) {
                                coursesBuilder.append(course.getCode()).append("\n");
                            }
                            popupContent.setText(coursesBuilder.toString());

                            // Show the popup window
                            popupWindow.showAsDropDown(holder.mutualCoursesTextView, 0, 0);
                        });
                    }

                    @Override
                    public void onError(String error) {
                        holder.mutualCoursesTextView.setText("0 mutual courses");
                    }
                });

        // Fetch mutual friends
        friendService.getFriendsInCommon(UserSession.getInstance().getNetId(), student.getNetId(),
                response -> {
                    int mutualFriendsCount = response.length();
                    holder.mutualFriendsTextView.setText(mutualFriendsCount + " mutual friends");

                    // Add popup for mutual friends
                    holder.mutualFriendsTextView.setOnClickListener(v -> {
                        View popupView = LayoutInflater.from(context).inflate(R.layout.popup_layout, null);

                        // Create the PopupWindow
                        PopupWindow popupWindow = new PopupWindow(popupView,
                                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

                        // Set data in popup
                        TextView popupTitle = popupView.findViewById(R.id.popupTitle);
                        TextView popupContent = popupView.findViewById(R.id.popupContent);

                        popupTitle.setText("Mutual Friends");
                        StringBuilder friendsBuilder = new StringBuilder();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                friendsBuilder.append(response.getJSONObject(i).getString("firstName"))
                                        .append(" ")
                                        .append(response.getJSONObject(i).getString("lastName"))
                                        .append("\n");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        popupContent.setText(friendsBuilder.toString());

                        // Show the popup window
                        popupWindow.showAsDropDown(holder.mutualFriendsTextView, 0, 0);
                    });
                },
                error -> holder.mutualFriendsTextView.setText("0 mutual friends"));

        // View profile button functionality
        holder.viewProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, FriendProfile.class);
            intent.putExtra("netId", student.getNetId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        ImageView profileImageView;
        TextView viewProfileButton;
        TextView mutualFriendsTextView;
        TextView mutualCoursesTextView;

        StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.friend_name); // Assuming friend_name is the ID in friends_item.xml
            viewProfileButton = itemView.findViewById(R.id.viewfriendbutton);
            mutualFriendsTextView = itemView.findViewById(R.id.mutual_friends);
            mutualCoursesTextView = itemView.findViewById(R.id.mutual_courses);
            profileImageView = itemView.findViewById(R.id.friend_avatar);
        }
    }
}
