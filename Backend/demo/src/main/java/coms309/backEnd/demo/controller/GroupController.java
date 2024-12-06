package coms309.backEnd.demo.controller;

import coms309.backEnd.demo.entity.Group;
import coms309.backEnd.demo.entity.Join;
import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.repository.GroupRepository;
import coms309.backEnd.demo.repository.JoinRepository;
import coms309.backEnd.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/groups")
public class GroupController {
    @Autowired
    private final GroupRepository groupRepository;

    @Autowired
    private final JoinRepository joinRepository;

    @Autowired
    private final UserRepository userRepository;


    public GroupController(GroupRepository groupRepository, JoinRepository joinRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.joinRepository = joinRepository;
        this.userRepository = userRepository;
    }

    private boolean checkIfUserInTheGroup(Group group, User targetUser){
        List<Join> joins = group.getJoins();
        for(Join join : joins){
            User user  = join.getUser();
            if(targetUser.getId() == user.getId()){
                return true;
            }
        }
        return false;

    }

    @PostMapping("/create")
    public ResponseEntity<String> createGroup(
            @RequestParam(required = false) String groupName,
            @RequestParam String netId
    ){
        // Check if user exists
        Optional<User> curUser = userRepository.findUserByNetId(netId);
        if(curUser.isEmpty()){
            return  ResponseEntity.internalServerError().build();
        }
        User user = curUser.get();

        // Create the group
        Group group = new Group(groupName);
        groupRepository.save(group);

        // Add the user to the group by default
        Join join = new Join(group, user);
        joinRepository.save(join);

        return ResponseEntity.ok("Group created successfully with the user added.");
    }

    @PostMapping("/addMember")
    public ResponseEntity<String> addMember(
            @RequestParam String adderNetId,
            @RequestParam String personAddedNetId,
            @RequestParam Long groupId
    ){
        //Check if 2 users exist
        Optional<User> curAdder = userRepository.findUserByNetId(adderNetId);
        if(curAdder.isEmpty()){
            return  ResponseEntity.internalServerError().build();
        }
        User adder = curAdder.get();

        Optional<User> curPersonAdded  = userRepository.findUserByNetId(personAddedNetId);
        if(curPersonAdded.isEmpty()){
            return  ResponseEntity.internalServerError().build();
        }
        User personAdded = curPersonAdded.get();

        //Check if the group exist
        Optional<Group> curGroup = groupRepository.findById(groupId);
        if(curGroup.isEmpty()){
            return  ResponseEntity.internalServerError().build();
        }
        Group group = curGroup.get();
        //Check if adder is in the group
        if(!checkIfUserInTheGroup(group,adder)){
            return  ResponseEntity.internalServerError().build();
        }
        //Add this person to the group
        Join join = new Join(group, personAdded);
        joinRepository.save(join);

        return ResponseEntity.ok("Add member successfully");
    }
}
