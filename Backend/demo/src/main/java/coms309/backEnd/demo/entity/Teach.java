package coms309.backEnd.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;

public class Teach {
    @Id
    @Column(name = "fId")
    private Long fId;

    @Column(name = "cId")
    private Long cId;

    @Column(name = "section")
    private String section;

    @Column(name = "classTime")
    private String classTime;

    public Teach() {
    }

    public Long getcId() {
        return cId;
    }

    public void setcId(Long cId) {
        this.cId = cId;
    }

    public Long getfId() {
        return fId;
    }

    public void setfId(Long fId) {
        this.fId = fId;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getClassTime() {
        return classTime;
    }

    public void setClassTime(String classTime) {
        this.classTime = classTime;
    }
}
