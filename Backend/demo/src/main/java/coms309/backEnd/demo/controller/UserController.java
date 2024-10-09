package coms309.backEnd.demo.controller;

import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.entity.UserType;
import coms309.backEnd.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
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

    @Transactional
    @PutMapping(path = "updatepw/{netId}")
    public void updateUserAccount(@PathVariable String netId,
                                  @RequestParam(required = true) String newPassword) {
        Optional<User> userOptional = userRepository.findById(netId);
        if (!userOptional.isPresent()) {
            throw new IllegalStateException("User does not exist");
        }

        User user = userOptional.get();
        if (user.getHashedPassword() == newPassword)
            throw new IllegalStateException("New newPassword must be different from the old newPassword");
        
        user.setHashedPassword(newPassword);
    }

    @DeleteMapping(path = "/{netId}")
    public ResponseEntity<String> deleteUserAccount(@PathVariable String netId) {
        // Check if the user exists, if not throw an exception
        User user = userRepository.findById(netId)
                .orElseThrow(() -> new IllegalStateException("User does not exist"));

        // Proceed to delete the user
        userRepository.delete(user);

        // Return a response indicating success
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("User account deleted successfully.");
    }
}
