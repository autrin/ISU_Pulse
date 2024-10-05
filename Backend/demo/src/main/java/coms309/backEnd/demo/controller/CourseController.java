package coms309.backEnd.demo.controller;

import coms309.backEnd.demo.entity.Course;
import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/course")
public class CourseController {


//    @Autowired
//    private final CourseRepository courseRepository;
//
//    public CourseController(CourseRepository courseRepository) {
//        this.courseRepository = courseRepository;
//    }
//
//    @PostMapping("/addCourse")
//    public void addCourse(@RequestBody Course courseDetails){
//        if (courseRepository.findByCode(courseDetails.getCode()) != null) {
//            throw new IllegalArgumentException("Course already Exist");
//        }
//
//
//    }
}
