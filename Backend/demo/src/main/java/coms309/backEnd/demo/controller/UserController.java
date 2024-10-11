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

import javax.swing.text.html.Option;
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
    public ResponseEntity<String> updateUserAccount(@PathVariable String netId,
                                  @RequestParam(required = true) String newPassword) {
        Optional<User> userOptional = userRepository.findById(netId);
        if (!userOptional.isPresent()) {
            throw new IllegalStateException("User does not exist");
        }

        User user = userOptional.get();
        if (user.getHashedPassword().equals(newPassword))
            throw new IllegalStateException("New newPassword must be different from the old newPassword");
        
        user.setHashedPassword(newPassword);
        return ResponseEntity.ok("User " + netId + " has successfully changed the password.");
    }

    @DeleteMapping(path = "/{netId}")
    public ResponseEntity<String> deleteUserAccount(@PathVariable String netId) {
        log.info("Attempting to delete user with NetId: {}", netId);
        User user = userRepository.findById(netId)
                .orElseThrow(() -> {
                    log.warn("User with NetId {} does not exist.", netId);
                    return new IllegalStateException("User does not exist");
                });

        // Proceed to delete the user (Enroll records will be deleted via cascade if configured)
        log.info("Fetch User's data: {}.", user.getEmail());
        userRepository.deleteById(netId);
        // Alternatively, you can use: userRepository.deleteById(netId);

        log.info("User with NetId {} deleted successfully.", netId);

        // Return a response indicating success
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("User account deleted successfully.");
    }
}
