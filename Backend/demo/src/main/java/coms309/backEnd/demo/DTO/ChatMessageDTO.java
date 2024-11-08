package coms309.backEnd.demo.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageDTO {
    private String senderNetId;
    private String recipientNetId;
    private String content;
    private LocalDateTime timestamp;
}
