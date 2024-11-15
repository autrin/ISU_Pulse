package com.coms309.isu_pulse_frontend.friend_functional;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.coms309.isu_pulse_frontend.student_display.Student;
import com.coms309.isu_pulse_frontend.student_display.StudentAdapter;
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

        holder.addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendService.sendFriendRequest(senderNetId, receiverNetId, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, "Friend sent successfully", Toast.LENGTH_SHORT).show();
                        friendSuggestions.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                        notifyItemRangeChanged(holder.getAdapterPosition(), friendSuggestions.size());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Friend sent unsuccessfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
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


        FriendSuggestionViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.friend_name);
            addFriendButton = itemView.findViewById(R.id.addfriendbutton);
            mutualFriendsTextView = itemView.findViewById(R.id.mutual_friends);
            mutualCoursesTextView = itemView.findViewById(R.id.mutual_courses);
        }
    }
}