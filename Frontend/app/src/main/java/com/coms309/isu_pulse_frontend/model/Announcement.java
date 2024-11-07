package com.coms309.isu_pulse_frontend.model;

public class Announcement {

    private Long id;
    private String content;
    private Long scheduleId;
    private String facultyNetId;
    private String timestamp;
    //    private boolean seenStatus;  // Optional, relevant for frontend display
    private String courseName;

    public Announcement(Long id, String content, Long scheduleId, String facultyNetId, String timestamp, String courseName) {
        this.id = id;
        this.content = content;
        this.scheduleId = scheduleId;
        this.facultyNetId = facultyNetId;
        this.timestamp = timestamp;
//        this.seenStatus = seenStatus;
        this.courseName = courseName;
    }


    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getFacultyNetId() {
        return facultyNetId;
    }

    public void setFacultyNetId(String facultyNetId) {
        this.facultyNetId = facultyNetId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

//    public boolean isSeenStatus() {
//        return seenStatus;
//    }
//
//    public void setSeenStatus(boolean seenStatus) {
//        this.seenStatus = seenStatus;
//    }
}
