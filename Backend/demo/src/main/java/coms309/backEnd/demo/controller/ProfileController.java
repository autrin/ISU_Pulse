package coms309.backEnd.demo.controller;


import coms309.backEnd.demo.entity.Profile;
import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.repository.ProfileRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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


    /**
     * Retrieves a user's profile by their NetID.
     *
     * @param netId The NetID of the user.
     * @return A ResponseEntity containing the profile and user details.
     */
    @Operation(summary = "Retrieve profile by NetID",
            description = "Fetches the profile details of a user using their NetID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Profile retrieved successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404",
                    description = "Profile not found")
    })
    @GetMapping("/{netId}")
    public ResponseEntity<Map<String, Object>> getProfileByNetId(@Parameter(description = "NetID of the user whose profile is to be retrieved", required = true) @PathVariable String netId) {

        Optional<Profile> optionalProfile = profileRepository.findByUserNetId(netId);

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

    /**
     * Updates a user's profile details.
     *
     * @param netId The NetID of the user whose profile is being updated.
     * @param linkedinUrl The updated LinkedIn URL (optional).
     * @param externalUrl The updated external URL (optional).
     * @param description The updated description (optional).
     * @return A ResponseEntity with a success message.
     */
    @Operation(summary = "Update profile",
            description = "Updates the profile details of a user such as LinkedIn URL, external URL, or description.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Profile updated successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404",
                    description = "Profile not found")
    })
    @Transactional
    @PutMapping("/{netId}")
    public ResponseEntity<String> updateProfile(@Parameter(description = "NetID of the user whose profile is to be updated", required = true)
                                                    @PathVariable String netId,
                                                @Parameter(description = "Updated LinkedIn URL", required = false)
                                                    @RequestParam(required = false) String linkedinUrl,
                                                @Parameter(description = "Updated external URL", required = false)
                                                    @RequestParam(required = false) String externalUrl,
                                                @Parameter(description = "Updated description", required = false)
                                                    @RequestParam(required = false) String description) {
        Optional<Profile> optionalProfile = profileRepository.findByUserNetId(netId);
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
