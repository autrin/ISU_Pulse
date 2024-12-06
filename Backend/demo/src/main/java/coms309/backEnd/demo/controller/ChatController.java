package coms309.backEnd.demo.controller;

import coms309.backEnd.demo.DTO.ChatMessageDTO;
import coms309.backEnd.demo.entity.*;
import coms309.backEnd.demo.repository.ChatMessageRepository;
import coms309.backEnd.demo.repository.GroupMessagesRepository;
import coms309.backEnd.demo.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private final ChatMessageRepository chatMessageRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final GroupMessagesRepository groupMessagesRepository;

    public ChatController(ChatMessageRepository chatMessageRepository, UserRepository userRepository, GroupMessagesRepository groupMessagesRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        this.groupMessagesRepository = groupMessagesRepository;
    }


    /**
     * Retrieves the chat history between two users identified by their NetIDs.
     *
     * @param user1NetId The NetID of the first user.
     * @param user2NetId The NetID of the second user.
     * @return A list of chat messages exchanged between the two users.
     */
    @Operation(summary = "Get Chat History", description = "Retrieve the chat history between two users identified by their NetIDs.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chat history retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChatMessageDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user IDs provided",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    @GetMapping("/history")
    public ResponseEntity<List<ChatMessageDTO>> getChatHistory(
            @Parameter(description = "The NetID of the first user", required = true)
            @RequestParam String user1NetId,
            @Parameter(description = "The NetID of the second user", required = true)
            @RequestParam String user2NetId) {

        // Ensure both users exist in the database
        User user1 = userRepository.findUserByNetId(user1NetId).orElse(null);
        User user2 = userRepository.findUserByNetId(user2NetId).orElse(null);

        if (user1 == null || user2 == null) {
            return ResponseEntity.badRequest().body(null);
        }

        // Retrieve chat history between the two users, in both directions
        List<ChatMessage> chatMessages = chatMessageRepository.findMessagesBetweenUsers(user1NetId, user2NetId);

        // Convert ChatMessage entities to ChatMessageDTOs
        List<ChatMessageDTO> chatHistory = new ArrayList<>();
        for (ChatMessage message : chatMessages) {
            ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
            chatMessageDTO.setSenderNetId(message.getSender().getNetId());
            chatMessageDTO.setRecipientNetId(message.getRecipient().getNetId());
            chatMessageDTO.setContent(message.getContent());
            chatMessageDTO.setTimestamp(message.getTimestamp());
            chatHistory.add(chatMessageDTO);
        }

        return ResponseEntity.ok(chatHistory);
    }

//    @PostMapping("/send")
//    public ResponseEntity<String> sendChatMessage(@RequestBody ChatMessageDTO chatMessageDTO) {
//        // Verify sender and recipient exist
//        User sender = userRepository.findUserByNetId(chatMessageDTO.getSenderNetId()).orElse(null);
//        User recipient = userRepository.findUserByNetId(chatMessageDTO.getRecipientNetId()).orElse(null);
//
//        if (sender == null || recipient == null) {
//            return ResponseEntity.badRequest().body("Invalid sender or recipient.");
//        }
//
//        // Save the chat message to the database
//        ChatMessage chatMessage = new ChatMessage(sender, recipient, chatMessageDTO.getContent());
//        chatMessageRepository.save(chatMessage);
//
//        return ResponseEntity.ok("Message sent successfully.");
//    }

    /**
     * Retrieves the latest chat messages between the specified user and all other users they have messaged with.
     *
     * @param netId The NetID of the user.
     * @return A list of the latest chat messages with each user the specified user has interacted with.
     */
    @Operation(summary = "Get All Latest Messages", description = "Retrieve the latest chat messages between the specified user and all other users they have messaged with.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Latest messages retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChatMessage.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user ID provided",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    @GetMapping("/allLatestMessages/{netId}")
    public ResponseEntity<List<Message>> getUsersYouMessagingWith(
            @Parameter(description = "The NetID of the user", required = true)
            @PathVariable String netId){
        // Find the user with the given netId
        User user = userRepository.findUserByNetId(netId).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body(null);
        }
        // Fetch the list of unique users the specified user has chatted with
        List<User> chattedUserAsRecipient = chatMessageRepository.findDistinctRecipients(netId);
        List<User> chattedUserAsSender = chatMessageRepository.findDistinctSenders(netId);

        // Use a Set to avoid duplicates
        List<User> chattedUser = new ArrayList<>(chattedUserAsRecipient);
        for(User us : chattedUserAsSender){
            boolean isInTheList = false;
            for(User use : chattedUser){
                if (us.getId() == use.getId()) {
                    isInTheList = true;
                    break;
                }
            }
            if(!isInTheList){
                chattedUser.add(us);
            }
        }
        // Get the latest message from user that a given user is messaging with
        List<Message> latestMessages = new ArrayList<>();
        for(User userInChatList : chattedUser){
            Message chatMessage = getLatestMessageBetween2User(netId,userInChatList.getNetId()).getBody();
            latestMessages.add(chatMessage);
        }

        //Get all the group that a given user is in
        List<Join> joins = user.getJoins();
        List<Group> groups = new ArrayList<>();
        for(Join join : joins){
            groups.add(join.getGroup());
        }

        for(Group group : groups){
            List<GroupMessages> groupMessages = groupMessagesRepository.findByGroupId(group.getId());
            if(!groupMessages.isEmpty()){
                latestMessages.add(groupMessages.get(groupMessages.size()-1));
            }
        }
        latestMessages.sort(new Comparator<Message>() {
            @Override
            public int compare(Message message1, Message message2) {
                return message2.getTimestamp().compareTo(message1.getTimestamp());
            }
        });
        return ResponseEntity.ok(latestMessages);
    }
        /**
         * Retrieves the latest chat message between two users identified by their NetIDs.
         *
         * @param netIdUser1 The NetID of the first user.
         * @param netIdUser2 The NetID of the second user.
         * @return The latest chat message exchanged between the two users.
         */
        @Operation(summary = "Get Latest Message Between Two Users", description = "Retrieve the latest chat message between two users identified by their NetIDs.")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Latest chat message retrieved successfully",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChatMessage.class))),
                @ApiResponse(responseCode = "400", description = "Invalid user IDs provided",
                        content = @Content),
                @ApiResponse(responseCode = "404", description = "No chat messages found between the users",
                        content = @Content),
                @ApiResponse(responseCode = "500", description = "Internal server error",
                        content = @Content)
        })
        @GetMapping("/getLatestMessageBetween2User")
        public ResponseEntity<ChatMessage> getLatestMessageBetween2User(
                @Parameter(description = "The NetID of the first user", required = true)
                @RequestParam String netIdUser1,
                @Parameter(description = "The NetID of the second user", required = true)
                @RequestParam String netIdUser2){

            // Check if these 2 users exist
            User user1 = userRepository.findUserByNetId(netIdUser1).orElse(null);
            if (user1 == null) {
                return ResponseEntity.badRequest().body(null);
            }

            User user2 = userRepository.findUserByNetId(netIdUser2).orElse(null);
            if (user2 == null) {
                return ResponseEntity.badRequest().body(null);
            }

            List<ChatMessage> chatMessages = chatMessageRepository.findMessagesBetweenUsers(netIdUser1,netIdUser2);

            ChatMessage chatMessage = null;
            if(!chatMessages.isEmpty()){
                chatMessage = chatMessages.get(chatMessages.size()-1);
            }
            return ResponseEntity.ok(chatMessage);

        }



}
