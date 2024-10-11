package com.coms309.isu_pulse_frontend.ui.home;

public class PersonalTask {
    private String title;
    private String description;
    private String dueDate;
    private String userNetId;

    public PersonalTask(String title, String description, String dueDate, String userNetId) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.userNetId = userNetId;
    }
    public String getUserNetId() {
        return userNetId;
    }

    public void setUserNetId(String userNetId) {
        this.userNetId = userNetId;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
