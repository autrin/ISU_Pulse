package coms309.backEnd.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class PersonalTask {

    // This is the id for personal task
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dueDate;

    @ManyToOne
    @JoinColumn
    private User user;

    public PersonalTask(String title, String description, Date dueDate, User user) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.user = user;
    }

    public PersonalTask() {
    }
}
