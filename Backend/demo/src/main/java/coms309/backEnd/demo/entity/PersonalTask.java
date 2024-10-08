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
    private int personalTaskId;

    private String title;

    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dueDate;

    @ManyToOne
    @JoinColumn(name = "sId", referencedColumnName = "netId")
    @JsonIgnore
    private User user;

    public PersonalTask() {
    }

    public PersonalTask(String title, Date dueDate, String description, User userNetId) {
        this.title = title;
        this.dueDate = dueDate;
        this.description = description;
        this.user = userNetId;
    }
}
