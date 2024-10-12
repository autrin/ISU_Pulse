package coms309.backEnd.demo.controller;

import coms309.backEnd.demo.entity.Profile;
import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.entity.UserType;
import coms309.backEnd.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/users")
@Slf4j // Lombok annotation for logging
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

//    @PostMapping
//    public void registerNewStudent(@RequestBody User user) {
//        Optional<User> userOptional = userRepository.findById(user.getNetId());
//        if (userOptional.isPresent()) {
//            throw new IllegalStateException("Student with NetId already exists.");
//        }
//
//        userRepository.save(user);
//    }

    @PostMapping
    public ResponseEntity<Map<String, String>> registerNewStudent(@RequestBody User user) {
        Optional<User> userOptional = userRepository.findById(user.getNetId());
        Map<String, String> response = new HashMap<>();
        if (userOptional.isPresent()) {
            response.put("message", "Student with NetId already exists");
            return ResponseEntity.badRequest().body(response);
        }

        Profile profile = new Profile();
        profile.setUser(user);

        // Cascade will automatically save profile
        user.setProfile(profile);
        userRepository.save(user);
        response.put("message", "Successfully registered new user.");
        return ResponseEntity.ok(response);
    }

    @Transactional
    @PutMapping(path = "updatepw/{netId}")
    public ResponseEntity<String> updateUserAccount(@PathVariable String netId, @RequestParam(required = true) String newPassword) {
        Optional<User> userOptional = userRepository.findById(netId);
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist");
        }

        User user = userOptional.get();
        if (user.getHashedPassword().equals(newPassword))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("New password must be different from the old password");

        user.setHashedPassword(newPassword);
        return ResponseEntity.status(HttpStatus.OK).body("Password updated successfully");
    }

//    @Transactional
//    @PutMapping(path = "updatepw/{netId}")
//    public ResponseEntity<String> updateUserAccount(@PathVariable String netId,
//                                  @RequestParam(required = true) String newPassword) {
//        Optional<User> userOptional = userRepository.findById(netId);
//        if (!userOptional.isPresent()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist");
//        }
//
//        User user = userOptional.get();
//        if (user.getHashedPassword().equals(newPassword))
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("New password must be different from the old password");
//
//        user.setHashedPassword(newPassword);
//        return ResponseEntity.ok("User " + netId + " has successfully changed the password.");
//    }
}
