package com.coms309.isu_pulse_frontend.chat_system;

import java.time.LocalDate;

public class ChatMessageDTO {
    private String senderNetId;
    private String recipientNetId;
    private String content;
    private String timestamp;

    public ChatMessageDTO(String senderNetId, String recipientNetId, String content, String timestamp) {
        this.senderNetId = senderNetId;
        this.recipientNetId = recipientNetId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public ChatMessageDTO(String senderNetId, String recipientNetId, String content) {
        this.senderNetId = senderNetId;
        this.recipientNetId = recipientNetId;
        this.content = content;
    }

    public String getSenderNetId() {
        return senderNetId;
    }

    public String getRecipientNetId() {
        return recipientNetId;
    }

    public String getContent() {
        return content;
    }

    public String getTimestamp() {
        return timestamp;
    }
}

