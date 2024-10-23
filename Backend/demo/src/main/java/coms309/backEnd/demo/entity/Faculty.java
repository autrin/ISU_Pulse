package coms309.backEnd.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Entity
@Table(name = "Faculty")
public class Faculty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    @OneToOne
    @JoinColumn
    private User user;

    @ManyToOne
    @JoinColumn
    private Department department;

    @OneToMany(mappedBy = "faculty")
    private List<Teach> teachList;

    public Faculty() {
    }

    public Faculty(String title, User user, Department department) {
        this.title = title;
        this.user = user;
        this.department = department;
    }
}
