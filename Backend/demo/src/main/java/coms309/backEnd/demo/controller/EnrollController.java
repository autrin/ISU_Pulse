package coms309.backEnd.demo.controller;


import coms309.backEnd.demo.entity.Course;
import coms309.backEnd.demo.entity.Enroll;
import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.repository.CourseRepository;
import coms309.backEnd.demo.repository.EnrollRepository;
import coms309.backEnd.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/enroll")
public class EnrollController {

    @Autowired
    private final EnrollRepository enrollRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final CourseRepository courseRepository;

    public EnrollController(EnrollRepository enrollRepository, UserRepository userRepository, CourseRepository courseRepository) {
        this.enrollRepository = enrollRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }
    @GetMapping("/getEnroll/{sId}")
    public ResponseEntity<List<Course>> addEnroll(@PathVariable String sId){
        Optional<User> curUser = userRepository.findById(sId);
        if (curUser.isEmpty()) {
            return  ResponseEntity.internalServerError().build();
        }
        User user = curUser.get();
        List<Enroll> enrollList = enrollRepository.findAllByStudentid(sId);
        List<Course> courseList = new ArrayList<>();
        for(Enroll en : enrollList){
            courseList.add(en.getCourse());
        }
        return ResponseEntity.ok(courseList);
    }

    @PostMapping("/addEnroll/{sId}")
    public ResponseEntity<String> addEnroll(@PathVariable String sId, @RequestParam("course") int courseId) {
        Optional<User> curUser = userRepository.findById(sId);
        if (curUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User not found.");
        }
        Optional<Course> curCourse = courseRepository.findById(courseId);
        if (curCourse.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Course not found.");
        }
        Enroll curEnroll = new Enroll(curUser.get().getNetId(), curCourse.get().getCId(),
                curCourse.get().getNumSections(), curUser.get(), curCourse.get());

        enrollRepository.save(curEnroll);

        return ResponseEntity.ok("Enrollment successful.");
    }


    @DeleteMapping("/removeEnroll/{sId}")
    public ResponseEntity<String> removeEnroll(@PathVariable String sId, @RequestParam("c_id") int courseId) {
        Optional<User> curUser = userRepository.findById(sId);
        if (curUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User not found.");
        }
        Optional<Course> curCourse = courseRepository.findById(courseId);
        if (curCourse.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Course not found.");
        }

        Enroll enroll = enrollRepository.findByStudentidAndCourseid(sId, courseId);
        enrollRepository.delete(enroll);
        return ResponseEntity.ok("Successfully removed enrollment");
    }
}
