package coms309.backEnd.demo.controller;

import coms309.backEnd.demo.DTO.FacultyDTO;
import coms309.backEnd.demo.entity.*;
import coms309.backEnd.demo.repository.ChatMessageRepository;
import coms309.backEnd.demo.repository.DepartmentRepository;
import coms309.backEnd.demo.repository.FacultyRepository;
import coms309.backEnd.demo.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j // Lombok annotation for logging
public class UserController {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final DepartmentRepository departmentRepository;

    @Autowired
    private final ChatMessageRepository chatMessageRepository;

    public UserController(UserRepository userRepository, FacultyRepository facultyRepository, DepartmentRepository departmentRepository, ChatMessageRepository chatMessageRepository) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    /**
     * Fetches a user by their NetID.
     *
     * @param netId The NetID of the user to fetch.
     * @return The User entity if found, or an error message if not.
     */
    @Operation(summary = "Find a user by NetID", description = "Retrieve user details using their unique NetID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)
    })

    @GetMapping("/{netId}")
    public ResponseEntity<User> getUserByNetId(@Parameter(description = "Unique NetID of the user") @PathVariable String netId) {
        Optional<User> userOptional = userRepository.findUserByNetId(netId);
        if (!userOptional.isPresent())
            return ResponseEntity.status(404).body(null);
        User user = userOptional.get();
        return ResponseEntity.status(200).body(user);
    }

    /**
     * Registers a new student user.
     *
     * @param user The user details to register.
     * @return Success or error message.
     */
    @Operation(summary = "Register a new student", description = "Registers a new student user in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "User already exists")
    })
    @PostMapping
    public ResponseEntity<String> registerNewStudent(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "The user object containing details like netId, name, email, and password for registration.",
            content = @Content(schema = @Schema(implementation = User.class))
    ) @RequestBody User user){
        Optional<User> userOptional = userRepository.findUserByNetId(user.getNetId());
        if (userOptional.isPresent())
            return ResponseEntity.status(400).body("NetID already exists.");
        Faculty faculty = null;

        Profile profile = new Profile();
        user.setProfile(profile);
        profile.setUser(user);

        // Save user (will cascade and save profile as well)
        userRepository.save(user);

        return ResponseEntity.status(200).body("User is successfully registered.");
    }



    @Operation(summary = "Register a new faculty", description = "Registers a new faculty user in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Faculty signup successful"),
            @ApiResponse(responseCode = "400", description = "User with this NetID already exists")
    })
    @PostMapping("/faculty")
    public ResponseEntity<Map<String, String>> signupFaculty(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Faculty details for signup",
            required = true,
            content = @Content(schema = @Schema(implementation = FacultyDTO.class))) @RequestBody FacultyDTO facultyDTO) {
        Map<String, String> response = new HashMap<>();

        // Check if user already exists by netId
        if (userRepository.existsByNetId(facultyDTO.getNetId())) {
            response.put("message", "User with this NetId already exists.");
            return ResponseEntity.badRequest().body(response);
        }

        // Create new User entity
        User user = new User(
                facultyDTO.getNetId(),
                facultyDTO.getFirstName(),
                facultyDTO.getLastName(),
                facultyDTO.getEmail(),
                facultyDTO.getHashedPassword(),  // Assuming password is already hashed
                UserType.FACULTY
        );

        user.setProfilePictureUrl(facultyDTO.getProfilePictureUrl());

        Profile profile = new Profile();
        user.setProfile(profile);
        profile.setUser(user);

        // Retrieve the Department by ID and create Faculty entity
        Department department = departmentRepository.findByName(facultyDTO.getDepartment())
                .orElseThrow(() -> new IllegalStateException("Department does not exist"));

        Faculty faculty = new Faculty(facultyDTO.getTitle(), user, department);
        user.setFaculty(faculty);  // Set the faculty for bidirectional relationship

        // Save both User and Faculty
        userRepository.save(user);  // Cascade will save Faculty

        response.put("message", "Faculty signup successful.");
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Update user password", description = "Allows a user to update their password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or password is unchanged")
    })
    @Transactional
    @PutMapping(path = "updatepw/{netId}")
    public ResponseEntity<String> updateUserPassword(@Parameter(description = "NetID of the user", required = true) @PathVariable String netId,
                                                     @RequestParam(required = true) String newPassword) {
        Optional<User> userOptional = userRepository.findUserByNetId(netId);
        if (!userOptional.isPresent())
            return ResponseEntity.status(400).body("User does not exist.");
        User user = userOptional.get();
        if (user.getHashedPassword().equals(newPassword))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("New password must be different from the old password.");
        user.setHashedPassword(newPassword);
        return ResponseEntity.status(200).body("User " + user.getNetId() + " has successfully changed password.");
    }

    @Operation(summary = "Delete user account", description = "Deletes a user account from the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @Transactional
    @DeleteMapping(path = "/{netId}")
    public ResponseEntity<String> deleteUserAccount(@Parameter(description = "NetID of the user to delete", required = true) @PathVariable String netId) {
        Optional<User> userOptional = userRepository.findUserByNetId(netId);

        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with NetID " + netId + " not found.");
        }

        User user = userOptional.get();

        // Delete the user; associated entities will be deleted due to cascade settings
        userRepository.delete(user);

        return ResponseEntity.status(HttpStatus.OK)
                .body("User with NetID " + netId + " has been deleted successfully.");
    }

    @Operation(summary = "Retrieve all students", description = "Fetches a list of all student users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all students retrieved")
    })
    @GetMapping("/allStudents")
    public ResponseEntity<List<User>> getAllStudents(){
        Optional<List<User>> allStudents = userRepository.findAllUserByUserType(UserType.STUDENT);
        if(allStudents.isEmpty()){
            return  ResponseEntity.internalServerError().build();
        }
        List<User> listOfAllStudent = allStudents.get();
        listOfAllStudent.sort(new Comparator<User>() {
            @Override
            public int compare(User user1, User user2) {
                int firstNameComparison = user1.getFirstName().compareToIgnoreCase(user2.getFirstName());
                if (firstNameComparison != 0) {
                    return firstNameComparison;
                } else{
                    return user1.getLastName().compareToIgnoreCase(user2.getLastName());
                }
            }
        });
        return ResponseEntity.ok(listOfAllStudent);
    }

    @Operation(summary = "Search user by name", description = "Searches for users by their first or last name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users matching the search criteria found"),
            @ApiResponse(responseCode = "404", description = "No users found matching the search criteria")
    })
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUserByName(@Parameter(description = "Name to search by", required = true) @RequestParam String name) {
        List<User> users = userRepository.findByFirstNameOrLastNameIgnoreCase(name);
        if(users.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(users);
    }

    @PutMapping("/forgotPassword")
    public ResponseEntity<String> changePassword(
            @RequestParam String netId,
            @RequestParam String newHashPassword
    ){
        Optional<User> curUser = userRepository.findUserByNetId(netId);
        if(curUser.isEmpty()){
            return ResponseEntity.internalServerError().build();
        }
        User user = curUser.get();

        user.setHashedPassword(newHashPassword);
        return ResponseEntity.ok("Updating password successfully");
    }
}


