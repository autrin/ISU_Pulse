package com.coms309.isu_pulse_frontend.chat_system;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.api.UpdateAccount;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;
import com.coms309.isu_pulse_frontend.model.Profile;

import java.util.List;

public class ChatViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_ONE_ON_ONE = 1;
    private static final int VIEW_TYPE_GROUP = 2;

    private List<ChatMessage> chatMessages;

    public ChatViewAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        return chatMessage.getGroupId() != null ? VIEW_TYPE_GROUP : VIEW_TYPE_ONE_ON_ONE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_GROUP) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_groupchat, parent, false);
            return new GroupViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
            return new OneOnOneViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);

        if (holder instanceof GroupViewHolder) {
            GroupViewHolder groupHolder = (GroupViewHolder) holder;

            groupHolder.textViewName.setText(chatMessage.getGroupName());
            if (chatMessage.getSenderNetId() == null) {
                groupHolder.textViewMessage.setText(chatMessage.getMessage());
            } else if (chatMessage.getSenderNetId().equals(UserSession.getInstance().getNetId())) {
                groupHolder.textViewMessage.setText("You: " + chatMessage.getMessage());
            } else {
                groupHolder.textViewMessage.setText(chatMessage.getMessage());
            }
            groupHolder.textViewTimestamp.setText(chatMessage.getTimestamp());
            groupHolder.buttonMessage.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), GroupChatActivity.class);
                intent.putExtra("groupId", chatMessage.getGroupId());
                intent.putExtra("groupName", chatMessage.getGroupName());
                v.getContext().startActivity(intent);
            });
        } else {
            OneOnOneViewHolder oneOnOneHolder = (OneOnOneViewHolder) holder;

            if (chatMessage.getSenderNetId().equals(UserSession.getInstance().getNetId())) {
                oneOnOneHolder.textViewName.setText(chatMessage.getRecipientFullName());
                oneOnOneHolder.textViewMessage.setText("You: " + chatMessage.getMessage());
            } else {
                oneOnOneHolder.textViewName.setText(chatMessage.getSenderFullName());
                oneOnOneHolder.textViewMessage.setText(chatMessage.getMessage());
            }
            oneOnOneHolder.textViewTimestamp.setText(chatMessage.getTimestamp());
            UpdateAccount.fetchProfileData(chatMessage.getSenderNetId(), holder.itemView.getContext(), new UpdateAccount.ProfileCallback() {
                @Override
                public void onSuccess(Profile profile) {
                    String imageUrl = profile.getProfilePictureUrl();
                    Glide.with(holder.itemView.getContext())
                            .load(imageUrl)
                            .into(oneOnOneHolder.imageViewProfile);
                }

                @Override
                public void onError(VolleyError error) {
                    oneOnOneHolder.imageViewProfile.setImageResource(R.drawable.isu_logo);
                }
            });
            oneOnOneHolder.buttonMessage.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), ChatActivity.class);
                if (chatMessage.getSenderNetId().equals(UserSession.getInstance().getNetId())){
                    intent.putExtra("netId", chatMessage.getRecipientNetId());
                }
                else {
                    intent.putExtra("netId", chatMessage.getSenderNetId());
                }
                v.getContext().startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    static class OneOnOneViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewMessage;
        ImageView imageViewProfile;
        TextView textViewTimestamp;
        Button buttonMessage;

        public OneOnOneViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.name);
            textViewMessage = itemView.findViewById(R.id.last_message);
            imageViewProfile = itemView.findViewById(R.id.profile_image);
            textViewTimestamp = itemView.findViewById(R.id.timestamp);
            buttonMessage = itemView.findViewById(R.id.message_button);
        }
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewMessage;
        TextView textViewTimestamp;
        Button buttonMessage;

        public GroupViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.name);
            textViewMessage = itemView.findViewById(R.id.last_message);
            textViewTimestamp = itemView.findViewById(R.id.timestamp);
            buttonMessage = itemView.findViewById(R.id.message_button);
        }
    }
}
