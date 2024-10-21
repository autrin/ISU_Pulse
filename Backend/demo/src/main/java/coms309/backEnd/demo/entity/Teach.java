package coms309.backEnd.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Teach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "fId", referencedColumnName = "facultyId")
    private Faculty faculty;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cId", referencedColumnName = "cId")
    private Course course;


    // Constructors
    public Teach() {
    }
}