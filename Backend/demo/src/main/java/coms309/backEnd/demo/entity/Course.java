package coms309.backEnd.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity

public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cId;

    @Column(unique = true)
    private String code;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private int credits;
    private int numSections;

    @ManyToOne
    @JoinColumn(name = "d_id", referencedColumnName = "dId")
    private Department department;

//    @OneToMany
//    @JoinColumn(name = "course_id", referencedColumnName = "cId")
//    private List<Enroll> enrollList;

//    @OneToMany
//    @JoinColumn(name = "task_id", referencedColumnName =  "tId")
//    private List<Task> taskList;       // test to see whether we can get the list of tasks based on courseId

    public Course() {
    }

    public Course(int cId, String code, String title, String description, int credits, int numSections, Department department) {
        this.cId = cId;
        this.code = code;
        this.title = title;
        this.description = description;
        this.credits = credits;
        this.numSections = numSections;
        this.department = department;
    }
}