package coms309.backEnd.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Department")
public class Department {
    @Id
    @Column(name = "dId")
    private String dId;

    @Column(name = "department_name")
    private String name;

    @Column(name = "location")
    private String location;


    public Department() {
    }

    public String getdId() {
        return dId;
    }

    public void setdId(String dId) {
        this.dId = dId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
