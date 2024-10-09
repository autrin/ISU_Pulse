package coms309.backEnd.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Entity
public class User {
    @Id
    private String netId;

    private String firstName;
    private String lastName;
    private String email;
    private String hashedPassword;
    private String profilePictureUrl;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Enroll> enrollments;

    // Constructors
    public User() {
    }

    public User(String netId, String firstName, String lastName, String email, String hashedPassword, String profilePictureUrl, UserType userType) {
        this.netId = netId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.profilePictureUrl = profilePictureUrl;
        this.userType = userType;
    }
}