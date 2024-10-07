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

    @PostMapping
    public void registerNewStudent(@RequestBody User user) {
        Optional<User> userOptional = userRepository.findById(user.getNetId());
        if (userOptional.isPresent()) {
            throw new IllegalStateException("Student with NetId already exists.");
        }

        userRepository.save(user);
    }
}
