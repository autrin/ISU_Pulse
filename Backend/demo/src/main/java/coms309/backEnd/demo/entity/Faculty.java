package coms309.backEnd.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Table(name = "Faculty")
public class Faculty {
    @Id
    private String facultyId;

    private String title;

    @OneToOne
    @JoinColumn(name = "facultyId", referencedColumnName = "netId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "dId")
    private Department department;


    public Faculty() {
    }

    public Faculty(String facultyId, String title, User user, Department department) {
        this.facultyId = facultyId;
        this.title = title;
        this.user = user;
        this.department = department;
    }

}
