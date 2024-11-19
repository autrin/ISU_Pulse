package coms309.backEnd.demo.controller;

import coms309.backEnd.demo.entity.Course;
import coms309.backEnd.demo.entity.Enroll;
import coms309.backEnd.demo.entity.PersonalTask;
import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.repository.CourseRepository;
import coms309.backEnd.demo.repository.EnrollRepository;
import coms309.backEnd.demo.repository.PersonalTaskRepository;
import coms309.backEnd.demo.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/personalTask")
public class PersonalTaskController {

    @Autowired
    private final PersonalTaskRepository personalTaskRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final CourseRepository courseRepository;

    @Autowired
    private final EnrollRepository enrollRepository;

    public PersonalTaskController(PersonalTaskRepository personalTaskRepository, UserRepository userRepository, CourseRepository courseRepository, EnrollRepository enrollRepository) {
        this.personalTaskRepository = personalTaskRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.enrollRepository = enrollRepository;
    }
    /**
     * Retrieves a list of personal tasks for a specific user identified by their NetID.
     *
     * @param netId The NetID of the user.
     * @return A list of personal tasks associated with the user.
     */
    @Operation(summary = "Get Personal Tasks for a User", description = "Retrieve all personal tasks associated with a user by their NetID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Personal tasks retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonalTask.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    @GetMapping("/getPersonalTasks/{netId}")
    public ResponseEntity<List<PersonalTask>> getListofPersonalTasks(
            @Parameter(description = "The NetID of the user", required = true) @PathVariable String netId){
        Optional<User> curUser = userRepository.findUserByNetId(netId);
        if(curUser.isEmpty()){
            return  ResponseEntity.internalServerError().build();
        }
        User user = curUser.get();
        List<PersonalTask> personalTasklist = personalTaskRepository.findAllByUser(user);
        return ResponseEntity.ok(personalTasklist);
    }


    /**
     * Adds a new personal task for a specific user identified by their NetID.
     *
     * @param netId            The NetID of the user.
     * @param title            The title of the personal task.
     * @param description      The description of the personal task.
     * @param dueDateTimestamp The due date of the task as a timestamp.
     * @return A confirmation message upon successful addition.
     */
    @Operation(summary = "Add a Personal Task", description = "Create a new personal task for a user identified by their NetID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Personal task added successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    @PostMapping("/addPersonalTask/{netId}")
    public ResponseEntity<String> addPersonTasks(
            @Parameter(description = "The NetID of the user", required = true)
            @PathVariable String netId,
            @Parameter(description = "The title of the personal task", required = true)
            @RequestParam String title,
            @Parameter(description = "The description of the personal task", required = true)
            @RequestParam String description,
            @Parameter(description = "The due date of the task as a timestamp", required = true)
            @RequestParam long dueDateTimestamp
    ){
        Optional<User> curUser = userRepository.findUserByNetId(netId);
        if(curUser.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        User user = curUser.get();
        PersonalTask personalTask = new PersonalTask(title, description, new Date(dueDateTimestamp), user);
        personalTaskRepository.save(personalTask);
        return ResponseEntity.ok("Personal task added successfully.");
    }





    /**
     * Updates an existing personal task for a specific user identified by their NetID.
     *
     * @param netId            The NetID of the user.
     * @param taskId           The ID of the task to be updated.
     * @param title            (Optional) The new title of the task.
     * @param description      (Optional) The new description of the task.
     * @param dueDateTimestamp (Optional) The new due date of the task as a timestamp.
     * @return A confirmation message upon successful update.
     */
    @Operation(summary = "Update a Personal Task", description = "Update details of an existing personal task for a user identified by their NetID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "User or Task not found",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden: Unauthorized to update this task",
                    content = @Content)
    })
    @PutMapping("/updatePersonalTask/{netId}")
    public ResponseEntity<String> updatePersonTasks(
            @Parameter(description = "The NetID of the user", required = true)
            @PathVariable String netId,
            @Parameter(description = "The ID of the task to update", required = true)
            @RequestParam long taskId,
            @Parameter(description = "The new title of the task", required = false)
            @RequestParam(required = false) String title,
            @Parameter(description = "The new description of the task", required = false)
            @RequestParam(required = false) String description,
            @Parameter(description = "The new due date of the task as a timestamp", required = false)
            @RequestParam(required = false) Long dueDateTimestamp
    ) {
        Optional<User> curUser = userRepository.findUserByNetId(netId);
        if (curUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with ID " + netId + " not found.");
        }

        User user = curUser.get();

        Optional<PersonalTask> optionalTask = personalTaskRepository.findById(taskId);
        if (optionalTask.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Task with ID " + taskId + " not found.");
        }

        PersonalTask task = optionalTask.get();

        if(!task.getUser().getNetId().trim().equalsIgnoreCase(netId.trim())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to update this task.");
        }

        if (title != null) {
            task.setTitle(title);
        }
        if (description != null) {
            task.setDescription(description);
        }
        if (dueDateTimestamp != null) {
            task.setDueDate(new Date(dueDateTimestamp));
        }

        personalTaskRepository.save(task);

        return ResponseEntity.ok("Task updated successfully.");

    }
    /**
     * Deletes an existing personal task for a specific user identified by their NetID.
     *
     * @param netId  The NetID of the user.
     * @param taskId The ID of the task to be deleted.
     * @return A confirmation message upon successful deletion.
     */
    @Operation(summary = "Delete a Personal Task", description = "Delete an existing personal task for a user identified by their NetID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task deleted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "User or Task not found",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden: Unauthorized to delete this task",
                    content = @Content)
    })
    @DeleteMapping("/deletePersonalTask/{netId}")
    public ResponseEntity<String> deletePersonalTasks(
            @Parameter(description = "The NetID of the user", required = true)
            @PathVariable String netId,
            @Parameter(description = "The ID of the task to delete", required = true)
            @RequestParam long taskId
    ){
        Optional<User> curUser = userRepository.findUserByNetId(netId);
        if (curUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with ID " + netId + " not found.");
        }

        User user = curUser.get();

        Optional<PersonalTask> optionalTask = personalTaskRepository.findById(taskId);
        if (optionalTask.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Task with ID " + taskId + " not found.");
        }

        PersonalTask task = optionalTask.get();

        if(!task.getUser().getNetId().trim().equalsIgnoreCase(netId.trim())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to update this task.");
        }
        personalTaskRepository.delete(task);
        return ResponseEntity.ok("Task deleted successfully.");

    }
}
