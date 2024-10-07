package coms309.backEnd.demo.controller;

import coms309.backEnd.demo.entity.PersonalTask;
import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.repository.PersonalTaskRepository;
import coms309.backEnd.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
}
