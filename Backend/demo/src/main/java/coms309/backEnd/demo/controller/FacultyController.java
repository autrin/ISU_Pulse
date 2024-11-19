package coms309.backEnd.demo.controller;

import coms309.backEnd.demo.entity.Faculty;
import coms309.backEnd.demo.entity.Schedule;
import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.repository.FacultyRepository;
import coms309.backEnd.demo.repository.TeachRepository;
import coms309.backEnd.demo.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/faculty")
public class FacultyController {
    @Autowired
    private TeachRepository teachRepository;
    @Autowired
    private UserRepository userRepository;

    public FacultyController(TeachRepository teachRepository, UserRepository userRepository) {
        this.teachRepository = teachRepository;
        this.userRepository = userRepository;
    }
    /**
     * Retrieves the schedules associated with a faculty member identified by their NetID.
     *
     * @param netId The NetID of the faculty member.
     * @return A list of schedules taught by the faculty member.
     */
    @Operation(summary = "Get Faculty Schedules", description = "Retrieve schedules associated with a faculty member identified by their NetID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Schedules retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Schedule.class))),
            @ApiResponse(responseCode = "404", description = "User not found or not a faculty member",
                    content = @Content)
    })
    @GetMapping("/schedules/{netId}")
    public ResponseEntity<List<Schedule>> getFacultySchedules(
            @Parameter(description = "The NetID of the faculty member", required = true)
            @PathVariable String netId) {
        Optional<User> userOptional = userRepository.findUserByNetId(netId);
        if (!userOptional.isPresent())
            throw new IllegalStateException("User does not exist.");

        User user = userOptional.get();

        if (user.getFaculty() == null) {
            throw new IllegalStateException("This user is not a faculty.");
        }

        Faculty faculty = user.getFaculty();
        long facultyId = faculty.getId();
        List<Schedule> schedules = teachRepository.findSchedulesByFacultyId(facultyId);
        return ResponseEntity.ok(schedules);
    }
}
