package coms309.backEnd.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Teach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn
    private Faculty faculty;

    @OneToOne
    @JoinColumn
    private Schedule schedule;

    // Constructors
    public Teach() {
    }
}