package com.coms309.isu_pulse_frontend.chat_system;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;
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
        }
        else {
            holder.textViewName.setText(chatMessage.getSenderFullName());
            holder.textViewMessage.setText(chatMessage.getMessage());
            holder.textViewTimestamp.setText(chatMessage.getTimestamp());
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
        TextView textViewTimestamp;
        Button buttonMessage;

        public MessageViewHolder(View itemview) {
            super(itemview);
            textViewName = itemview.findViewById(R.id.name);
            textViewMessage = itemview.findViewById(R.id.last_message);
            textViewTimestamp = itemview.findViewById(R.id.timestamp);
            buttonMessage = itemview.findViewById(R.id.message_button);
        }
    }
}
