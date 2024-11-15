package com.coms309.isu_pulse_frontend.chat_system;

import com.coms309.isu_pulse_frontend.loginsignup.User;

public class ChatMessage {
    private String timestamp;
    private String message;
    private boolean isSent;
    private User sender;
    private User recipient;
    private String senderfirstName;
    private String senderlastName;
    private String recipientfirstName;
    private String recipientlastName;
    private String senderNetId;
    private String recipientNetId;

    public ChatMessage(String message, boolean isSent, String timestamp) {
        this.message = message;
        this.isSent = isSent;
        this.timestamp = timestamp;
    }

    public ChatMessage(User sender, User recipient, String message, String timestamp) {
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
        this.timestamp = timestamp;
    }

    public ChatMessage(String senderfirstName, String senderlastName, String recipientfirstName, String recipientlastName, String senderNetId, String recipientNetId, String message, String timestamp) {
        this.senderfirstName = senderfirstName;
        this.senderlastName = senderlastName;
        this.recipientfirstName = recipientfirstName;
        this.recipientlastName = recipientlastName;
        this.senderNetId = senderNetId;
        this.recipientNetId = recipientNetId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getSenderfirstName() {
        return senderfirstName;
    }

    public String getSenderlastName() {
        return senderlastName;
    }

    public String getRecipientfirstName() {
        return recipientfirstName;
    }

    public String getRecipientlastName() {
        return recipientlastName;
    }

    public String getSenderFullName() {
        return senderfirstName + " " + senderlastName;
    }

    public String getRecipientFullName() {
        return recipientfirstName + " " + recipientlastName;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSent() {
        return isSent;
    }

    public String getTimestamp() {
        return timestamp;
    }
    public String getSenderNetId() {
        return senderNetId;
    }
    public String getRecipientNetId() {
        return recipientNetId;
    }
    public User getSender() {
        return sender;
    }
    public User getRecipient() {
        return recipient;
    }
}
