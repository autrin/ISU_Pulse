package coms309.backEnd.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
@Entity
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String location;

    @OneToOne(mappedBy = "department")
    private List<Faculty> facultyList;

    @OneToMany(mappedBy = "department")
    private List<Course> courseList;

    public Department() {
    }

    public Department(String name, String location) {
        this.name = name;
        this.location = location;
    }
}