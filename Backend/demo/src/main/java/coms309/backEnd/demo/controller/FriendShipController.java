package coms309.backEnd.demo.controller;

import coms309.backEnd.demo.entity.*;
import coms309.backEnd.demo.repository.FriendShipRepository;
import coms309.backEnd.demo.repository.GroupMessagesRepository;
import coms309.backEnd.demo.repository.GroupRepository;
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

import java.util.*;

@RestController
@RequestMapping("/friendShip")
public class FriendShipController {
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final FriendShipRepository friendShipRepository;

    @Autowired
    private final GroupRepository groupRepository;

    public FriendShipController(UserRepository userRepository, FriendShipRepository friendShipRepository, GroupRepository groupRepository) {
        this.userRepository = userRepository;
        this.friendShipRepository = friendShipRepository;
        this.groupRepository = groupRepository;
    }


    private List<User> getFriendsfromFriendships(List<FriendShip> friendShips, User user){
        List<User> friendlst = new ArrayList<>();
        for(FriendShip friendShip : friendShips){
            if(friendShip.getUser1().getId() == user.getId()){
                friendlst.add(friendShip.getUser2());
            }
            else if (friendShip.getUser1().getId() != user.getId()) {
                friendlst.add(friendShip.getUser1());
            }
        }
        return friendlst;
    }
    /**
     * Retrieves the list of friends for a specific user identified by their NetID.
     *
     * @param netId The NetID of the user.
     * @return A list of friends associated with the user.
     */
    @Operation(summary = "Get Friend List", description = "Retrieve the list of friends for a specific user by their NetID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friend list retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    @GetMapping("/friends/{netId}")
    public ResponseEntity<List<User>> displayFriendList(
            @Parameter(description = "The NetID of the user", required = true)
            @PathVariable String netId){
        Optional<User> curUser = userRepository.findUserByNetId(netId);
        if(curUser.isEmpty()){
            return  ResponseEntity.internalServerError().build();
        }
        User user = curUser.get();
        List<FriendShip> friendShips = user.getFriendShips();
        List<User> friendList = getFriendsfromFriendships(friendShips,user);
        return ResponseEntity.ok(friendList);
    }


    /**
     * Retrieves a sorted list of friends for a specific user identified by their NetID.
     * The list is sorted alphabetically by first name and then by last name.
     *
     * @param netId The NetID of the user.
     * @return A sorted list of friends associated with the user.
     */
    @Operation(summary = "Get Sorted Friend List", description = "Retrieve a sorted list of friends for a specific user by their NetID. The list is sorted alphabetically by first name and then by last name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sorted friend list retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    @GetMapping("/sortFriends/{netId}")
    public ResponseEntity<List<User>> displayingSortedFriendList(
            @Parameter(description = "The NetID of the user", required = true)
            @PathVariable String netId){
        Optional<User> curUser = userRepository.findUserByNetId(netId);
        if(curUser.isEmpty()){
            return  ResponseEntity.internalServerError().build();
        }
        User user = curUser.get();
        List<FriendShip> friendShips = user.getFriendShips();
        List<User> friendList = getFriendsfromFriendships(friendShips,user);

        friendList.sort(new Comparator<User>() {
            @Override
            public int compare(User user1, User user2) {
                int firstNameComparison = user1.getFirstName().compareToIgnoreCase(user2.getFirstName());
                if (firstNameComparison != 0) {
                    return firstNameComparison;
                } else{
                    return user1.getLastName().compareToIgnoreCase(user2.getLastName());
                }

            }
        });
        return ResponseEntity.ok(friendList);
    }
    /**
     * Checks if two users are friends based on their NetIDs.
     *
     * @param netIdUser1 The NetID of the first user.
     * @param netIdUser2 The NetID of the second user.
     * @return True if the users are friends, otherwise false.
     */
    @Operation(summary = "Check Friendship Status", description = "Determine if two users are friends based on their NetIDs.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friendship status retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    @GetMapping("/isFriend")
    public ResponseEntity<Boolean> checkIfTwoUsersAreFriends(
            @Parameter(description = "The NetID of the first user", required = true)
            @RequestParam String netIdUser1,
            @Parameter(description = "The NetID of the second user", required = true)
            @RequestParam String netIdUser2){

        // Check if user1 and user2 exists or not
        Optional<User> curUser1 = userRepository.findUserByNetId(netIdUser1);
        if(curUser1.isEmpty()){
            return  ResponseEntity.ok(false);
        }
        User user1 = curUser1.get();

        Optional<User> curUser2 = userRepository.findUserByNetId(netIdUser2);
        if(curUser2.isEmpty()){
            return  ResponseEntity.ok(false);
        }
        User user2 = curUser2.get();

        Optional<FriendShip> friendShip = friendShipRepository.findFriendShipBetweenUsers(user1, user2);
        if(friendShip.isEmpty()){
            return ResponseEntity.ok(false);
        }
        return ResponseEntity.ok(true);
    }


    /**
     * Retrieves a list of common friends between two users identified by their NetIDs.
     *
     * @param netIdUser1 The NetID of the first user.
     * @param netIdUser2 The NetID of the second user.
     * @return A list of users who are friends with both users.
     */
    @Operation(summary = "Get Common Friends", description = "Retrieve a list of common friends between two users identified by their NetIDs.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Common friends retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    @GetMapping("/sameFriends")
    public ResponseEntity<List<User>> displayingFriendsInCommon(
            @Parameter(description = "The NetID of the first user", required = true)
            @RequestParam String netIdUser1,
            @Parameter(description = "The NetID of the second user", required = true)
            @RequestParam String netIdUser2){

        Optional<User> curUser1 = userRepository.findUserByNetId(netIdUser1);
        if(curUser1.isEmpty()){
            return  ResponseEntity.internalServerError().build();
        }
        User user1 = curUser1.get();

        Optional<User> curUser2 = userRepository.findUserByNetId(netIdUser2);
        if(curUser2.isEmpty()){
            return  ResponseEntity.internalServerError().build();
        }
        User user2 = curUser2.get();

        List<FriendShip> friendShips1 = user1.getFriendShips();
        List<User> friendLst1 = getFriendsfromFriendships(friendShips1,user1);

        List<FriendShip> friendShips2 = user2.getFriendShips();
        List<User> friendLst2 = getFriendsfromFriendships(friendShips2,user2);

        List<User> friendsInCommon = new ArrayList<>();
        for(User userFromFriendLst1 : friendLst1){
            boolean isInCommon = false;
            for(User userFromFriendLst2 : friendLst2){
                if (userFromFriendLst1.getId() == userFromFriendLst2.getId()) {
                    isInCommon = true;
                    break;
                }
            }
            if(isInCommon){
                friendsInCommon.add(userFromFriendLst1);
            }
        }
        return ResponseEntity.ok(friendsInCommon);
    }
    /**
     * Provides a list of suggested friends for a specific user identified by their NetID.
     *
     * @param netId The NetID of the user.
     * @return A list of users who are not friends with the specified user.
     */
    @Operation(summary = "Get Friend Suggestions", description = "Retrieve a list of suggested friends for a user identified by their NetID. Suggestions are users who are not already friends with the specified user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friend suggestions retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    @GetMapping("/friendSuggestion/{netId}")
    public ResponseEntity<List<User>> getFriendSuggestion(
            @Parameter(description = "The NetID of the user", required = true)
            @PathVariable String netId){
        Optional<User> curUser = userRepository.findUserByNetId(netId);
        if(curUser.isEmpty()){
            return  ResponseEntity.internalServerError().build();
        }
        User user = curUser.get();
        List<User> listOfSuggestedFriends = userRepository.findUsersNotFriendsWith(user.getId());
        List<User> listOfSuggestedStudentFriends = new ArrayList<>();
        for(User friendUser : listOfSuggestedFriends){
            if(friendUser.getUserType().equals(UserType.STUDENT)){
                listOfSuggestedStudentFriends.add(friendUser);
            }
        }
        return ResponseEntity.ok(listOfSuggestedStudentFriends);
    }


    /**
     * Removes a friendship between two users identified by their NetIDs.
     *
     * @param userNetId1 The NetID of the first user.
     * @param userNetId2 The NetID of the second user.
     * @return A confirmation message upon successful unfriending.
     */
    @Operation(summary = "Unfriend Users", description = "Remove a friendship between two users identified by their NetIDs.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unfriended successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "User or Friendship not found",
                    content = @Content)
    })
    @DeleteMapping("/unfriend")
    public ResponseEntity<String> unfriend(
            @Parameter(description = "The NetID of the first user", required = true)
            @RequestParam String userNetId1,
            @Parameter(description = "The NetID of the second user", required = true)
            @RequestParam String userNetId2) {

        // Check if 2 users exist
        Optional<User> curUser1 = userRepository.findUserByNetId(userNetId1);
        if (curUser1.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with ID " + userNetId1 + " not found.");
        }
        User user1 = curUser1.get();

        Optional<User> curUser2 = userRepository.findUserByNetId(userNetId2);
        if (curUser2.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with ID " + userNetId2 + " not found.");
        }
        User user2 = curUser2.get();

        // Check if the friendship between 2 users exists or not
        Optional<FriendShip> friendship = friendShipRepository.findFriendShipBetweenUsers(user1, user2);
        if (friendship.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Friendship between " + userNetId1 + " and " + userNetId2 + " does not exist.");
        }

        friendShipRepository.delete(friendship.get());
        return ResponseEntity.ok("Unfriended successfully.");
    }

    @GetMapping("/fetchFriendNotInAGivenGroup")
    public ResponseEntity<List<User>> fetchUserNotfromAGivenGroup(
            @RequestParam String netId,
            @RequestParam long groupId
    ){
        //validate
        Optional<Group> curGroup = groupRepository.findById(groupId);
        if(curGroup.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        Group group = curGroup.get();

        User user = userRepository.findUserByNetId(netId).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(null);
        }
        // Check if user is in the group or not
        if(!checkUserInTheGroup(user,group)){
            return  ResponseEntity.internalServerError().build();
        }
        // List all the friends
        List<User> friends = displayFriendList(netId).getBody();
        List<User> friendsNotInAGivenGroup = new ArrayList<>();
        if(friends != null){
            for(User friend : friends) {
                if (!checkUserInTheGroup(friend, group)) {
                    friendsNotInAGivenGroup.add(friend);
                }
            }
        }
        return ResponseEntity.ok(friendsNotInAGivenGroup);
    }
    private boolean checkUserInTheGroup(User user, Group group){
        List<Join> joins = user.getJoins();
        for(Join join : joins){
            if(Objects.equals(join.getGroup().getId(), group.getId())){
                return true;
            }
        }
        return false;
    }
//    @GetMapping("/checkIfUserIsInTheGroup")
//    public ResponseEntity<Boolean> checkIfUserIsInTheGroup1(
//            @RequestParam String netId,
//            @RequestParam long groupId
//    ){
//        Optional<Group> curGroup = groupRepository.findById(groupId);
//        if(curGroup.isEmpty()){
//            return ResponseEntity.notFound().build();
//        }
//        Group group = curGroup.get();
//
//        User user = userRepository.findUserByNetId(netId).orElse(null);
//        if (user == null) {
//            return ResponseEntity.badRequest().body(null);
//        }
//        return ResponseEntity.ok(checkUserInTheGroup(user,group));
//    }
}
