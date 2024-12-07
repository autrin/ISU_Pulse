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

public class ChatViewAdapter extends RecyclerView.Adapter<ChatViewAdapter.MessageViewHolder> {
    private List<ChatMessage> chatMessages;

    public ChatViewAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        if (chatMessage.getSenderNetId().equals(UserSession.getInstance().getNetId())){
            holder.textViewName.setText(chatMessage.getRecipientFullName());
            holder.textViewMessage.setText("You: " + chatMessage.getMessage());
            holder.textViewTimestamp.setText(chatMessage.getTimestamp());
            UpdateAccount.fetchProfileData(UserSession.getInstance().getNetId(), holder.itemView.getContext(), new UpdateAccount.ProfileCallback() {
                @Override
                public void onSuccess(Profile profile) {
                    String imageUrl = profile.getProfilePictureUrl();  // Assume the Profile class has a method to get profile picture URL
                    Glide.with(holder.itemView.getContext())
                            .load(imageUrl)
                            .into(holder.imageViewProfile);  // Set the profile image to the ImageView
                }

                @Override
                public void onError(VolleyError error) {
                    // Handle the error, e.g., show a default image or log the error
                    holder.imageViewProfile.setImageResource(R.drawable.isu_logo);
                }
            });
        }
        else {
            holder.textViewName.setText(chatMessage.getSenderFullName());
            holder.textViewMessage.setText(chatMessage.getMessage());
            holder.textViewTimestamp.setText(chatMessage.getTimestamp());
            UpdateAccount.fetchProfileData(chatMessage.getSenderNetId(), holder.itemView.getContext(), new UpdateAccount.ProfileCallback() {
                @Override
                public void onSuccess(Profile profile) {
                    String imageUrl = profile.getProfilePictureUrl();  // Assume the Profile class has a method to get profile picture URL
                    Glide.with(holder.itemView.getContext())
                            .load(imageUrl)
                            .into(holder.imageViewProfile);  // Set the profile image to the ImageView
                }

                @Override
                public void onError(VolleyError error) {
                    // Handle the error, e.g., show a default image or log the error
                    holder.imageViewProfile.setImageResource(R.drawable.isu_logo);
                }
            });
        }

        holder.buttonMessage.setOnClickListener(v -> {
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

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewMessage;
        ImageView imageViewProfile;
        TextView textViewTimestamp;
        Button buttonMessage;

        public MessageViewHolder(View itemview) {
            super(itemview);
            textViewName = itemview.findViewById(R.id.name);
            textViewMessage = itemview.findViewById(R.id.last_message);
            imageViewProfile = itemview.findViewById(R.id.profile_image);
            textViewTimestamp = itemview.findViewById(R.id.timestamp);
            buttonMessage = itemview.findViewById(R.id.message_button);
        }
    }
}
