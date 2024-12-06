package com.coms309.isu_pulse_frontend.chat_system;

public class GroupChatMessage {

    private String senderName;
    private String messageContent;
    private String timestamp;
    private boolean isSentByUser;
    private boolean isSystemMessage;

    public GroupChatMessage(String senderName, String messageContent, String timestamp, boolean isSentByUser, boolean isSystemMessage) {
        this.senderName = senderName;
        this.messageContent = messageContent;
        this.timestamp = timestamp;
        this.isSentByUser = isSentByUser;
        this.isSystemMessage = isSystemMessage;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public boolean isSentByUser() {
        return isSentByUser;
    }

    public boolean isSystemMessage() {
        return isSystemMessage;
    }
}
