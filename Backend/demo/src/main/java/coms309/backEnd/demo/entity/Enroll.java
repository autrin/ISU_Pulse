package coms309.backEnd.demo.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@IdClass(EnrollId.class)
public class Enroll implements Serializable {

    @Id
    private String sId;

    @Id
    private String cId;

    @Id
    private int section;

    @ManyToOne
    @JoinColumn(name = "sId", referencedColumnName = "netId", insertable = false, updatable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "cId", insertable = false, updatable = false)
    private Course course;

    // Getters and Setters
    public String getSId() {
        return sId;
    }

    public void setSId(String sId) {
        this.sId = sId;
    }

    // (Other getters and setters)

    // Constructors
    public Enroll() {
    }

    public Enroll(String sId, String cId, int section, User student, Course course) {
        this.sId = sId;
        this.cId = cId;
        this.section = section;
        this.student = student;
        this.course = course;
    }

    // (Optional: toString method)
}