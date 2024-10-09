package coms309.backEnd.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Setter;

import java.util.Date;

@Entity
@Data
@Table(name = "Task")
public class Task {
    // change this instance variable into integer
    @Id
    private String tId;

    private int section;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dueDate;

    @Enumerated(EnumType.STRING)
    private TaskType taskType;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cId")
    private Course course;


    public Task() {
    }

    public Task(String tId, int section, String title, String description, Date dueDate, TaskType taskType, Course course) {
        this.tId = tId;
        this.section = section;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.taskType = taskType;
        this.course = course;
    }
}
