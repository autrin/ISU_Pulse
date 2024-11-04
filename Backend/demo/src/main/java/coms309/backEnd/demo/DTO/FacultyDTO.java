package coms309.backEnd.demo.DTO;

import lombok.Data;

// FacultySignupDTO.java
@Data
public class FacultyDTO {
    private String netId;
    private String firstName;
    private String lastName;
    private String email;
    private String hashedPassword;  // Ensure password is hashed before saving
    private String title;
    private String department;
    private String userType;
    private String profilePictureUrl;
}