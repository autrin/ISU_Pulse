package coms309.backEnd.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@IdClass(EnrollId.class)
public class Enroll implements Serializable {


    @Id
    private String sId;

    @Id
    private int cId;

    @Id
    private int section;

    @ManyToOne
    @JoinColumn(name = "sId", referencedColumnName = "netId", insertable = false, updatable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "cId", insertable = false, updatable = false)
    private Course course;

    public String getSId() {
        return sId;
    }

    public void setSId(String sId) {
        this.sId = sId;
    }

    public Course getCourse(){
        return course;
    }

    // Constructors
    public Enroll() {
    }

    public Enroll(String sId, int cId, int section, User student, Course course) {
        this.sId = sId;
        this.cId = cId;
        this.section = section;
        this.student = student;
        this.course = course;
    }

}