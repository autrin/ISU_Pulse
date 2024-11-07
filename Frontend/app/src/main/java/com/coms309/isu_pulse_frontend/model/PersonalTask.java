package com.coms309.isu_pulse_frontend.model;

public class PersonalTask {
    private long id;
    private String title;
    private String description;
    private Long dueDate;
    private String userNetId;

    // Constructor
    public PersonalTask(int id, String title, String description, long dueDate, String userNetId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.userNetId = userNetId;
    }

    // Getters
    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Long getDueDate() {
        return dueDate;
    }

    public String getUserNetId() {
        return userNetId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDueDate(Long dueDate) {
        this.dueDate = dueDate;
    }

    public void setUserNetId(String userNetId) {
        this.userNetId = userNetId;
    }
}
