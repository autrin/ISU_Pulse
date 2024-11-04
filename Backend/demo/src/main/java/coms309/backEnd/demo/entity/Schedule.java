package coms309.backEnd.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Course course;

    private String section;

    @Column(nullable = false)
    private String recurringPattern;

    @Column(nullable = false)
    private java.time.LocalTime startTime;

    @Column(nullable = false)
    private java.time.LocalTime endTime;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore
    private List<Enroll> enrollList;

    // Same class but different schedule(section) has different due dates for tasks or have different tasks
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore
    private  List<Task> taskList;

    @OneToOne(mappedBy = "schedule", cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore
    private Teach teach;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Announcement> announcements;

    public Schedule() {
    }
}
