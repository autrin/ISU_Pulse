package coms309.backEnd.demo.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GroupMessagesDTO {
    private Long id;
    private String senderNetId;  // Sender's NetID
    private Long groupId;        // Group ID
    private String content;      // Message content
    private LocalDateTime timestamp; // Timestamp of the message
}
