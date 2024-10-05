package coms309.backEnd.demo.controller;

import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.entity.UserType;
import coms309.backEnd.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/{netId}")
    public ResponseEntity<User> getUserByNetId(@PathVariable String netId) {
        Optional<User> userOptional = userRepository.findById(netId);

        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get());
        } else {
            throw new IllegalArgumentException("NetId doesn't exist.");
        }
    }


//    @PostMapping("/addStudent")
//    public void addStudent(@RequestBody User studentDetails) {
//        if (userRepository.findByNetId(studentDetails.getNetId()) != null) {
//            throw new IllegalArgumentException("NetId already exists");
//        }
//
//        User newStudent = new User(
//                studentDetails.getNetId(),
//                studentDetails.getFirstName(),
//                studentDetails.getLastName(),
//                studentDetails.getEmail(),
//                studentDetails.getHashedPassword(),
//                studentDetails.getProfilePictureUrl(),
//                UserType.STUDENT
//        );
//
//        userRepository.save(newStudent);
//    }
}
