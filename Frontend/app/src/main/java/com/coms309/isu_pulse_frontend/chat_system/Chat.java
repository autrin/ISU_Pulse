package com.coms309.isu_pulse_frontend.chat_system;

public class Chat {
    private String firstName;
    private String lastName;
    private String lastMessage;
    private String timestamp;
    private String profileImage; // URL or resource ID for the profile image

    public Chat(String firstName, String lastName, String lastMessage, String timestamp, String profileImage) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.profileImage = profileImage;
    }

    public Chat(String firstName, String lastName, String lastMessage, String timestamp) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getLastMessage() { return lastMessage; }
    public String getTimestamp() { return timestamp; }
    public String getProfileImage() { return profileImage; }
}

