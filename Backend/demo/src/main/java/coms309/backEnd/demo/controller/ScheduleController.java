package coms309.backEnd.demo.controller;

import coms309.backEnd.demo.entity.Enroll;
import coms309.backEnd.demo.entity.Schedule;
import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.repository.ScheduleRepository;
import coms309.backEnd.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * This controller is for admin to add new schedule(course and section)
 */
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
    private List<Schedule> getScheduleList(List<Enroll> enrollList){
        List<Schedule> scheduleList = new ArrayList<>();
        for(Enroll enroll : enrollList){
            scheduleList.add(enroll.getSchedule());
        }
        return scheduleList;
    }

//    @GetMapping("/getSchedule/{netId}")
//    public ResponseEntity<List<Schedule>> getSchedule(@PathVariable String netId){
//        Optional<User> curUser = userRepository.findUserByNetId(netId);
//        if(curUser.isEmpty()){
//            return  ResponseEntity.internalServerError().build();
//        }
//        User user1 = curUser.get();
//        List<Enroll> enrollList = user1.getEnrollList();
//        List<Schedule> scheduleList = getScheduleList(enrollList);
//        return ResponseEntity.ok(scheduleList);
//    }

    @GetMapping("/SameSchedule")
    public ResponseEntity<List<Schedule>> getSameSchedule(@RequestParam String user1NetId, String user2NetId ){
        // Check if user 1 exists or not
        Optional<User> curUser1 = userRepository.findUserByNetId(user1NetId);
        if(curUser1.isEmpty()){
            return  ResponseEntity.internalServerError().build();
        }
        User user1 = curUser1.get();

        //Check if user 2 exists or not
        Optional<User> curUser2 = userRepository.findUserByNetId(user2NetId);
        if(curUser2.isEmpty()){
            return  ResponseEntity.internalServerError().build();
        }
        User user2 = curUser2.get();

        // Get the scheduleList for user 1
        List<Enroll> enrollList1 = user1.getEnrollList();
        List<Schedule> scheduleList1 = getScheduleList(enrollList1);

        // Get the ScheduleList for user 2
        List<Enroll> enrollList2 = user2.getEnrollList();
        List<Schedule> scheduleList2 = getScheduleList(enrollList2);

        //Compare to 2 list of schedule to take the similar schedule
        List<Schedule> sameSchedules = new ArrayList<>();
        for(Schedule schedule1 : scheduleList1){
            boolean sameSchedule = false;
            for(Schedule schedule2 : scheduleList2){
                //System.out.println("schedule1(id): " + schedule1.getId() + "// schedule2(id): " + schedule2.getId());
                if (schedule1.getId() == schedule2.getId()) {
                    sameSchedule = true;
                    //System.out.println(sameSchedule);
                    break;
                }
            }
            if(sameSchedule){
                sameSchedules.add(schedule1);
            }
        }
        return ResponseEntity.ok(sameSchedules);

    }
}
