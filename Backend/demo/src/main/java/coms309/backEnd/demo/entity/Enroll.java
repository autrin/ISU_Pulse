package coms309.backEnd.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@IdClass(EnrollId.class)
public class Enroll implements Serializable {


    @Id
    private String studentid;

    @Id
    private int courseid;

    @Id
    private int section;

    @ManyToOne
    @JoinColumn(name = "studentid", referencedColumnName = "netId", insertable = false, updatable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "courseid", insertable = false, updatable = false)
    private Course course;

    // Constructors
    public Enroll() {
    }

    public Enroll(String studentid, int cId, int section, User student, Course course) {
        this.studentid = studentid;
        this.courseid = cId;
        this.section = section;
        this.student = student;
        this.course = course;
    }

}