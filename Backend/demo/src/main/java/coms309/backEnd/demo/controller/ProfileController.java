package coms309.backEnd.demo.controller;


import coms309.backEnd.demo.entity.Profile;
import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.repository.ProfileRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private final ProfileRepository profileRepository;

    public ProfileController(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @GetMapping("/{netId}")
    public ResponseEntity<Map<String, Object>> getProfileByNetId(@PathVariable String netId) {

        Optional<Profile> optionalProfile = profileRepository.findProfileByNetId(netId);

        if (!optionalProfile.isPresent())
            throw new IllegalStateException("Profile doesn't exist.");

        Profile profile = optionalProfile.get();
        User user = profile.getUser();
        String profilePictureUrl = user.getProfilePictureUrl();
        String firstName = user.getFirstName();
        String lastName = user.getLastName();

        Map<String, Object> response = new HashMap<>();
        response.put("profile", profile);
        response.put("firstName", firstName);
        response.put("lastName", lastName);
        response.put("profilePictureUrl", profilePictureUrl);

        return ResponseEntity.ok(response);
    }

    @Transactional
    @PutMapping("/{netId}")
    public ResponseEntity<String> updateProfile(@PathVariable String netId,
                                                @RequestParam(required = false) String linkedinUrl,
                                                @RequestParam(required = false) String externalUrl,
                                                @RequestParam(required = false) String description) {
        Optional<Profile> optionalProfile = profileRepository.findProfileByNetId(netId);
        if (!optionalProfile.isPresent())
            throw new IllegalStateException("Profile doesn't exist.");
        Profile profile = optionalProfile.get();

        if(description != null)
            profile.setDescription(description);

        if (externalUrl != null)
            profile.setExternalUrl(externalUrl);

        if (linkedinUrl != null)
            profile.setLinkedinUrl(linkedinUrl);
        return ResponseEntity.ok("Update profile successfully");
    }
}
