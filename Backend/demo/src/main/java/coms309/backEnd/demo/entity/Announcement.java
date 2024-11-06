package coms309.backEnd.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Faculty faculty;

    public Announcement() {
        this.timestamp = LocalDateTime.now();
    }

    public Announcement(String content, Schedule schedule, Faculty faculty) {
        this.content = content;
        this.schedule = schedule;
        this.faculty = faculty;
        this.timestamp = LocalDateTime.now();
    }
}
