package coms309.backEnd.demo.controller;

import coms309.backEnd.demo.entity.Faculty;
import coms309.backEnd.demo.entity.Profile;
import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.entity.UserType;
import coms309.backEnd.demo.repository.ProfileRepository;
import coms309.backEnd.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        Profile profile = new Profile();
        profile.setLinkedinUrl("No LinkedIn is set.");
        profile.setExternalUrl("No url is set.");
        user.setProfile(profile);
        profile.setUser(user);

        // Handle Teacher Sign Up
//        if (user.getUserType().equals("FACULTY"))
//            Faculty faculty = new Faculty();

        // Save user (will cascade and save profile as well)
        userRepository.save(user);

        return ResponseEntity.status(200).body("User is successfully registered.");
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
}
