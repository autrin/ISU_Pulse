package com.coms309.isu_pulse_frontend.ui.home;

import java.sql.Date;

public class CourseTask {
    private String tId;
    private int section;
    private String title;
    private String description;
    private Date dueDate;
    private String taskType;
    private Course course;
    private Department department;

    public CourseTask(String tId, int section, String title, String description, Date dueDate, String taskType, Course course, Department department) {
        this.tId = tId;
        this.section = section;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.taskType = taskType;
        this.course = course;
        this.department = department;
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

    public int getSection() {
        return section;
    }

    public String gettId() {
        return tId;
    }


    public Course getCourse() {
        return course;
    }

    public Department getDepartment() {
        return department;
    }
}
