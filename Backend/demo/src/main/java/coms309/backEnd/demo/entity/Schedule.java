package coms309.backEnd.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;


// One course on esection
// Example: COMS 309 A has many tasks
// work like section
@Data
@Entity
public class Schedule {

    @ManyToOne
    private Course course;

    private String section;

    //“MWF|11:00-13:00”
    private String schedule;

    @OneToMany(mappedBy = "schedule")
    private List<Enroll> enrollList;

    // Same class but different schedule(section) has different due dates for tasks or have different tasks
    @OneToMany(mappedBy = "schedule")
    private  List<Task> taskList;

    public Schedule() {
    }

    public Schedule(Course course, String section, String schedule, List<Enroll> enrollList, List<Task> taskList) {
        this.course = course;
        this.section = section;
        this.schedule = schedule;
        this.enrollList = enrollList;
        this.taskList = taskList;
    }
}
