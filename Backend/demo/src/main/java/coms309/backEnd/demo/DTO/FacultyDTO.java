package coms309.backEnd.demo.DTO;

import lombok.Data;

@Data
public class FacultyDTO {
    private String netId;
    private String firstName;
    private String lastName;
    private String email;
    private String hashedPassword;
    private String title;
    private String department;
    private String userType;
    private String profilePictureUrl;
}