package coms309.backEnd.demo.controller;

import coms309.backEnd.demo.entity.Course;
import coms309.backEnd.demo.entity.Enroll;
import coms309.backEnd.demo.entity.Schedule;
import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.repository.ScheduleRepository;
import coms309.backEnd.demo.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private final ScheduleRepository scheduleRepository;

    @Autowired
    private final UserRepository userRepository;

    public ScheduleController(ScheduleRepository scheduleRepository, UserRepository userRepository) {
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
    }
    // helper method

    /**
     * This functions takes the list of enrollList and return the list of schedule
     * @param enrollList the list of enrollList that one user takes
     * @return scheduleList the list of schedule(including courses and recurring times of the specific section)
     */
    private List<Schedule> getScheduleList(List<Enroll> enrollList){
        List<Schedule> scheduleList = new ArrayList<>();
        for(Enroll enroll : enrollList){
            scheduleList.add(enroll.getSchedule());
        }
        return scheduleList;
    }

    @Operation(summary = "Find courses in mutual between 2 users by NetID", description = "Retrieve courses in mutual using the NetIDs of 2 users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course in mutual found successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Course.class))),
            @ApiResponse(responseCode = "404", description = "Course in mutual not found ",
                    content = @Content)
    })
    @GetMapping("/coursesInMutual")
    public ResponseEntity<List<Course>> getSameSchedule(
            @Parameter(description = "The NetId of user 1") @RequestParam String user1NetId,
            @Parameter(description = "The NetId of user 2") @RequestParam String user2NetId){
        // Check if user 1 exists or not
        Optional<User> curUser1 = userRepository.findUserByNetId(user1NetId);
        if(curUser1.isEmpty()){
            return  ResponseEntity.status(404).body(null);
        }
        User user1 = curUser1.get();

        //Check if user 2 exists or not
        Optional<User> curUser2 = userRepository.findUserByNetId(user2NetId);
        if(curUser2.isEmpty()){
            return  ResponseEntity.status(404).body(null);
        }
        User user2 = curUser2.get();

        // Get the scheduleList for user 1
        List<Enroll> enrollList1 = user1.getEnrollList();
        List<Schedule> scheduleList1 = getScheduleList(enrollList1);

        // Get the ScheduleList for user 2
        List<Enroll> enrollList2 = user2.getEnrollList();
        List<Schedule> scheduleList2 = getScheduleList(enrollList2);

        //Compare to 2 list of schedule to take the similar schedule
        List<Course> coursesInMutual = new ArrayList<>();
        for(Schedule schedule1 : scheduleList1){
            boolean sameCourse = false;
            for(Schedule schedule2 : scheduleList2){
                //System.out.println("schedule1(id): " + schedule1.getId() + "// schedule2(id): " + schedule2.getId());
                if (schedule1.getCourse().getId() == schedule2.getCourse().getId()) {
                    sameCourse = true;
                    //System.out.println(sameSchedule);
                    break;
                }
            }
            if(sameCourse){
                coursesInMutual.add(schedule1.getCourse());
            }
        }
        return ResponseEntity.ok(coursesInMutual);

    }
}
