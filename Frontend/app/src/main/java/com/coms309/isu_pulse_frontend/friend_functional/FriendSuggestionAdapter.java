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

public class FriendSuggestionAdapter extends RecyclerView.Adapter<FriendSuggestionAdapter.FriendSuggestionViewHolder> {

    private List<Friend> friendSuggestions;
    private Context context;
    private FriendService friendService;

    public FriendSuggestionAdapter(List<Friend> friendSuggestions, Context context) {
        this.friendSuggestions = friendSuggestions;
        this.context = context;
        this.friendService = new FriendService(context);
    }

    @NonNull
    @Override
    public FriendSuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_item, parent, false);
        return new FriendSuggestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendSuggestionViewHolder holder, int position) {
        Friend student = friendSuggestions.get(position);
        holder.nameTextView.setText(student.getFirstName() + " " + student.getLastName());
        String senderNetId = UserSession.getInstance().getNetId();
        String receiverNetId = student.getNetId();

        CourseService courseService = new CourseService(context);

        FriendSuggestionViewHolder friendSuggestionViewHolder = (FriendSuggestionViewHolder) holder;

        UpdateAccount.fetchProfileData(student.getNetId(), holder.itemView.getContext(), new UpdateAccount.ProfileCallback() {
            @Override
            public void onSuccess(Profile profile) {
                String imageUrl = profile.getProfilePictureUrl();
                Glide.with(holder.itemView.getContext())
                        .load(imageUrl)
                        .into(friendSuggestionViewHolder.profileImageView);
            }

            @Override
            public void onError(VolleyError error) {
                friendSuggestionViewHolder.profileImageView.setImageResource(R.drawable.isu_logo);
            }
        });

        // Fetch mutual courses
        courseService.getMutualCourses(senderNetId, receiverNetId,
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
        friendService.getFriendsInCommon(senderNetId, receiverNetId,
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

        // Add friend button functionality
        holder.addFriendButton.setOnClickListener(v -> {
            friendService.sendFriendRequest(senderNetId, receiverNetId, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(context, "Friend request sent successfully", Toast.LENGTH_SHORT).show();
                    friendSuggestions.remove(holder.getAdapterPosition());
                    notifyItemRemoved(holder.getAdapterPosition());
                    notifyItemRangeChanged(holder.getAdapterPosition(), friendSuggestions.size());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "Failed to send friend request", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return friendSuggestions.size();
    }

    static class FriendSuggestionViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        Button addFriendButton;
        TextView mutualFriendsTextView;
        TextView mutualCoursesTextView;
        ImageView profileImageView;

        FriendSuggestionViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.friend_name);
            profileImageView = itemView.findViewById(R.id.friend_avatar);
            addFriendButton = itemView.findViewById(R.id.addfriendbutton);
            mutualFriendsTextView = itemView.findViewById(R.id.mutual_friends);
            mutualCoursesTextView = itemView.findViewById(R.id.mutual_courses);
        }
    }
}
