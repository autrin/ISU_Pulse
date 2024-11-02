package coms309.backEnd.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Setter;

import java.util.Date;

@Entity
@Data
@Table(name = "Task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dueDate;

    @Enumerated(EnumType.STRING)
    private TaskType taskType;

    @ManyToOne
    @JoinColumn
    @JsonIgnore
    private Schedule schedule;


    public Task() {
    }

    public Task(long id, String title, String description, Date dueDate, TaskType taskType, Schedule schedule) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.taskType = taskType;
        this.schedule = schedule;
    }
}
