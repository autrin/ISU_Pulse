package com.coms309.isu_pulse_frontend.ui.home;

import java.sql.Date;

public class ListTaskObject {
    private Long cId;
    private Long tId;
    private String section;
    private String title;
    private String description;
    private Date dueDate;
    private String taskType;
    private String courseName;
    private int credits;
    private int mediumTerm;
    private String roomName;
    private String building;

    public ListTaskObject(Long cId, Long tId, String section, String title, String description, Date dueDate, String taskType, String courseName, int credits, int mediumTerm, String roomName, String building) {
        this.cId = cId;
        this.tId = tId;
        this.section = section;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.taskType = taskType;
        this.courseName = courseName;
        this.credits = credits;
        this.mediumTerm = mediumTerm;
        this.roomName = roomName;
        this.building = building;
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

    public Long gettId() {
        return tId;
    }

    public Long getcId() {
        return cId;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getCredits() {
        return credits;
    }

    public int getMediumTerm() {
        return mediumTerm;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getBuilding() {
        return building;
    }
}
