package coms309.backEnd.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
@Data
@Entity
@IdClass(TeachId.class)
public class Teach implements Serializable {

    @Id
    private String fId;

    @Id
    private int cId;

    @Id
    private int section;

    private String classTime;

    @ManyToOne
    @JoinColumn(name = "fId", referencedColumnName = "facultyId")
    private Faculty faculty;

    @ManyToOne
    @JoinColumn(name = "cId", referencedColumnName = "cId")
    private Course course;

    public String getFId() {
        return fId;
    }

    public void setFId(String fId) {
        this.fId = fId;
    }

    // Constructors
    public Teach() {
    }

    public Teach(String fId, int cId, int section, String classTime, Faculty faculty, Course course) {
        this.fId = fId;
        this.cId = cId;
        this.section = section;
        this.classTime = classTime;
        this.faculty = faculty;
        this.course = course;
    }
}