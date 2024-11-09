package com.coms309.isu_pulse_frontend.friend_functional;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.api.CourseService;
import com.coms309.isu_pulse_frontend.api.FriendService;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;
import com.coms309.isu_pulse_frontend.ui.home.Course;

import java.util.List;

public class FriendSentRequestAdapter extends RecyclerView.Adapter<FriendSentRequestAdapter.FriendSentRequestViewHolder> {

    private List<Friend> friendSentRequestList;
    private FriendService friendService;
    private Context context;

    public FriendSentRequestAdapter(Context context, List<Friend> friendSentRequestList) {
        this.friendSentRequestList = friendSentRequestList;
        this.context = context;
        this.friendService = new FriendService(context);
    }

    @NonNull
    @Override
    public FriendSentRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_sent, parent, false);
        return new FriendSentRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendSentRequestViewHolder holder, int position) {
        Friend friend = friendSentRequestList.get(position);
        holder.friendName.setText(friend.getFirstName() + " " + friend.getLastName());
        String senderNetId = UserSession.getInstance().getNetId();
        String receiverNetId = friend.getNetId();

        CourseService courseService = new CourseService(context);

        courseService.getMutualCourses(senderNetId, receiverNetId,
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

        friendService.getFriendsInCommon(senderNetId, receiverNetId,
                response -> {
                    int mutualFriendsCount = response.length();
                    holder.mutualFriendsTextView.setText(mutualFriendsCount + " mutual friends");
                },
                error -> holder.mutualFriendsTextView.setText("0 mutual friends"));

        holder.unsendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendService.unsendFriendRequest(senderNetId, receiverNetId, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, "Friend request unsent successfully", Toast.LENGTH_SHORT).show();
                        friendSentRequestList.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                        notifyItemRangeChanged(holder.getAdapterPosition(), friendSentRequestList.size());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Failed to request unsent unsuccessfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendSentRequestList.size();
    }

    public static class FriendSentRequestViewHolder extends RecyclerView.ViewHolder {
        TextView friendName;
        TextView unsendButton;
        TextView mutualFriendsTextView;
        TextView mutualCoursesTextView;

        public FriendSentRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            friendName = itemView.findViewById(R.id.friend_name);
            unsendButton = itemView.findViewById(R.id.unsendbutton);
            mutualFriendsTextView = itemView.findViewById(R.id.mutual_friends);
            mutualCoursesTextView = itemView.findViewById(R.id.mutual_courses);
        }
    }
}
