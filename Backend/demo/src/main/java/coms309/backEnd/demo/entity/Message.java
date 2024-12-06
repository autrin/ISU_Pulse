package coms309.backEnd.demo.entity;

import java.time.LocalDateTime;

public interface Message {
    Long getId();
    String getContent();
    LocalDateTime getTimestamp();
}
