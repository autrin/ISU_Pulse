package com.coms309.isu_pulse_frontend.student_display;

public class Student {
    private String firstName;
    private String lastName;
    private String netId;

    public Student(String firstName, String lastName, String netId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.netId = netId;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getNetId() { return netId; }
}
