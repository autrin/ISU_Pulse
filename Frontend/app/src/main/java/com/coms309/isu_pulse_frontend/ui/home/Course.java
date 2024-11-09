package com.coms309.isu_pulse_frontend.ui.home;

public class Course {
    private String code;
    private String title;
    private String description;
    private int credits;
    private int numSections;
    private Department department;

    public Course(String code, String title, String description, int credits, Department department) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.credits = credits;
        this.department = department;
    }

    public Course(String code, String title, String description, int credits, String name, String location) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.credits = credits;
        this.numSections = numSections;
        this.department = new Department(name, location);
    }

    // Getters and setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getNumSections() {
        return numSections;
    }

    public void setNumSections(int numSections) {
        this.numSections = numSections;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

}