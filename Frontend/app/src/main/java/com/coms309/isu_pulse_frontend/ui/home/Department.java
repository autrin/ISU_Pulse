package com.coms309.isu_pulse_frontend.ui.home;

public class Department {
    private String name;
    private String location;
    private int did;

    public Department(String name, String location) {
        this.name = name;
        this.location = location;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDid(int did) {
        this.did = did;
    }
}