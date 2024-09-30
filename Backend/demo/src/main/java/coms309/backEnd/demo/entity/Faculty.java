package coms309.backEnd.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Faculty")
public class Faculty {
    @OneToOne
    @JoinColumn(name = "facultyId", referencedColumnName = "netId", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "departmentId", referencedColumnName = "dId", insertable = false, updatable = false)
    private Department department;

}
