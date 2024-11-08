package coms309.backEnd.demo.controller;

import coms309.backEnd.demo.entity.FriendRequest;
import coms309.backEnd.demo.entity.FriendShip;
import coms309.backEnd.demo.entity.RequestStatus;
import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.repository.FriendRequestRepository;
import coms309.backEnd.demo.repository.FriendShipRepository;
import coms309.backEnd.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/friendRequest")
public class FriendRequestConroller {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final FriendRequestRepository friendRequestRepository;

    @Autowired
    private final FriendShipRepository friendShipRepository;


    public FriendRequestConroller(UserRepository userRepository, FriendRequestRepository friendRequestRepository, FriendShipRepository friendShipRepository) {
        this.userRepository = userRepository;
        this.friendRequestRepository = friendRequestRepository;
        this.friendShipRepository = friendShipRepository;
    }


    @GetMapping("/receivedRequest/{netId}")
    public ResponseEntity<List<User>> getAllFriendRequest(@PathVariable String netId){
        Optional<User> curReceiver = userRepository.findUserByNetId(netId);
        if (curReceiver.isEmpty()) {
            return  ResponseEntity.internalServerError().build();
        }
        User receiver = curReceiver.get();
        List<FriendRequest> receivedRequests = friendRequestRepository.findAllByReceiverAndStatus(receiver,RequestStatus.PENDING);
        List<User> listOfSenders = new ArrayList<>();
        for(FriendRequest friendRequest : receivedRequests){
            listOfSenders.add(friendRequest.getSender());
        }
        return ResponseEntity.ok(listOfSenders);
    }

    @GetMapping("/sentRequest/{netId}")
    public ResponseEntity<List<User>> getAllSentRequest(@PathVariable String netId){
        Optional<User> curSender = userRepository.findUserByNetId(netId);
        if (curSender.isEmpty()) {
            return  ResponseEntity.internalServerError().build();
        }
        User sender = curSender.get();
        List<FriendRequest> sentRequest = friendRequestRepository.findAllBySenderAndStatus(sender, RequestStatus.PENDING);
        List<User> listOfReceiver = new ArrayList<>();
        for(FriendRequest friendRequest : sentRequest){
            listOfReceiver.add(friendRequest.getReceiver());
        }
        return ResponseEntity.ok(listOfReceiver);
    }

    @PostMapping("/request")
    public ResponseEntity<String> sendFriendRequest(
            @RequestParam String senderNetId,
            @RequestParam String receiverNetId){
        Optional<User> curSender = userRepository.findUserByNetId(senderNetId);
        Optional<User> curReceiver = userRepository.findUserByNetId(receiverNetId);

        if (curSender.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with ID " + senderNetId + " not found.");
        }
        if (curReceiver.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with ID " + receiverNetId + " not found.");
        }

        User sender = curSender.get();
        User receiver = curReceiver.get();

        FriendRequest request = new FriendRequest(sender, receiver, RequestStatus.PENDING);
        friendRequestRepository.save(request);

        return ResponseEntity.ok("Friend request sent.");
    }

    @DeleteMapping("/accept")
    public ResponseEntity<String> acceptFriendRequest (
            @RequestParam String receiverNetId,
            @RequestParam String senderNetId
    ){
        // Check user exists or not
        Optional<User> curReceiver = userRepository.findUserByNetId(receiverNetId);
        if (curReceiver.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with ID " + receiverNetId  + " not found.");
        }
        User receiver = curReceiver.get();

        Optional<User> curSender = userRepository.findUserByNetId(senderNetId);
        if (curSender.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with ID " + senderNetId  + " not found.");
        }
        User sender = curSender.get();

        // Check the request exists or not
        Optional<FriendRequest> curFriendRequest = friendRequestRepository.findFriendRequestBySenderAndReceiver(sender,receiver);
        if(curFriendRequest.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Friend request not exist");
        }
        FriendRequest friendRequest = curFriendRequest.get();

        //getNetId().trim().equalsIgnoreCase(sId.trim())
        // Check if the receiver of the friend request is the same as the person who try to accept this friend request by NetId
        if(!friendRequest.getReceiver().getNetId().trim().equalsIgnoreCase(receiverNetId.trim())){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("You can not modify this friend request");
        }

        //Create the FriendShip Object and add it into the FriendShip table
        FriendShip friendShip = new FriendShip(friendRequest.getSender(), friendRequest.getReceiver());
        friendShipRepository.save(friendShip);

        // After creating the friendship between 2 user, delete it in the friendRequestRepository
        friendRequestRepository.delete(friendRequest);
        return ResponseEntity.ok("Friend request accepted");
    }

    @DeleteMapping("/reject")
    public ResponseEntity<String> declineFriendRequest (
            @RequestParam String receiverNetId,
            @RequestParam String senderNetId
    ){
        // Check user exists or not
        Optional<User> curReceiver = userRepository.findUserByNetId(receiverNetId);
        if (curReceiver.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with ID " + receiverNetId  + " not found.");
        }
        User receiver = curReceiver.get();

        Optional<User> curSender = userRepository.findUserByNetId(senderNetId);
        if (curSender.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with ID " + senderNetId  + " not found.");
        }
        User sender = curSender.get();

        // Check the request exists or not
        Optional<FriendRequest> curFriendRequest = friendRequestRepository.findFriendRequestBySenderAndReceiver(sender,receiver);
        if(curFriendRequest.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Friend request not exist");
        }
        FriendRequest friendRequest = curFriendRequest.get();

        // Check if the receiver of the friend request is the same as the person who try to accept this friend request by NetId
        if(!friendRequest.getReceiver().getNetId().trim().equalsIgnoreCase(receiverNetId.trim())){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("You can not modify this friend request");
        }

        // Delete it in friendRequestRepository
        friendRequestRepository.delete(friendRequest);
        return ResponseEntity.ok("Friend request rejected");
    }

    @DeleteMapping("/unsent")
    public ResponseEntity<String> unsentFriendRequest(
            @RequestParam String senderNetId,
            @RequestParam String receiverNetId
    ){
        // Check user exists or not
        Optional<User> curReceiver = userRepository.findUserByNetId(receiverNetId);
        if (curReceiver.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with ID " + receiverNetId  + " not found.");
        }
        User receiver = curReceiver.get();

        Optional<User> curSender = userRepository.findUserByNetId(senderNetId);
        if (curSender.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with ID " + senderNetId  + " not found.");
        }
        User sender = curSender.get();

        // Check the request exists or not
        Optional<FriendRequest> curFriendRequest = friendRequestRepository.findFriendRequestBySenderAndReceiver(sender,receiver);
        if(curFriendRequest.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Friend request not exist");
        }
        FriendRequest friendRequest = curFriendRequest.get();

        // Check if the receiver of the friend request is the same as the person who try to accept this friend request
        if(!friendRequest.getSender().getNetId().equalsIgnoreCase(senderNetId)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("You can not modify this friend request");
        }
        //Delete the friend request
        friendRequestRepository.delete(friendRequest);
        return ResponseEntity.ok("Unsent Friend request successfully");
    }
}
