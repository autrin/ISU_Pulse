package coms309.backEnd.demo.controller;


import coms309.backEnd.demo.entity.Course;
import coms309.backEnd.demo.entity.Enroll;
import coms309.backEnd.demo.entity.Schedule;
import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.repository.CourseRepository;
import coms309.backEnd.demo.repository.EnrollRepository;
import coms309.backEnd.demo.repository.ScheduleRepository;
import coms309.backEnd.demo.repository.UserRepository;
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

    @GetMapping("/getEnroll/{id}")
    public ResponseEntity<List<Schedule>> getSchedule(@PathVariable long id){
        Optional<User> curUser = userRepository.findById(id);
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
    @PostMapping("/addEnroll/{id}")
    public ResponseEntity<String> addSchedule(
            @PathVariable long id,
            @RequestParam long scheduleId) {
        // check if user exists or not
        Optional<User> curUser = userRepository.findById(id);
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

    @DeleteMapping("/deleteEnroll/{id}")
    public ResponseEntity<String> deleteSchedule(
            @PathVariable long id,
            @RequestParam long enrollId
    ){
        // check if user exists or not
        Optional<User> curUser = userRepository.findById(id);
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
        if(enroll.getStudent().getId() != id){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("You are not authorized to delete this enrollment");
        }

        enrollRepository.delete(enroll);
        return ResponseEntity.ok("Delete enrollment successfully");
    }







//    @GetMapping("/getEnroll/{sId}")
//    public ResponseEntity<List<Course>> addEnroll(@PathVariable String sId){
//        Optional<User> curUser = userRepository.findById(sId);
//        if (curUser.isEmpty()) {
//            return  ResponseEntity.internalServerError().build();
//        }
//        User user = curUser.get();
//        List<Enroll> enrollList = enrollRepository.findAllByStudentid(sId);
//        List<Course> courseList = new ArrayList<>();
//        for(Enroll en : enrollList){
//            courseList.add(en.getCourse());
//        }
//        return ResponseEntity.ok(courseList);
//    }
//
//    @PostMapping("/addEnroll/{sId}")
//    public ResponseEntity<String> addEnroll(@PathVariable String sId, @RequestParam("course") int courseId) {
//        Optional<User> curUser = userRepository.findById(sId);
//        if (curUser.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User not found.");
//        }
//        Optional<Course> curCourse = courseRepository.findById(courseId);
//        if (curCourse.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Course not found.");
//        }
//        Enroll curEnroll = new Enroll(curUser.get().getNetId(), curCourse.get().getCId(),
//                curCourse.get().getNumSections(), curUser.get(), curCourse.get());
//
//        enrollRepository.save(curEnroll);
//
//        return ResponseEntity.ok("Enrollment successful.");
//    }
//
//
//    @DeleteMapping("/removeEnroll/{sId}")
//    public ResponseEntity<String> removeEnroll(@PathVariable String sId, @RequestParam("c_id") int courseId) {
//        Optional<User> curUser = userRepository.findById(sId);
//        if (curUser.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User not found.");
//        }
//        Optional<Course> curCourse = courseRepository.findById(courseId);
//        if (curCourse.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Course not found.");
//        }
//
//        Enroll enroll = enrollRepository.findByStudentidAndCourseid(sId, courseId);
//        enrollRepository.delete(enroll);
//        return ResponseEntity.ok("Successfully removed enrollment");
//    }
}
