package coms309.backEnd.demo.controller;

import coms309.backEnd.demo.entity.Course;
import coms309.backEnd.demo.entity.Enroll;
import coms309.backEnd.demo.entity.Task;
import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.repository.CourseRepository;
import coms309.backEnd.demo.repository.EnrollRepository;
import coms309.backEnd.demo.repository.TaskRepository;
import coms309.backEnd.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/task")
public class TaskController {
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final TaskRepository taskRepository;

    @Autowired
    private final CourseRepository courseRepository;

    @Autowired
    private final EnrollRepository enrollRepository;

    public TaskController(UserRepository userRepository, TaskRepository taskRepository, CourseRepository courseRepository, EnrollRepository enrollRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.courseRepository = courseRepository;
        this.enrollRepository = enrollRepository;
    }

    @GetMapping("/getTasksByCourse")
    public ResponseEntity<List<Task>> getTaskByCourse(@PathVariable int cId ){
        Optional<Course> curCourse = courseRepository.findById(cId);
        if(curCourse.isEmpty()){
            return  ResponseEntity.internalServerError().build();
        }

        List<Task> tasklist = taskRepository.findAllByCourse(curCourse.get());
        return ResponseEntity.ok(tasklist);
    }

    @GetMapping("/getTaskByUserIn2days/{sId}")
    public ResponseEntity<List<Task>> getTaskByCourse(@PathVariable String sId){
        //
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, 2);
        Date tomorrowDate = calendar.getTime();
        //
        Optional<User> curUser = userRepository.findById(sId);
        if(curUser.isEmpty()){
            return  ResponseEntity.internalServerError().build();
        }
        List<Enroll> curEnroll = enrollRepository.findAllBysId(sId);
        List<Task> taskin2days = new ArrayList<>();
        for(Enroll en : curEnroll){
            Course cou = en.getCourse();
            List<Task> listTaskOfOneCourse = taskRepository.findAllByCourse(cou);
            for(Task task : listTaskOfOneCourse){
                if (task.getDueDate() != null &&
                        !task.getDueDate().before(currentDate) &&
                        !task.getDueDate().after(tomorrowDate)) {
                    taskin2days.add(task);
                }
            }
        }
        return ResponseEntity.ok(taskin2days);
    }


}