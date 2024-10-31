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
    public ResponseEntity<List<FriendRequest>> getAllFriendRequest(@PathVariable String netId){
        Optional<User> curReceiver = userRepository.findUserByNetId(netId);
        if (curReceiver.isEmpty()) {
            return  ResponseEntity.internalServerError().build();
        }
        User receiver = curReceiver.get();
        List<FriendRequest> receivedRequests = friendRequestRepository.findAllByReceiverAndStatus(receiver,RequestStatus.PENDING);
        return ResponseEntity.ok(receivedRequests);
    }

    @GetMapping("/sentRequest/{netId}")
    public ResponseEntity<List<FriendRequest>> getAllSentRequest(@PathVariable String netId){
        Optional<User> curSender = userRepository.findUserByNetId(netId);
        if (curSender.isEmpty()) {
            return  ResponseEntity.internalServerError().build();
        }
        User sender = curSender.get();
        List<FriendRequest> sentRequest = friendRequestRepository.findAllBySenderAndStatus(sender, RequestStatus.PENDING);
        return ResponseEntity.ok(sentRequest);
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

    @PutMapping("/accept")
    public ResponseEntity<String> acceptFriendRequest (
            @RequestParam String receiverNetId,
            @RequestParam long requestId
    ){
        // Check user exists or not
        Optional<User> curReceiver = userRepository.findUserByNetId(receiverNetId);
        if (curReceiver.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with ID " + receiverNetId  + " not found.");
        }
        User receiver = curReceiver.get();

        // Check the request exists or not
        Optional<FriendRequest> curFriendRequest = friendRequestRepository.findById(requestId);
        if(curFriendRequest.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Friend Request with ID " + requestId + " not found.");
        }
        FriendRequest friendRequest = curFriendRequest.get();

        //getNetId().trim().equalsIgnoreCase(sId.trim())
        // Check if the receiver of the friend request is the same as the person who try to accept this friend request by NetId
        if(!friendRequest.getReceiver().getNetId().trim().equalsIgnoreCase(receiverNetId)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("You can not modify this friend request");
        }
        // Set the friend request status into "Accepted" and save it in the friendRequestRepository
        friendRequest.setStatus(RequestStatus.ACCEPTED);
        friendRequestRepository.save(friendRequest);

        //Create the FriendShip Object and add it into the FriendShip table
        FriendShip friendShip = new FriendShip(friendRequest.getSender(), friendRequest.getReceiver());
        friendShipRepository.save(friendShip);

        return ResponseEntity.ok("Friend request accepted");
    }

    @PutMapping("/reject")
    public ResponseEntity<String> declineFriendRequest (
            @RequestParam String receiverNetId,
            @RequestParam long requestId
    ){
        // Check user exists or not
        Optional<User> curReceiver = userRepository.findUserByNetId(receiverNetId);
        if (curReceiver.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with ID " + receiverNetId  + " not found.");
        }
        User receiver = curReceiver.get();

        // Check the request exists or not
        Optional<FriendRequest> curFriendRequest = friendRequestRepository.findById(requestId);
        if(curFriendRequest.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Friend Request with ID " + requestId + " not found.");
        }
        FriendRequest friendRequest = curFriendRequest.get();

        // Check if the receiver of the friend request is the same as the person who try to accept this friend request by NetId
        if(!friendRequest.getReceiver().getNetId().trim().equalsIgnoreCase(receiverNetId)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("You can not modify this friend request");
        }

        // Set the friend request status into "Rejected" and save it in friendRequestRepository
        friendRequest.setStatus(RequestStatus.REJECTED);
        friendRequestRepository.save(friendRequest);

        return ResponseEntity.ok("Friend request rejected");
    }

    @DeleteMapping("/unsent")
    public ResponseEntity<String> unsentFriendRequest(
            @RequestParam String senderNetId,
            @RequestParam long requestId
            ){
        // Check user exists or not
        Optional<User> curSender = userRepository.findUserByNetId(senderNetId);
        if (curSender.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with ID " + senderNetId + " not found.");
        }
        User sender = curSender.get();

        // Check the request exists or not
        Optional<FriendRequest> curFriendRequest = friendRequestRepository.findById(requestId);
        if(curFriendRequest.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Friend Request with ID " + requestId + " not found.");
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
