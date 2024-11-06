package coms309.backEnd.demo.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnnouncementDTO {
    private long id;
    private String content;
    private LocalDateTime timestamp;
    private long scheduleId;
    private String facultyNetId;
}