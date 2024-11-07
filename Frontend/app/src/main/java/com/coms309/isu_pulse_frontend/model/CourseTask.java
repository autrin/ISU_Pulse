package com.coms309.isu_pulse_frontend.model;

import java.sql.Date;

public class CourseTask {
    private long id;
    private int section;
    private String title;
    private String description;
    private Date dueDate;
    private String taskType;
    private Course course;
    private Department department;

    public CourseTask(long id, String title, String description, Date dueDate, String taskType) {
        this.id = id;
        this.section = section;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.taskType = taskType;
    }

    public String getTaskType() {
        return taskType;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public long getId() {
        return id;
    }

}
