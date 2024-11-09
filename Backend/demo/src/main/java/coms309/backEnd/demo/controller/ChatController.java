package coms309.backEnd.demo.controller;

import coms309.backEnd.demo.DTO.ChatMessageDTO;
import coms309.backEnd.demo.entity.ChatMessage;
import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.repository.ChatMessageRepository;
import coms309.backEnd.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessageDTO>> getChatHistory(
            @RequestParam String user1NetId,
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


    @GetMapping("/allLatestMessages/{netId}")
    public ResponseEntity<List<ChatMessage>> getUsersYouMessagingWith(@PathVariable String netId){
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
        List<ChatMessage> latestMessages = new ArrayList<>();
        for(User userInChatList : chattedUser){
            ChatMessage chatMessage = getLatestMessageBetween2User(netId,userInChatList.getNetId()).getBody();
            latestMessages.add(chatMessage);
        }

        // Sort the message based on time
        latestMessages.sort(new Comparator<ChatMessage>() {
            @Override
            public int compare(ChatMessage chatMessage1, ChatMessage chatMessage2) {
                return chatMessage2.getTimestamp().compareTo(chatMessage1.getTimestamp());
            }
        });
        return ResponseEntity.ok(latestMessages);
    }

        @GetMapping("/getLatestMessageBetween2User")
        public ResponseEntity<ChatMessage> getLatestMessageBetween2User(@RequestParam String netIdUser1, @RequestParam String netIdUser2){

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
            ChatMessage chatMessage = chatMessages.get(chatMessages.size()-1);
            return ResponseEntity.ok(chatMessage);

        }
}
