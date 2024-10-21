package coms309.backEnd.demo.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // Section can be 1,2,3 or A, B, C
    private String sectionName;

    @ManyToOne
    @JoinColumn
    private Course course;

    private String instructor;

    //“MWF|11:00-13:00”
    private List<String> schedule;

    @OneToMany(mappedBy = "section")
    private List<Task> taskList;

    public Section() {
    }

    public Section(long id, String sectionName, Course course, String instructor, List<String> schedule, List<Task> taskList) {
        this.id = id;
        this.sectionName = sectionName;
        this.course = course;
        this.instructor = instructor;
        this.schedule = schedule;
        this.taskList = taskList;
    }
}
