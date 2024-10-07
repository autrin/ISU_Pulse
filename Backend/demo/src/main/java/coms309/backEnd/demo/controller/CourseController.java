package coms309.backEnd.demo.controller;

import coms309.backEnd.demo.entity.Course;
import coms309.backEnd.demo.entity.Department;
import coms309.backEnd.demo.entity.Enroll;
import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.repository.CourseRepository;
import coms309.backEnd.demo.repository.DepartmentRepository;
import coms309.backEnd.demo.repository.EnrollRepository;
import coms309.backEnd.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/course")
public class CourseController {

    @Autowired
    private final DepartmentRepository departmentRepository;


    @Autowired
    private final CourseRepository courseRepository;

    @Autowired
    private final EnrollRepository enrollRepository;

    @Autowired
    private final UserRepository userRepository;

    public CourseController(DepartmentRepository departmentRepository, CourseRepository courseRepository, EnrollRepository enrollRepository, UserRepository userRepository) {
        this.departmentRepository = departmentRepository;
        this.courseRepository = courseRepository;
        this.enrollRepository = enrollRepository;
        this.userRepository = userRepository;
    }

//    @PostMapping("/addCourse")
//    public void addCourse(@RequestBody Course courseDetails){
//        if (courseRepository.findByCode(courseDetails.getCode()) != null) {
//            throw new IllegalArgumentException("Course already Exist");
//        }

//    @GetMapping("/{sId}")
//    public ResponseEntity<Course[]> getCourseByStudentId(@PathVariable String sId) throws Exception {
//        Optional<User> curUser = userRepository.findById(sId);
//        if (curUser.isEmpty())  {
//            throw new Exception();
//        }
//        Course[] courses = enrollRepository.findCoursesBysId(curUser.get());
//        return ResponseEntity.ok(courses);
//    }

    @GetMapping("/getCourseList/{sId}")
    public ResponseEntity<List<Enroll>> getCourseByStudentId(@PathVariable String sId){
        Optional<User> curUser = userRepository.findById(sId);
        if(curUser.isEmpty()){
            return  ResponseEntity.internalServerError().build();
        }
        List<Enroll> enrolls = enrollRepository.findAllBysId(sId);
//        List<String> codeList = new ArrayList<>();
//        for (Enroll en : enrolls){
//            String code = en.getCourse().getCode();
//            codeList.add(code);
//        }
        return ResponseEntity.ok(enrolls);
    }

    @PostMapping("/addCourses")
    public void addCourse(@RequestBody Course course, @RequestParam String depName){
        Department dep = departmentRepository.findDepartmentByName(depName);
        course.setDepartment(dep);
        courseRepository.save(course);
    }

}
