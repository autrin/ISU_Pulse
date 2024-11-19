package coms309.backEnd.demo.controller;

import coms309.backEnd.demo.entity.*;
import coms309.backEnd.demo.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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


    /**
     * Get tasks due in the next 2 days for a user.
     *
     * @param netId The NetID of the user.
     * @return A list of tasks due in the next 2 days.
     */
    @Operation(summary = "Fetch tasks due in the next 2 days", description = "Retrieve all tasks due in the next 2 days for the specified user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "User not found")
    })
    @GetMapping("/getTaskByUserIn2days/{netId}")
    public ResponseEntity<List<Task>> getTaskByCourse(@Parameter(description = "NetID of the user", required = true) @PathVariable String netId){
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, 2);
        Date tomorrowDate = calendar.getTime();

        Optional<User> curUser = userRepository.findUserByNetId(netId);
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


    /**
     * Fetch upcoming tasks for a schedule.
     *
     * @param scheduleId The ID of the schedule.
     * @return A list of upcoming tasks for the schedule.
     */
    @Operation(summary = "Fetch upcoming tasks by schedule", description = "Retrieve all upcoming tasks for a specific schedule.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/scheduleTask/{scheduleId}")
    public ResponseEntity<List<Task>> fetchUpcomingTasksBySchedule(@Parameter(description = "ID of the schedule", required = true) @PathVariable long scheduleId) {
        List<Task> tasksOfSchedule = taskRepository.findUpcomingTasksByScheduleId(scheduleId);
        return ResponseEntity.ok(tasksOfSchedule);
    }

    /**
     * Create a task for a specific schedule.
     *
     * @param scheduleId The ID of the schedule.
     * @param task The task details.
     * @return The created task.
     */
    @Operation(summary = "Create a task for a schedule", description = "Add a new task to a specific schedule.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created successfully", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Schedule not found")
    })
    @PostMapping("/scheduleTask/{scheduleId}")
    public ResponseEntity<Task> createScheduleTask(@Parameter(description = "ID of the schedule", required = true) @PathVariable long scheduleId,
                                                   @Parameter(description = "Details of the task to be created", required = true) @RequestBody Task task) {
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

    /**
     * Update an existing task for a schedule.
     *
     * @param scheduleId The ID of the schedule.
     * @param taskId The ID of the task to update.
     * @param updatedTask The updated task details.
     * @return The updated task.
     */
    @Operation(summary = "Update a task for a schedule", description = "Modify the details of an existing task for a specific schedule.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated successfully", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Schedule or task not found"),
            @ApiResponse(responseCode = "400", description = "Task does not belong to the specified schedule")
    })
    @PutMapping("/scheduleTask/{scheduleId}/task/{taskId}")
    public ResponseEntity<Task> updateTask(
            @Parameter(description = "ID of the schedule", required = true)
            @PathVariable long scheduleId,
            @Parameter(description = "ID of the task to update", required = true)
            @PathVariable long taskId,
            @Parameter(description = "Updated task details", required = true)
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