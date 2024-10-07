package coms309.backEnd.demo.controller;

import coms309.backEnd.demo.entity.PersonalTask;
import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.repository.PersonalTaskRepository;
import coms309.backEnd.demo.repository.UserRepository;
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


    public PersonalTaskController(PersonalTaskRepository personalTaskRepository, UserRepository userRepository) {
        this.personalTaskRepository = personalTaskRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/getPersonalTasks/{sId}")
    public ResponseEntity<List<PersonalTask>> getListofPersonalTasks(@PathVariable String sId){
        Optional<User> curUser = userRepository.findById(sId);
        if(curUser.isEmpty()){
            return  ResponseEntity.internalServerError().build();
        }
        List<PersonalTask> personalTasklist = personalTaskRepository.findAllByUser(curUser.get());
        return ResponseEntity.ok(personalTasklist);
    }

    @PostMapping("/addPersonalTask/{sId}")
    public ResponseEntity<Boolean> addPersonTasks(
            @PathVariable String sId,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam long dueDateTimestamp
    ) {
        Optional<User> curUser = userRepository.findById(sId);
        if (curUser.isEmpty()){
            return ResponseEntity.internalServerError().build();
        }
        personalTaskRepository.save(new PersonalTask(
            title, new Date(dueDateTimestamp), description, curUser.get()
        ));
        return ResponseEntity.ok(true);
    }

//    @PutMapping("/updatePersonalTask/{sId}")
//    public ResponseEntity<String> updatePersonTasks(
//            @PathVariable String sId,
//            @RequestParam int taskId,
//            @RequestParam(required = false) String title,
//            @RequestParam(required = false) String description,
//            @RequestParam(required = false) Long dueDateTimestamp
//    ){
//        // It will check if user exists or not
//        Optional<User> curUser = userRepository.findById(sId);
//        if (curUser.isEmpty()){
//            return ResponseEntity.internalServerError().build();
//        }
//
//        // It willl check if the task exists or not
//        Optional<PersonalTask> optionalTask = personalTaskRepository.findById(taskId);
//        if (optionalTask.isEmpty()){
//            return ResponseEntity.notFound().build();
//        }
//
//
//        PersonalTask task = optionalTask.get();
//
//        //It will check if the netId in the task is the same as the netId of the input user
//        if(!( task.getUser().getNetId().equals(curUser.get().getNetId()))){
//            return ResponseEntity.notFound().build();
//        }
//
//        // The netId of the user have to match with the netID on the task in order to make changes
//        if (title != null) {
//            task.setTitle(title);
//        }
//        if (description != null) {
//            task.setDescription(description);
//        }
//        if (dueDateTimestamp != null){
//            task.setDueDate(new Date(dueDateTimestamp));
//        }
//
//        personalTaskRepository.save(task);
//
//        return ResponseEntity.ok(true);
//    }

    @PutMapping("/updatePersonalTask/{sId}")
    public ResponseEntity<String> updatePersonTasks(
            @PathVariable String sId,
            @RequestParam int taskId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Long dueDateTimestamp
    ) {
        // Check if the user exists
        Optional<User> curUser = userRepository.findById(sId);
        if (curUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with ID " + sId + " not found.");
        }

        // Check if the task exists
        Optional<PersonalTask> optionalTask = personalTaskRepository.findById(taskId);
        if (optionalTask.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Task with ID " + taskId + " not found.");
        }

        PersonalTask task = optionalTask.get();

        // Check if the netId in the task matches the netId of the input user
        if  (!task.getUser().getNetId().trim().equalsIgnoreCase(sId.trim())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not authorized to update this task.");
        }

        // Update the task details if they are provided
        if (title != null) {
            task.setTitle(title);
        }
        if (description != null) {
            task.setDescription(description);
        }
        if (dueDateTimestamp != null) {
            task.setDueDate(new Date(dueDateTimestamp));
        }

        // Save the updated task
        personalTaskRepository.save(task);

        return ResponseEntity.ok("Task updated successfully.");
    }


    @DeleteMapping("/deletePersonalTask/{sId}")
    public ResponseEntity<String> deletePersonalTasks(
            @PathVariable String sId,
            @RequestParam int taskId
    ){
        // Check if the user exists
        Optional<User> curUser = userRepository.findById(sId);
        if (curUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with ID " + sId + " not found.");
        }

        // Check if the task exists
        Optional<PersonalTask> optionalTask = personalTaskRepository.findById(taskId);
        if (optionalTask.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Task with ID " + taskId + " not found.");
        }

        PersonalTask task = optionalTask.get();

        // Check if the netId in the task matches the netId of the input user
        if  (!task.getUser().getNetId().trim().equalsIgnoreCase(sId.trim())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not authorized to delete this task.");
        }
        personalTaskRepository.delete(task);
        return ResponseEntity.ok("Task deleted successfully.");
    }
}
