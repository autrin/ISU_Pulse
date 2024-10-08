package com.coms309.isu_pulse_frontend.ui.home;

import java.sql.Date;

public class ListTaskObject {
    private Long cId;
    private String tId;
    private String section;
    private String title;
    private String description;
    private Date dueDate;
    private String taskType;
    private String courseCode;
    private String courseTitle;
    private String departmentName;
    private String departmentLocation;

    public ListTaskObject(Long cId, String tId, String section, String title, String description, Date dueDate, String taskType, String courseCode, String courseTitle, String departmentName, String departmentLocation) {
        this.cId = cId;
        this.tId = tId;
        this.section = section;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.taskType = taskType;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.departmentName = departmentName;
        this.departmentLocation = departmentLocation;
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

    public String getSection() {
        return section;
    }

    public String gettId() {
        return tId;
    }

    public Long getcId() {
        return cId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public String getDepartmentLocation() {
        return departmentLocation;
    }
}
