package coms309.backEnd.demo.controller;

import coms309.backEnd.demo.DTO.FacultyDTO;
import coms309.backEnd.demo.entity.*;
import coms309.backEnd.demo.repository.ChatMessageRepository;
import coms309.backEnd.demo.repository.DepartmentRepository;
import coms309.backEnd.demo.repository.FacultyRepository;
import coms309.backEnd.demo.repository.UserRepository;
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

    @GetMapping("/{netId}")
    public ResponseEntity<User> getUserByNetId(@PathVariable String netId) {
        Optional<User> userOptional = userRepository.findUserByNetId(netId);
        if (!userOptional.isPresent())
            throw new IllegalStateException("User doesn't exist.");
        User user = userOptional.get();
        return ResponseEntity.status(200).body(user);
    }

    @PostMapping
    public ResponseEntity<String> registerNewStudent(@RequestBody User user){
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

    @PostMapping("/faculty")
    public ResponseEntity<Map<String, String>> signupFaculty(@RequestBody FacultyDTO facultyDTO) {
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

    @Transactional
    @PutMapping(path = "updatepw/{netId}")
    public ResponseEntity<String> updateUserPassword(@PathVariable String netId,
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

    @DeleteMapping(path = "/{netId}")
    @Transactional
    public ResponseEntity<String> deleteUserAccount(@PathVariable String netId) {
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

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUserByName(@RequestParam String name) {
        List<User> users = userRepository.findByFirstNameOrLastNameIgnoreCase(name);
        if(users.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(users);
    }

}


