package com.coms309.isu_pulse_frontend.friend_functional;

public class Friend {
    private String firstName;
    private String lastName;
    private String netId;

    public Friend(String firstName, String lastName, String netId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.netId = netId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public String getNetId() {
        return netId;
    }
}

