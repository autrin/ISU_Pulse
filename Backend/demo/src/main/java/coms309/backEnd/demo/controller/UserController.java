package coms309.backEnd.demo.controller;

import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.entity.UserType;
import coms309.backEnd.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
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
    public ResponseEntity<Map<String, String>> registerNewStudent(@RequestBody User user) {
        Optional<User> userOptional = userRepository.findById(user.getNetId());
        Map<String, String> response = new HashMap<>();
        if (userOptional.isPresent()) {
            response.put("message", "Student with NetId already exists");
            return ResponseEntity.badRequest().body(response);
        }

        userRepository.save(user);
        response.put("message", "Successfully registered new user.");
        return ResponseEntity.ok(response);
    }

}
