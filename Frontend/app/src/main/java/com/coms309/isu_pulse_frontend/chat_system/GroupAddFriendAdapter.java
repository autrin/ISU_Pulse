package com.coms309.isu_pulse_frontend.chat_system;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.api.UpdateAccount;
import com.coms309.isu_pulse_frontend.friend_functional.Friend;
import com.coms309.isu_pulse_frontend.model.Profile;

import java.util.List;

public class GroupAddFriendAdapter extends RecyclerView.Adapter<GroupAddFriendAdapter.GroupAddFriendViewHolder> {
    private List<Friend> friendList;
    private Context context;
    private OnSelectionChangeListener onSelectionChangeListener;

    public interface OnSelectionChangeListener {
        void onSelectionChanged(int selectedCount);
    }

    public GroupAddFriendAdapter(Context context, List<Friend> friendList, OnSelectionChangeListener listener) {
        this.friendList = friendList;
        this.context = context;
        this.onSelectionChangeListener = listener;
    }

    @NonNull
    @Override
    public GroupAddFriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friend_add_group_chat_item, viewGroup, false);
        return new GroupAddFriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupAddFriendViewHolder holder, int position) {
        Friend friend = friendList.get(position);
        holder.friendName.setText(friend.getFirstName() + " " + friend.getLastName());
        holder.selectButton.setChecked(friend.isSelected());

        holder.selectButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            friend.setSelected(isChecked);
            int selectedCount = (int) friendList.stream().filter(Friend::isSelected).count();
            if (onSelectionChangeListener != null) {
                onSelectionChangeListener.onSelectionChanged(selectedCount);
            }
        });

        GroupAddFriendViewHolder groupAddFriendViewHolder = (GroupAddFriendViewHolder) holder;
        UpdateAccount.fetchProfileData(friend.getNetId(), holder.itemView.getContext(), new UpdateAccount.ProfileCallback() {
            @Override
            public void onSuccess(Profile profile) {
                String imageUrl = profile.getProfilePictureUrl();
                Glide.with(holder.itemView.getContext())
                        .load(imageUrl)
                        .into(groupAddFriendViewHolder.profileImage);
            }

            @Override
            public void onError(VolleyError error) {
                groupAddFriendViewHolder.profileImage.setImageResource(R.drawable.isu_logo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public static class GroupAddFriendViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView friendName;
        CheckBox selectButton;

        public GroupAddFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profile_image);
            friendName = itemView.findViewById(R.id.friend_name);
            selectButton = itemView.findViewById(R.id.select_button);
        }
    }
}
