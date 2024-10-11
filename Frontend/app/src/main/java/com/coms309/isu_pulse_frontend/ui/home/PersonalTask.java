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
}
