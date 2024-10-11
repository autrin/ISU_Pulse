package com.coms309.isu_pulse_frontend.ui.home;

public class PersonalTask {
    private String id;
    private String title;
    private String description;
    private long dueDate;
    private String userNetId;

    // Constructor
    public PersonalTask(String id, String title, String description, long dueDate, String userNetId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.userNetId = userNetId;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getDueDate() {
        return dueDate;
    }

    public String getUserNetId() {
        return userNetId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public void setUserNetId(String userNetId) {
        this.userNetId = userNetId;
    }
}