package coms309.backEnd.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "course" )
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Course ID")
    private long cId;

    @Column(name = "code")
    private long code;

    @Column(name = "Title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "credits")
    private int credits;

    @Column(name = "dId")
    private long dId;

    @Column(name = "numSections")
    private int numSections;

    public Course() {
    }

    public void setcId(int cId) {
        this.cId = cId;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public void setdId(int dId) {
        this.dId = dId;
    }

    public void setNumSections(int numSections) {
        this.numSections = numSections;
    }

    public long getcId() {
        return cId;
    }

    public long getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getCredits() {
        return credits;
    }

    public long getdId() {
        return dId;
    }

    public int getNumSections() {
        return numSections;
    }
}
