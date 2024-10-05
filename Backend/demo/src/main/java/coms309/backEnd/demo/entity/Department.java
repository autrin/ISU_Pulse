package coms309.backEnd.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Department {
    @Id
    @GeneratedValue
    // fix: no strategy = IDENTITY
    private String dId;

    private String name;
    private String location;

    // Constructors
    public Department() {
    }

    public Department(String dId, String name, String location) {
        this.dId = dId;
        this.name = name;
        this.location = location;
    }
}