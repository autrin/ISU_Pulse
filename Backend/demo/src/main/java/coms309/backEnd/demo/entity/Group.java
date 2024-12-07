package coms309.backEnd.demo.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "user_group")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDateTime timestamp;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Join> joins;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<GroupMessages> groupMessages;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;


    public Group(String name, User creator) {
        this.name = name != null && !name.isEmpty() ? name : "Untitled";
        this.creator = creator;
        this.timestamp = LocalDateTime.now();
    }

    public Group() {
        this.name = "Untitled";
        this.timestamp = LocalDateTime.now();
    }
}
