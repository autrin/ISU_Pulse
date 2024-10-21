package coms309.backEnd.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
public class Enroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;

    @ManyToOne
    @JoinColumn
    @JsonIgnore
    private User student;

    @ManyToOne
    @JoinColumn
    @JsonIgnore
    private Course course;

    public Enroll() {
    }

    public Enroll(long id, User student, Course course) {
        Id = id;
        this.student = student;
        this.course = course;
    }
}