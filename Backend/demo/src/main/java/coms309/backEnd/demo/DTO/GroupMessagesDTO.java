package coms309.backEnd.demo.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GroupMessagesDTO {
    private Long id;
    private String senderNetId;  // Sender's NetID
    private String senderName;   // Sender's full name (Optional, for better UI representation)
    private Long groupId;        // Group ID
    private String groupName;    // Group name (Optional, for better UI representation)
    private String content;      // Message content
    private LocalDateTime timestamp; // Timestamp of the message
}
