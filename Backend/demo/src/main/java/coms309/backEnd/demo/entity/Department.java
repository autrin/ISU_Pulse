package coms309.backEnd.demo.entity;

import jakarta.persistence.*;

@Entity
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int dId;

    private String name;
    private String location;

    // Getters and Setters
    public int getDId() {
        return dId;
    }

    public void setDId(int dId) {
        this.dId = dId;
    }

    // Constructors
    public Department() {
    }

    public Department(int dId, String name, String location) {
        this.dId = dId;
        this.name = name;
        this.location = location;
    }
}