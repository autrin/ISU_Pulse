package coms309.backEnd.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ChatSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn
    private User user;

    @ManyToOne
    @JoinColumn
    private Message message;

    public ChatSection() {
    }
}
