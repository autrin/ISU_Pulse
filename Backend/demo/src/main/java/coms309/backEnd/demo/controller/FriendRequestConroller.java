package coms309.backEnd.demo.controller;

import coms309.backEnd.demo.entity.FriendRequest;
import coms309.backEnd.demo.entity.FriendShip;
import coms309.backEnd.demo.entity.RequestStatus;
import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.repository.FriendRequestRepository;
import coms309.backEnd.demo.repository.FriendShipRepository;
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

    /**
     * Retrieves all received friend requests for a specific user identified by their NetID.
     *
     * @param netId The NetID of the user.
     * @return A list of users who have sent friend requests to the specified user.
     */
    @Operation(summary = "Get Received Friend Requests", description = "Retrieve all pending friend requests received by a user identified by their NetID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Received friend requests retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    @GetMapping("/receivedRequest/{netId}")
    public ResponseEntity<List<User>> getAllFriendRequest(
            @Parameter(description = "The NetID of the user", required = true)
            @PathVariable String netId){
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


    /**
     * Retrieves all sent friend requests from a specific user identified by their NetID.
     *
     * @param netId The NetID of the user.
     * @return A list of users to whom the specified user has sent friend requests.
     */
    @Operation(summary = "Get Sent Friend Requests", description = "Retrieve all pending friend requests sent by a user identified by their NetID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sent friend requests retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    @GetMapping("/sentRequest/{netId}")
    public ResponseEntity<List<User>> getAllSentRequest(
            @Parameter(description = "The NetID of the user", required = true)
            @PathVariable String netId){
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


    /**
     * Sends a friend request from one user to another.
     *
     * @param senderNetId   The NetID of the user sending the friend request.
     * @param receiverNetId The NetID of the user receiving the friend request.
     * @return A confirmation message upon successful sending of the friend request.
     */
    @Operation(summary = "Send Friend Request", description = "Send a friend request from one user to another using their NetIDs.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friend request sent successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Sender or Receiver not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    @PostMapping("/request")
    public ResponseEntity<String> sendFriendRequest(
            @Parameter(description = "The NetID of the user sending the friend request", required = true)
            @RequestParam String senderNetId,
            @Parameter(description = "The NetID of the user receiving the friend request", required = true)
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
     * Accepts a received friend request, establishing a friendship between the sender and receiver.
     *
     * @param receiverNetId The NetID of the user accepting the friend request.
     * @param senderNetId   The NetID of the user who sent the friend request.
     * @return A confirmation message upon successful acceptance of the friend request.
     */
    @Operation(summary = "Accept Friend Request", description = "Accept a received friend request, establishing a friendship between the sender and receiver.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friend request accepted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "User or Friend Request not found",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden: Unauthorized to accept this friend request",
                    content = @Content)
    })
    @DeleteMapping("/accept")
    public ResponseEntity<String> acceptFriendRequest (
            @Parameter(description = "The NetID of the user accepting the friend request", required = true)
            @RequestParam String receiverNetId,
            @Parameter(description = "The NetID of the user who sent the friend request", required = true)
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
    /**
     * Rejects a received friend request.
     *
     * @param receiverNetId The NetID of the user rejecting the friend request.
     * @param senderNetId   The NetID of the user who sent the friend request.
     * @return A confirmation message upon successful rejection of the friend request.
     */
    @Operation(summary = "Reject Friend Request", description = "Reject a received friend request.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friend request rejected successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "User or Friend Request not found",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden: Unauthorized to reject this friend request",
                    content = @Content)
    })
    @DeleteMapping("/reject")
    public ResponseEntity<String> declineFriendRequest (
            @Parameter(description = "The NetID of the user rejecting the friend request", required = true)
            @RequestParam String receiverNetId,
            @Parameter(description = "The NetID of the user who sent the friend request", required = true)
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


    /**
     * Cancels a sent friend request that has not yet been accepted or rejected.
     *
     * @param senderNetId   The NetID of the user who sent the friend request.
     * @param receiverNetId The NetID of the user who received the friend request.
     * @return A confirmation message upon successful cancellation of the friend request.
     */
    @Operation(summary = "Unsend Friend Request", description = "Cancel a sent friend request that has not yet been accepted or rejected.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friend request unsent successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "User or Friend Request not found",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden: Unauthorized to unsend this friend request",
                    content = @Content)
    })
    @DeleteMapping("/unsent")
    public ResponseEntity<String> unsentFriendRequest(
            @Parameter(description = "The NetID of the user who sent the friend request", required = true)
            @RequestParam String senderNetId,
            @Parameter(description = "The NetID of the user who received the friend request", required = true)
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
