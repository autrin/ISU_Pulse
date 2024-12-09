package coms309.backEnd.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Chatbot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * The user associated with this message.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    @JsonIgnore
    private User user;

    /**
     * The sender of the message: "USER" or "CHATBOT".
     */
    @Column(nullable = false)
    private String sender;

    /**
     * The content of the message.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    /**
     * Timestamp when the message was sent.
     */
    @Column(nullable = false)
    private LocalDateTime timestamp;

    public Chatbot() {
        // Default constructor
    }
}
