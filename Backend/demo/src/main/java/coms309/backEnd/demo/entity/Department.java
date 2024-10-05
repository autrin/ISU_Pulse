package coms309.backEnd.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int dId;

    private String name;
    private String location;

    // Constructors
    public Department() {
    }

    public Department(int dId, String name, String location) {
        this.dId = dId;
        this.name = name;
        this.location = location;
    }
}