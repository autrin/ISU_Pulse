package com.coms309.isu_pulse_frontend.chat_system;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coms309.isu_pulse_frontend.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {
    private List<ChatMessage> messages;

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) { // Received message
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_received_text_message, parent, false);
        } else { // Sent message
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sent_text_message, parent, false);
        }
        return new MessageViewHolder(view, viewType);
    }


    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage chatMessage = messages.get(position);
        holder.textViewMessage.setText(chatMessage.getMessage());
        holder.textViewTimestamp.setText(chatMessage.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isSent() ? 1 : 0;
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
//        removeDuplicates();
        notifyItemInserted(messages.size() - 1);
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

//    public void clearMessages() {
//        messages.clear();
//        notifyDataSetChanged();
//    }

//    private void removeDuplicates() {
//        Set<String> uniqueMessages = new HashSet<>();
//        List<ChatMessage> uniqueList = new ArrayList<>();
//
//        for (ChatMessage message : messages) {
//            if (!uniqueMessages.contains(generateMessageKey(message))) {
//                uniqueMessages.add(generateMessageKey(message));
//                uniqueList.add(message);
//            }
//        }
//
//        messages.clear();
//        messages.addAll(uniqueList);
//    }

    private String generateMessageKey(ChatMessage message) {
        return message.getTimestamp() + "_" + message.toString()+ "_" + message.isSent();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage;
        TextView textViewTimestamp;

        MessageViewHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == 0) {
                textViewMessage = itemView.findViewById(R.id.textViewReceivedMessage); // For received messages
                textViewTimestamp = itemView.findViewById(R.id.textViewReceivedTimestamp);
            } else {
                textViewMessage = itemView.findViewById(R.id.textViewSentMessage); // For sent messages
                textViewTimestamp = itemView.findViewById(R.id.textViewSentTimestamp);
            }
        }

        void bind(String message) {
            textViewMessage.setText(message);
        }
    }
}

