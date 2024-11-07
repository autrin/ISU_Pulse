package com.coms309.isu_pulse_frontend.model;

public class Department {
    private String name;
    private String location;
    private int did;

    public Department(String name, String location, int did) {
        this.name = name;
        this.location = location;
        this.did = did;
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

    public int getDid() {
        return did;
    }

    public void setDid(int did) {
        this.did = did;
    }
}
