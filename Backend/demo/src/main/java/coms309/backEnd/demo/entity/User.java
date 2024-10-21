package coms309.backEnd.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String netId;

    private String firstName;
    private String lastName;
    private String email;
    private String hashedPassword;
    private String profilePictureUrl = "https://as1.ftcdn.net/v2/jpg/01/78/33/12/1000_F_178331249_PIVD6lideletB8pUGKaRy1Z3L3N2YE9n.jpg";

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @OneToMany(mappedBy = "user")
    private List<PersonalTask> personalTaskList;

    @OneToMany(mappedBy = "student")
    private List<Enroll> enrollList;

    @OneToMany(mappedBy = "student")
    private List<Enroll> enrollments;

    @OneToOne(mappedBy = "user")
    @JsonIgnore
    private Profile profile;

    @OneToOne(mappedBy = "user")
    @JsonIgnore
    private Faculty faculty;

    public User() {
    }

    public User(String netId, String firstName, String lastName, String email, String hashedPassword, UserType userType) {
        this.netId = netId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.userType = userType;
    }
}