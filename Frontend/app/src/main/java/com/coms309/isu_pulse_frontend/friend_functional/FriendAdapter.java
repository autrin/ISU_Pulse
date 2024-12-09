package com.coms309.isu_pulse_frontend.friend_functional;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.api.CourseService;
import com.coms309.isu_pulse_frontend.api.FriendService;
import com.coms309.isu_pulse_frontend.api.UpdateAccount;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;
import com.coms309.isu_pulse_frontend.model.Profile;
import com.coms309.isu_pulse_frontend.ui.home.Course;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {
    private Context context;
    private List<Friend> friendList;

    public FriendAdapter(Context context, List<Friend> friendList) {
        this.friendList = friendList;
        this.context = context;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.unfriend_layout, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Friend friend = friendList.get(position);
        holder.friendName.setText(friend.getFirstName() + " " + friend.getLastName());

        String student1NetId = UserSession.getInstance().getNetId();
        String student2NetId = friend.getNetId();
        FriendService friendService = new FriendService(context);
        CourseService courseService = new CourseService(context);
        FriendViewHolder friendViewHolder = (FriendViewHolder) holder;

        UpdateAccount.fetchProfileData(friend.getNetId(), holder.itemView.getContext(), new UpdateAccount.ProfileCallback() {
            @Override
            public void onSuccess(Profile profile) {
                String imageUrl = profile.getProfilePictureUrl();
                Glide.with(holder.itemView.getContext())
                        .load(imageUrl)
                        .into(friendViewHolder.profileImage);
            }

            @Override
            public void onError(VolleyError error) {
                friendViewHolder.profileImage.setImageResource(R.drawable.isu_logo);
            }
        });

        // Fetch mutual courses
        courseService.getMutualCourses(student1NetId, student2NetId,
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
        friendService.getFriendsInCommon(student1NetId, student2NetId,
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

        // Unfriend button functionality
        holder.unfriendButton.setOnClickListener(v -> {
            friendService.unfriendFriend(student1NetId, student2NetId, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(context, "Unfriend successfully", Toast.LENGTH_SHORT).show();
                    friendList.remove(holder.getAdapterPosition());
                    notifyItemRemoved(holder.getAdapterPosition());
                    notifyItemRangeChanged(holder.getAdapterPosition(), friendList.size());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "Unfriend unsuccessfully", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // View profile button functionality
        holder.viewProfileButton.setOnClickListener(v -> {
            // Handle view profile button click
            Intent intent = new Intent(context, FriendProfile.class);
            intent.putExtra("netId", friend.getNetId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView friendName;
        TextView mutualFriendsTextView;
        TextView mutualCoursesTextView;
        Button viewProfileButton;
        Button unfriendButton;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.friend_avatar);
            friendName = itemView.findViewById(R.id.friend_name);
            mutualFriendsTextView = itemView.findViewById(R.id.mutual_friends);
            mutualCoursesTextView = itemView.findViewById(R.id.mutual_courses);
            viewProfileButton = itemView.findViewById(R.id.viewfriendbutton);
            unfriendButton = itemView.findViewById(R.id.unfriendbutton);
        }
    }
}
