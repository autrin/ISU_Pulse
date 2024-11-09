package com.coms309.isu_pulse_frontend.schedule;

import com.coms309.isu_pulse_frontend.ui.home.Course;
import com.coms309.isu_pulse_frontend.ui.home.Department;

public class Schedule {

    private String section;
    private String recurringPattern;
    private String startTime;
    private String endTime;
    private Course course;
    private Department department;

    public Schedule(Course course, Department department, String section, String recurringPattern, String startTime, String endTime){
        this.course = course;
        this.department = department;
        this.section = section;
        this.recurringPattern = recurringPattern;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Schedule(Course course, String section, String recurringPattern, String startTime, String endTime){
        this.course = course;
        this.section = section;
        this.recurringPattern = recurringPattern;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getSection() {
        return section;
    }

    public String getRecurringPattern() {
        return recurringPattern;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public Course getCourse() {
        return course;
    }

    public Department getDepartment() {
        return department;
    }
}
