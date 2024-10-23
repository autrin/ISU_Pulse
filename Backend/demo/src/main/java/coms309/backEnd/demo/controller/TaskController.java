package coms309.backEnd.demo.controller;

import coms309.backEnd.demo.entity.*;
import coms309.backEnd.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Calendar;
import java.util.ArrayList;
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

    @Autowired
    private final ScheduleRepository scheduleRepository;

    public TaskController(UserRepository userRepository, TaskRepository taskRepository, CourseRepository courseRepository, EnrollRepository enrollRepository, ScheduleRepository scheduleRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.courseRepository = courseRepository;
        this.enrollRepository = enrollRepository;
        this.scheduleRepository = scheduleRepository;
    }

//    @GetMapping("/getTasksByCourse")
//    public ResponseEntity<List<Task>> getTaskByCourse(@PathVariable int cId ){
//        Optional<Course> curCourse = courseRepository.findById(cId);
//        if(curCourse.isEmpty()){
//            return  ResponseEntity.internalServerError().build();
//        }
//
//        List<Task> tasklist = taskRepository.findAllByCourse(curCourse.get());
//        return ResponseEntity.ok(tasklist);
//    }
//
//    @GetMapping("/getTaskByUserIn2days/{sId}")
//    public ResponseEntity<List<Task>> getTaskByCourse(@PathVariable String sId){
//        //
//        Date currentDate = new Date();
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(currentDate);
//        calendar.add(Calendar.DATE, 2);
//        Date tomorrowDate = calendar.getTime();
//        //
//        Optional<User> curUser = userRepository.findById(sId);
//        if(curUser.isEmpty()){
//            return  ResponseEntity.internalServerError().build();
//        }
//        List<Enroll> curEnroll = enrollRepository.findAllByStudent(curUser.get());
//        List<Task> taskin2days = new ArrayList<>();
//        for(Enroll en : curEnroll){
//            Course cou = en.getCourse();
//            List<Task> listTaskOfOneCourse = taskRepository.findAllByCourse(cou);
//            for(Task task : listTaskOfOneCourse){
//                if (task.getDueDate() != null &&
//                        !task.getDueDate().before(currentDate) &&
//                        !task.getDueDate().after(tomorrowDate)) {
//                    taskin2days.add(task);
//                }
//            }
//        }
//        return ResponseEntity.ok(taskin2days);
//    }

    @GetMapping("/getTaskByUserIn2days/{id}")
    public ResponseEntity<List<Task>> getTaskByCourse(@PathVariable Long id){
        //
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, 2);
        Date tomorrowDate = calendar.getTime();
        //
        Optional<User> curUser = userRepository.findById(id);
        if(curUser.isEmpty()){
            return  ResponseEntity.internalServerError().build();
        }
        User user = curUser.get();
        List<Enroll> curEnroll = user.getEnrollList();
        List<Task> taskList = new ArrayList<>();
        for(Enroll enroll : curEnroll){
            Schedule schedule = enroll.getSchedule();
            List<Task> tasks = taskRepository.findAllBySchedule(schedule);
            for(Task task : tasks){
                if (task.getDueDate() != null &&
                        !task.getDueDate().before(currentDate) &&
                        !task.getDueDate().after(tomorrowDate)) {
                taskList.add(task);
                }
            }
        }
        return ResponseEntity.ok(taskList);



    }


}