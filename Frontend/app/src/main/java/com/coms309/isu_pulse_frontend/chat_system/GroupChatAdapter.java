package com.coms309.isu_pulse_frontend.chat_system;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;

import java.util.List;

public class GroupChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SENT = 0;
    private static final int TYPE_RECEIVED = 1;
    private static final int TYPE_JOINED = 2;

    private List<ChatMessage> chatMessages;
    private String currentUserNetId;

    public GroupChatAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
        this.currentUserNetId = UserSession.getInstance().getNetId();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        String senderNetId = chatMessage.getSenderNetId();
        if (senderNetId == null || senderNetId.isEmpty() || senderNetId.equalsIgnoreCase("null")) {
            return TYPE_JOINED; // System message
        } else if (senderNetId.equals(currentUserNetId)) {
            return TYPE_SENT; // Sent by the current user
        } else {
            return TYPE_RECEIVED; // Received from another user
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_SENT) {
            Log.d("GroupChatAdapter", "Creating SentMessageViewHolder");
            View view = inflater.inflate(R.layout.item_sent_text_message_groupchat, parent, false);
            return new SentMessageViewHolder(view);
        } else if (viewType == TYPE_RECEIVED) {
            Log.d("GroupChatAdapter", "Creating ReceivedMessageViewHolder");
            View view = inflater.inflate(R.layout.item_received_text_message_groupchat, parent, false);
            return new ReceivedMessageViewHolder(view);
        } else {
            Log.d("GroupChatAdapter", "Creating JoinedMessageViewHolder");
            View view = inflater.inflate(R.layout.item_joined_groupchat, parent, false);
            return new JoinedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);

        if (holder instanceof SentMessageViewHolder) {
//            ((SentMessageViewHolder) holder).bind(chatMessage);
            SentMessageViewHolder sentMessageViewHolder = (SentMessageViewHolder) holder;
            sentMessageViewHolder.textViewSenderName.setText(chatMessage.getSenderNetId());
            sentMessageViewHolder.textViewMessage.setText(chatMessage.getMessage());
            sentMessageViewHolder.textViewTimestamp.setText(chatMessage.getTimestamp());
        } else if (holder instanceof ReceivedMessageViewHolder) {
//            ((ReceivedMessageViewHolder) holder).bind(chatMessage);
            ReceivedMessageViewHolder receivedMessageViewHolder = (ReceivedMessageViewHolder) holder;
            receivedMessageViewHolder.textViewSenderName.setText(chatMessage.getSenderNetId());
            receivedMessageViewHolder.textViewMessage.setText(chatMessage.getMessage());
            receivedMessageViewHolder.textViewTimestamp.setText(chatMessage.getTimestamp());
        } else if (holder instanceof JoinedMessageViewHolder) {
//            ((JoinedMessageViewHolder) holder).bind(chatMessage);
            JoinedMessageViewHolder joinedMessageViewHolder = (JoinedMessageViewHolder) holder;
            joinedMessageViewHolder.textViewMessage.setText(chatMessage.getMessage());
            Log.d("GroupChatAdapter", "System message bound: " + chatMessage.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewSenderName, textViewMessage, textViewTimestamp;

        public SentMessageViewHolder(View itemView) {
            super(itemView);
            textViewSenderName = itemView.findViewById(R.id.textViewSenderName);
            textViewMessage = itemView.findViewById(R.id.textViewSentMessage);
            textViewTimestamp = itemView.findViewById(R.id.textViewSentTimestamp);
        }

        public void bind(ChatMessage message) {
            textViewMessage.setText(message.getMessage());
            textViewTimestamp.setText(message.getTimestamp());
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewSenderName, textViewMessage, textViewTimestamp;

        public ReceivedMessageViewHolder(View itemView) {
            super(itemView);
            textViewSenderName = itemView.findViewById(R.id.textViewSenderName);
            textViewMessage = itemView.findViewById(R.id.textViewReceivedMessage);
            textViewTimestamp = itemView.findViewById(R.id.textViewReceivedTimestamp);
        }

        public void bind(ChatMessage message) {
            textViewSenderName.setText(message.getSenderNetId()); // Display sender's NetID
            textViewMessage.setText(message.getMessage());
            textViewTimestamp.setText(message.getTimestamp());
        }
    }

    static class JoinedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage;

        public JoinedMessageViewHolder(View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewSystemMessage);
        }

        public void bind(ChatMessage message) {
            textViewMessage.setText(message.getMessage());
        }
    }
}
