package coms309.backEnd.demo.controller;

import coms309.backEnd.demo.entity.*;
import coms309.backEnd.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/getTaskByUserIn2days/{id}")
    public ResponseEntity<List<Task>> getTaskByCourse(@PathVariable Long id){
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, 2);
        Date tomorrowDate = calendar.getTime();

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

    @GetMapping("/scheduleTask/{scheduleId}")
    public ResponseEntity<List<Task>> fetchTasksBySchedule(@PathVariable long scheduleId) {
        List<Task> tasksOfSchedule = taskRepository.findAllByScheduleIdOrderByDueDateDesc(scheduleId);
        return ResponseEntity.ok(tasksOfSchedule);
    }

    @PostMapping("/scheduleTask/{scheduleId}")
    public ResponseEntity<Task> createScheduleTask(@PathVariable long scheduleId, @RequestBody Task task) {
        // Find the schedule by ID
        Optional<Schedule> scheduleOptional = scheduleRepository.findById(scheduleId);

        if (!scheduleOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Assign the found schedule to the task and save the task
        Schedule schedule = scheduleOptional.get();
        task.setSchedule(schedule);
        taskRepository.save(task);

        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PutMapping("/scheduleTask/{scheduleId}/task/{taskId}")
    public ResponseEntity<Task> updateTask(
            @PathVariable long scheduleId,
            @PathVariable long taskId,
            @RequestBody Task updatedTask) {

        // Find the schedule by ID
        Optional<Schedule> scheduleOptional = scheduleRepository.findById(scheduleId);
        if (!scheduleOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Find the task by ID and ensure it belongs to the given schedule
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (!taskOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Task existingTask = taskOptional.get();

        // Check if the task's schedule matches the provided schedule
        if (existingTask.getSchedule().getId() != scheduleId) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }

        // Update only the fields that are provided in the request
        if (updatedTask.getTitle() != null) {
            existingTask.setTitle(updatedTask.getTitle());
        }
        if (updatedTask.getDescription() != null) {
            existingTask.setDescription(updatedTask.getDescription());
        }
        if (updatedTask.getDueDate() != null) {
            existingTask.setDueDate(updatedTask.getDueDate());
        }
        if (updatedTask.getTaskType() != null) {
            existingTask.setTaskType(updatedTask.getTaskType());
        }

        // Save the updated task
        taskRepository.save(existingTask);

        return ResponseEntity.status(HttpStatus.OK).body(existingTask);
    }
}