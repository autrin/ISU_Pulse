package com.coms309.isu_pulse_frontend.loginsignup;

public class User {

    private String netId;
    private String firstName;
    private String lastName;
    private String email;
    private String hashedPassword;
    private String profilePictureUrl;
    private String userType;

    public User(String netId, String firstName, String lastName, String email, String hashedPassword, String profilePictureUrl, String userType) {
        this.netId = netId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.profilePictureUrl = profilePictureUrl;
        this.userType = userType;
    }

    // Getters and setters for the fields
    public String getNetId() {
        return netId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public String getUserType() {
        return userType;
    }
}
