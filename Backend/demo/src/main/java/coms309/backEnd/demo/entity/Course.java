package coms309.backEnd.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Entity

public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String code;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private int credits;
    private int numSections;

    @OneToMany(mappedBy = "course")
    @JsonIgnore
    private List<Enroll> enrollList;

    @ManyToOne
    @JoinColumn
    @JsonIgnore
    private Department department;

    @OneToMany(mappedBy = "course")
    @JsonIgnore
    private List<Section> sectionList;

    public Course() {
    }

    public Course(long id, String code, String title, String description, int credits, int numSections, List<Enroll> enrollList, Department department, List<Section> sectionList) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.description = description;
        this.credits = credits;
        this.numSections = numSections;
        this.enrollList = enrollList;
        this.department = department;
        this.sectionList = sectionList;
    }

    //    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    private Set<Enroll> enrollments;

//    @OneToMany
//    @JoinColumn(name = "course_id", referencedColumnName = "cId")
//    private List<Enroll> enrollList;

//    @OneToMany
//    @JoinColumn(name = "task_id", referencedColumnName =  "tId")
//    private List<Task> taskList;       // test to see whether we can get the list of tasks based on courseId

}