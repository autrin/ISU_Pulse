package com.coms309.isu_pulse_frontend.ui.home;

public class Course {
    private String code;
    private String title;
    private String description;
    private int credits;
    private int numSections;
    private Department department;
    private int cId;

    public Course(String code, String title, String description, int credits, int numSections, Department department, int cId) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.credits = credits;
        this.numSections = numSections;
        this.department = department;
        this.cId = cId;
    }

    public Course(String code, String title, String description, int credits, int numSections, String name, String location, int did, int cid) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.credits = credits;
        this.numSections = numSections;
        this.cId = cId;
        this.department = new Department(name, location, did);
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
    public int getcId() {
        return cId;
    }
    public void setcId(int cId) {
        this.cId = cId;
    }

}