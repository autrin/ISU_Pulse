package coms309.backEnd.demo.entity;

import jakarta.persistence.*;

@Entity

public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cId;

    private String code;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private int credits;
    private int numSections;

    @ManyToOne
    @JoinColumn(name = "dId")
    private Department department;

    // Getters and Setters
    public int getCId() {
        return cId;
    }

    public void setCId(int cId) {
        this.cId = cId;
    }

    // Constructors
    public Course() {
    }

    public Course(int cId, String code, String title, String description, int credits, int numSections, Department department) {
        this.cId = cId;
        this.code = code;
        this.title = title;
        this.description = description;
        this.credits = credits;
        this.numSections = numSections;
        this.department = department;
    }
}