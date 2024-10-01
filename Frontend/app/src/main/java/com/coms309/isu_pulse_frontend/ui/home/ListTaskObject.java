package com.coms309.isu_pulse_frontend.ui.home;

import java.sql.Date;

public class ListTaskObject {
    private Long cId;
    private Long tId;
    private String section;
    private String title;
    private String description;
    private Date date;
    private String taskType;
    public ListTaskObject(Long cId, Long tId, String section, String title, String description, Date date, String taskType) {
        this.cId = cId;
        this.tId = tId;
        this.section = section;
        this.title = title;
        this.description = description;
        this.date = date;
        this.taskType = taskType;
    }
    public String getTaskType() {
        return taskType;
    }

    public Date getDate() {
        return date;
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

}
