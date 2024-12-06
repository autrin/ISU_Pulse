package com.coms309.isu_pulse_frontend.chat_system;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coms309.isu_pulse_frontend.R;

import java.util.List;

public class GroupChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    private static final int VIEW_TYPE_SYSTEM = 3;

    private List<GroupChatMessage> messages;

    public GroupChatAdapter(List<GroupChatMessage> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        GroupChatMessage message = messages.get(position);
        if (message.isSystemMessage()) {
            return VIEW_TYPE_SYSTEM;
        } else if (message.isSentByUser()) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sent_text_message_groupchat, parent, false);
            return new SentMessageViewHolder(view);
        } else if (viewType == VIEW_TYPE_RECEIVED) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_received_text_message_groupchat, parent, false);
            return new ReceivedMessageViewHolder(view);
        } else { // VIEW_TYPE_SYSTEM
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_joined_groupchat, parent, false);
            return new SystemMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        GroupChatMessage message = messages.get(position);

        if (holder instanceof SentMessageViewHolder) {
            SentMessageViewHolder sentHolder = (SentMessageViewHolder) holder;
            sentHolder.textViewSenderName.setText(message.getSenderName());
            sentHolder.textViewSentMessage.setText(message.getMessageContent());
            sentHolder.textViewSentTimestamp.setText(message.getTimestamp());
        } else if (holder instanceof ReceivedMessageViewHolder) {
            ReceivedMessageViewHolder receivedHolder = (ReceivedMessageViewHolder) holder;
            receivedHolder.textViewSenderName.setText(message.getSenderName());
            receivedHolder.textViewReceivedMessage.setText(message.getMessageContent());
            receivedHolder.textViewReceivedTimestamp.setText(message.getTimestamp());
        } else if (holder instanceof SystemMessageViewHolder) {
            SystemMessageViewHolder systemHolder = (SystemMessageViewHolder) holder;
            systemHolder.textViewSystemMessage.setText(message.getMessageContent());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addMessage(GroupChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewSenderName, textViewSentMessage, textViewSentTimestamp;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSenderName = itemView.findViewById(R.id.textViewSenderName);
            textViewSentMessage = itemView.findViewById(R.id.textViewSentMessage);
            textViewSentTimestamp = itemView.findViewById(R.id.textViewSentTimestamp);
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewSenderName, textViewReceivedMessage, textViewReceivedTimestamp;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSenderName = itemView.findViewById(R.id.textViewSenderName);
            textViewReceivedMessage = itemView.findViewById(R.id.textViewReceivedMessage);
            textViewReceivedTimestamp = itemView.findViewById(R.id.textViewReceivedTimestamp);
        }
    }

    static class SystemMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewSystemMessage;

        public SystemMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSystemMessage = itemView.findViewById(R.id.textViewSystemMessage);
        }
    }
}
