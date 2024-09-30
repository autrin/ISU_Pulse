package coms309.backEnd.demo.entity;

import jakarta.persistence.*;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int uId;

    @Column(unique = true)
    private String netId;

    private String firstName;
    private String lastName;
    private String email;
    private String hashedPassword;
    private String profilePictureUrl;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    // Getters and Setters
    public int getUId() {
        return uId;
    }

    // Setter is optional and often omitted for ID fields managed by the database
    public void setUId(int uId) {
        this.uId = uId;
    }

    public String getNetId() {
        return netId;
    }

    public void setNetId(String netId) {
        this.netId = netId;
    }

    public String getFirstName() {
        return firstName;
    }

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