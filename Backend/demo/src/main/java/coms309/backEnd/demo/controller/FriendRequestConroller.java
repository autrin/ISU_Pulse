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


    /**\
     * This API sends the friend request from one user to another user
     * @param senderId the id(type: long) of the sender
     * @param receiverId the id(type: long) of the receiver
     * @return a message "Friend request sent" if it is successful
     */
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

    /**
     * This Api is used for the receiver to accept the friend request
     * @param receiverId the ID of reciever
     * @param requestId the ID of the friend request
     * @return the message "Friend request accepted" if the receiver accepts the friend request
     */
    @PutMapping("/accept")
    public ResponseEntity<String> acceptFriendRequest (
            @RequestParam long receiverId,
            @RequestParam long requestId
    ){
        // Check user exists or not
        Optional<User> curReceiver = userRepository.findById(receiverId);
        if (curReceiver.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with ID " + receiverId  + " not found.");
        }
        User receiver = curReceiver.get();

        // Check the request exists or not
        Optional<FriendRequest> curFriendRequest = friendRequestRepository.findById(requestId);
        if(curFriendRequest.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Friend Request with ID " + requestId + " not found.");
        }
        FriendRequest friendRequest = curFriendRequest.get();

        // Check if the receiver of the friend request is the same as the person who try to accept this friend request
        if(friendRequest.getReceiver().getId() != receiverId){
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
    /**
     * This Api is used for the receiver to decline the friend request
     * @param receiverId the ID of reciever
     * @param requestId the ID of the friend request
     * @return the message "Friend request rejected" if the receiver declines the friend request
     */
    @PutMapping("/decline")
    public ResponseEntity<String> declineFriendRequest (
            @RequestParam long receiverId,
            @RequestParam long requestId
    ){
        // Check user exists or not
        Optional<User> curReceiver = userRepository.findById(receiverId);
        if (curReceiver.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with ID " + receiverId  + " not found.");
        }
        User receiver = curReceiver.get();

        // Check the request exists or not
        Optional<FriendRequest> curFriendRequest = friendRequestRepository.findById(requestId);
        if(curFriendRequest.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Friend Request with ID " + requestId + " not found.");
        }
        FriendRequest friendRequest = curFriendRequest.get();

        // Check if the receiver of the friend request is the same as the person who try to accept this friend request
        if(friendRequest.getReceiver().getId() != receiverId){
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
            @RequestParam long senderId,
            @RequestParam long requestId
            ){
        // Check user exists or not
        Optional<User> curSender = userRepository.findById(senderId);
        if (curSender.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with ID " + senderId  + " not found.");
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
        if(friendRequest.getSender().getId() != senderId){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("You can not modify this friend request");
        }
        //Delete the friend request
        friendRequestRepository.delete(friendRequest);
        return ResponseEntity.ok("Unsent Friend request successfully");
    }




}
