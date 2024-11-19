package coms309.backEnd.demo.controller;


import coms309.backEnd.demo.entity.Course;
import coms309.backEnd.demo.entity.Enroll;
import coms309.backEnd.demo.entity.Schedule;
import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.repository.CourseRepository;
import coms309.backEnd.demo.repository.EnrollRepository;
import coms309.backEnd.demo.repository.ScheduleRepository;
import coms309.backEnd.demo.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This controller is for student to get the schedule(course and section) that they currently enroll. They can also enroll to schedule(course and section)
 * or drop schedule(course and section)
 */
@RestController
@RequestMapping("/enroll")
public class EnrollController {
    @Autowired
    private final EnrollRepository enrollRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ScheduleRepository scheduleRepository;

    public EnrollController(EnrollRepository enrollRepository, UserRepository userRepository, ScheduleRepository scheduleRepository) {
        this.enrollRepository = enrollRepository;
        this.userRepository = userRepository;
        this.scheduleRepository = scheduleRepository;
    }


    /**
     * Get the schedules a user is currently enrolled in.
     *
     * @param netId The NetID of the user.
     * @return A list of schedules the user is enrolled in.
     */
    @Operation(summary = "Fetch enrolled schedules", description = "Retrieve all schedules (courses and sections) that the specified user is currently enrolled in.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enrolled schedules retrieved successfully", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "User not found")
    })
    @GetMapping("/getEnroll/{netId}")
    public ResponseEntity<List<Schedule>> getEnroll(@Parameter(description = "NetID of the user", required = true) @PathVariable String netId){
        Optional<User> curUser = userRepository.findUserByNetId(netId);
        // check if user exists or not
        if(curUser.isEmpty()){
            return  ResponseEntity.internalServerError().build();
        }
        User user = curUser.get();
        List<Enroll> enrollList = user.getEnrollList();
        List<Schedule> scheduleList = new ArrayList<>();
        for(Enroll enroll : enrollList){
            Schedule schedule = enroll.getSchedule();
            scheduleList.add(schedule);
        }
        return ResponseEntity.ok(scheduleList);
    }

    /**
     * Get all users enrolled in a specific schedule.
     *
     * @param scheduleId The ID of the schedule.
     * @return A list of users enrolled in the schedule.
     */
    @Operation(summary = "Fetch users in a schedule", description = "Retrieve all users (students) enrolled in the specified schedule.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/getPeople/{scheduleId}")
    public ResponseEntity<List<User>> fetchStudents(@Parameter(description = "ID of the schedule", required = true) @PathVariable long scheduleId) {
        List<User> people = enrollRepository.findStudentsBySchedule(scheduleId);
        return ResponseEntity.ok(people);
    }

    /**
     * Enroll a user in a specific schedule.
     *
     * @param netId      The NetID of the user.
     * @param scheduleId The ID of the schedule.
     * @return A success or error message.
     */
    @Operation(summary = "Enroll in a schedule", description = "Enroll the specified user in the given schedule.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enrollment added successfully", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User or schedule not found")
    })
    @PostMapping("/addEnroll/{netId}")
    public ResponseEntity<String> addEnroll(
            @Parameter(description = "NetID of the user", required = true)
            @PathVariable String netId,
            @Parameter(description = "ID of the schedule to enroll in", required = true)
            @RequestParam long scheduleId) {
        // check if user exists or not
        Optional<User> curUser = userRepository.findUserByNetId(netId);
        if (curUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        User user = curUser.get();
        // check if the schedule exists or not
        Optional<Schedule> curSchedule = scheduleRepository.findById(scheduleId);
        if (curSchedule.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Schedule not found.");
        }
        Schedule schedule = curSchedule.get();
        Enroll newEnroll = new Enroll(user, schedule);
        enrollRepository.save(newEnroll);
        return ResponseEntity.ok("Add enrollment successfully");
    }

    /**
     * Delete a user's enrollment from a specific schedule.
     *
     * @param netId    The NetID of the user.
     * @param enrollId The ID of the enrollment to delete.
     * @return A success or error message.
     */
    @Operation(summary = "Delete an enrollment", description = "Delete the specified enrollment for the given user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enrollment deleted successfully", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User or enrollment not found"),
            @ApiResponse(responseCode = "403", description = "User not authorized to delete this enrollment")
    })
    @DeleteMapping("/deleteEnroll/{netId}")
    public ResponseEntity<String> deleteEnroll(
            @Parameter(description = "NetID of the user", required = true)
            @PathVariable String netId,
            @Parameter(description = "ID of the enrollment to delete", required = true)
            @RequestParam long enrollId
    ){
        // check if user exists or not
        Optional<User> curUser = userRepository.findUserByNetId(netId);
        if (curUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        User user = curUser.get();
        // check if the enrollment exists or not
        Optional<Enroll> curEnroll = enrollRepository.findById(enrollId);
        if(curEnroll.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Enrollment not found");
        }
        Enroll enroll = curEnroll.get();
        // check if the enrollment belongs to the right person
        if(!enroll.getStudent().getNetId().trim().equalsIgnoreCase(netId)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("You are not authorized to delete this enrollment");
        }
        enrollRepository.delete(enroll);
        return ResponseEntity.ok("Delete enrollment successfully");
    }
}
