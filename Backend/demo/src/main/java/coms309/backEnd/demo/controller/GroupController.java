package coms309.backEnd.demo.controller;

import coms309.backEnd.demo.entity.Group;
import coms309.backEnd.demo.entity.GroupMessages;
import coms309.backEnd.demo.entity.Join;
import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.repository.GroupMessagesRepository;
import coms309.backEnd.demo.repository.GroupRepository;
import coms309.backEnd.demo.repository.JoinRepository;
import coms309.backEnd.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @Autowired
    private final GroupMessagesRepository groupMessagesRepository;

    public GroupController(GroupRepository groupRepository, JoinRepository joinRepository, UserRepository userRepository, GroupMessagesRepository groupMessagesRepository) {
        this.groupRepository = groupRepository;
        this.joinRepository = joinRepository;
        this.userRepository = userRepository;
        this.groupMessagesRepository = groupMessagesRepository;
    }


    private boolean checkIfUserInTheGroup(Group group, User targetUser) {
        List<Join> joins = group.getJoins();
        for (Join join : joins) {
            User user = join.getUser();
            if (targetUser.getId() == user.getId()) {
                return true;
            }
        }
        return false;

    }

    @PostMapping("/create")
    public ResponseEntity<String> createGroup(
            @RequestParam(required = false) String groupName,
            @RequestParam String netId
    ) {
        // Check if user exists
        Optional<User> curUser = userRepository.findUserByNetId(netId);
        if (curUser.isEmpty()) {
            return ResponseEntity.internalServerError().build();
        }
        User user = curUser.get();

        // Create the group
        Group group = new Group(groupName,user);
        groupRepository.save(group);

        // Add the user to the group by default
        Join join = new Join(group, user);
        joinRepository.save(join);

        // Add the message to the group
        GroupMessages message = new GroupMessages();
        message.setSender(null);
        message.setGroup(group);
        message.setContent(netId + " created the group");
        groupMessagesRepository.save(message);


        return ResponseEntity.ok("Group created successfully with the user added.");
    }

    @PostMapping("/addMember")
    public ResponseEntity<String> addMember(
            @RequestParam String adderNetId,
            @RequestParam String personAddedNetId,
            @RequestParam Long groupId
    ) {
        // Check if adder exists
        Optional<User> curAdder = userRepository.findUserByNetId(adderNetId);
        if (curAdder.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: Adder with NetID " + adderNetId + " does not exist.");
        }
        User adder = curAdder.get();

        // Check if the person to be added exists
        Optional<User> curPersonAdded = userRepository.findUserByNetId(personAddedNetId);
        if (curPersonAdded.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: User with NetID " + personAddedNetId + " does not exist.");
        }
        User personAdded = curPersonAdded.get();

        // Check if the group exists
        Optional<Group> curGroup = groupRepository.findById(groupId);
        if (curGroup.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: Group with ID " + groupId + " does not exist.");
        }
        Group group = curGroup.get();

        // Check if the adder is part of the group
        if (!checkIfUserInTheGroup(group, adder)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: Adder with NetID " + adderNetId + " is not a member of the group.");
        }

        // Add this person to the group
        Join join = new Join(group, personAdded);
        joinRepository.save(join);

        // Add the welcome message to the group
        GroupMessages message = new GroupMessages();
        message.setSender(null);
        message.setGroup(group);
        message.setContent(personAddedNetId + " joined the group");
        groupMessagesRepository.save(message);

        return ResponseEntity.ok("Member added successfully");
    }

    @PutMapping("/modifyGroupName")
    public ResponseEntity<String> modifyGroupName(
            @RequestParam String netId,
            @RequestParam long groupId,
            @RequestParam String groupName
    ){
        // Check if user exists
        Optional<User> curUser = userRepository.findUserByNetId(netId);
        if (curUser.isEmpty()) {
            return ResponseEntity.internalServerError().build();
        }
        User user = curUser.get();
        // Check the group exists
        Optional<Group> curGroup = groupRepository.findById(groupId);
        if(curGroup.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        Group group = curGroup.get();

        // If the target name of the group is not null, set it to the group name
        if(groupName != null){
            group.setName(groupName);
            groupRepository.save(group);
        }

        //Check if user is in the group or not
        if(!checkIfUserInTheGroup(group,user)){
            return ResponseEntity.internalServerError().build();
        }


        // Sending the message to the group chat to specify who changed the name of the group
        GroupMessages message = new GroupMessages();
        message.setGroup(group);
        message.setSender(null);
        message.setContent(netId + " changed the group name into " + groupName);
        groupMessagesRepository.save(message);


        return ResponseEntity.ok("Update the name of the group successfully");
    }
    // this is the API using the list<Group> in User Entity to find the latest group of a given user created
    @PostMapping("/addInitialMembers")
    public ResponseEntity<String> addInitialMembers(
            @RequestParam String creatorNetId,
            @RequestParam String personBeingAdded
    ){
        // validate
        Optional<User> curUser = userRepository.findUserByNetId(creatorNetId);
        if(curUser.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        User user = curUser.get();

        Optional<User> curPersonalBeingAdded = userRepository.findUserByNetId(personBeingAdded);
        if(curPersonalBeingAdded.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        User userAdded = curPersonalBeingAdded.get();


       List<Group> groups = user.getGroupsCreated();
       if(groups.isEmpty()){
           return ResponseEntity.internalServerError().build();
       }
       Group group = groups.get(groups.size()-1);

        //Create join
        Join join = new Join(group,userAdded);
        joinRepository.save(join);

        //Add the welcome message to the group
        GroupMessages message = new GroupMessages();
        message.setSender(null);
        message.setGroup(group);
        message.setContent(personBeingAdded + " joined the group");
        groupMessagesRepository.save(message);
        return ResponseEntity.ok("Added new member successfully");
    }

    // This is the API using query from MySQL to find the latest group that a given user create
    @GetMapping("/latestGroup")
    public ResponseEntity<Group> findTheLatestGroup(
            @RequestParam String netId
    ){
        Optional<User> curUser = userRepository.findUserByNetId(netId);
        if(curUser.isEmpty()){
            return ResponseEntity.internalServerError().build();
        }
        User user = curUser.get();

        Optional<Group> curGroup = groupRepository.findLatestGroupByUser(user.getId());
        if(curGroup.isEmpty()){
            return ResponseEntity.internalServerError().build();
        }
        Group group = curGroup.get();

        return ResponseEntity.ok(group);
    }


}
