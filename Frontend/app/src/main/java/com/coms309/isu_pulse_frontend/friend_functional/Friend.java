package com.coms309.isu_pulse_frontend.friend_functional;

public class Friend {
    private String firstName;
    private String lastName;
    private String netId;
    private boolean isSelected;

    public Friend(String firstName, String lastName, String netId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.netId = netId;
    }

    public Friend(String firstName, String lastName, String netId, boolean isSelected) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.netId = netId;
        this.isSelected = false;  // Default selection state is false
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
    public boolean isSelected() {return isSelected;}

    public void setSelected(boolean isChecked) {
        isSelected = isChecked;
    }
}

