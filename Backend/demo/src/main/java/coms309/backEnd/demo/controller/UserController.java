package coms309.backEnd.demo.controller;

import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.entity.UserType;
import coms309.backEnd.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @PostMapping("/addStudent")
    public void addStudent(@RequestBody User studentDetails) {
        if (userRepository.findByNetId(studentDetails.getNetId()) != null) {
            throw new IllegalArgumentException("NetId already exists");
        }

        User newStudent = new User(
                studentDetails.getNetId(),
                studentDetails.getFirstName(),
                studentDetails.getLastName(),
                studentDetails.getEmail(),
                studentDetails.getHashedPassword(),
                studentDetails.getProfilePictureUrl(),
                UserType.STUDENT
        );

        userRepository.save(newStudent);
    }







}
