package com.coms309.isu_pulse_frontend.friend_functional;

import android.content.Context;
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

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.FriendRequestViewHolder> {

    private List<Friend> friendRequestList;
    private FriendService friendService;
    private Context context;

    public FriendRequestAdapter(Context context, List<Friend> friendRequestList) {
        this.friendRequestList = friendRequestList;
        this.context = context;
        this.friendService = new FriendService(context);
    }

    @NonNull
    @Override
    public FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_status, parent, false);
        return new FriendRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestViewHolder holder, int position) {
        Friend friend = friendRequestList.get(position);
        String receiverNetId = UserSession.getInstance().getNetId();
        String senderNetId = friend.getNetId();
        FriendRequestViewHolder friendRequestViewHolder = (FriendRequestViewHolder) holder;

        holder.friendName.setText(friend.getFirstName() + " " + friend.getLastName());

        CourseService courseService = new CourseService(context);

        UpdateAccount.fetchProfileData(friend.getNetId(), holder.itemView.getContext(), new UpdateAccount.ProfileCallback() {
            @Override
            public void onSuccess(Profile profile) {
                String imageUrl = profile.getProfilePictureUrl();
                Glide.with(holder.itemView.getContext())
                        .load(imageUrl)
                        .into(friendRequestViewHolder.profileImage);
            }

            @Override
            public void onError(VolleyError error) {
                friendRequestViewHolder.profileImage.setImageResource(R.drawable.isu_logo);
            }
        });

        // Fetch and display mutual courses
        courseService.getMutualCourses(receiverNetId, senderNetId,
                new CourseService.GetMutualCoursesCallback() {
                    @Override
                    public void onSuccess(List<Course> courses) {
                        int mutualCoursesCount = courses.size();
                        holder.mutualCoursesTextView.setText(mutualCoursesCount + " mutual courses");

                        // Add popup functionality for mutual courses
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

        // Fetch and display mutual friends
        friendService.getFriendsInCommon(receiverNetId, senderNetId,
                response -> {
                    int mutualFriendsCount = response.length();
                    holder.mutualFriendsTextView.setText(mutualFriendsCount + " mutual friends");

                    // Add popup functionality for mutual friends
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

        // Accept friend request
        holder.acceptButton.setOnClickListener(v -> {
            friendService.acceptFriendRequest(receiverNetId, senderNetId, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(context, "Friend request accepted!", Toast.LENGTH_SHORT).show();
                    friendRequestList.remove(holder.getAdapterPosition());
                    notifyItemRemoved(holder.getAdapterPosition());
                    notifyItemRangeChanged(holder.getAdapterPosition(), friendRequestList.size());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "Failed to accept request", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Decline friend request
        holder.declineButton.setOnClickListener(v -> {
            friendService.rejectFriendRequest(receiverNetId, senderNetId, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(context, "Friend request declined", Toast.LENGTH_SHORT).show();
                    friendRequestList.remove(holder.getAdapterPosition());
                    notifyItemRemoved(holder.getAdapterPosition());
                    notifyItemRangeChanged(holder.getAdapterPosition(), friendRequestList.size());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "Failed to decline request", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return friendRequestList.size();
    }

    public static class FriendRequestViewHolder extends RecyclerView.ViewHolder {

        TextView friendName;
        Button acceptButton;
        Button declineButton;
        TextView mutualFriendsTextView;
        TextView mutualCoursesTextView;
        ImageView profileImage;

        public FriendRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            friendName = itemView.findViewById(R.id.friend_name);
            profileImage = itemView.findViewById(R.id.friend_avatar);
            acceptButton = itemView.findViewById(R.id.addfriendbutton);
            declineButton = itemView.findViewById(R.id.declinebutton);
            mutualFriendsTextView = itemView.findViewById(R.id.mutual_friends);
            mutualCoursesTextView = itemView.findViewById(R.id.mutual_courses);
        }
    }
}
