package coms309.backEnd.demo.controller;


import coms309.backEnd.demo.entity.Course;
import coms309.backEnd.demo.entity.Enroll;
import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.entity.UserType;
import coms309.backEnd.demo.repository.CourseRepository;
import coms309.backEnd.demo.repository.EnrollRepository;
import coms309.backEnd.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/addEnroll")
    public ResponseEntity<String> addEnroll(@RequestParam("netId") String netId, @RequestParam("course") int courseId) {
        Optional<User> curUser = userRepository.findById(netId);
        if (curUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User not found.");
        }

        Optional<Course> curCourse = courseRepository.findById(courseId);
        if (curCourse.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Course not found.");
        }

        Enroll curEnroll = new Enroll(curUser.get().getNetId(), curCourse.get().getCId(), curCourse.get().getNumSections(), curUser.get(), curCourse.get());
        enrollRepository.save(curEnroll);

        return ResponseEntity.ok("Enrollment successful!");
    }

}
