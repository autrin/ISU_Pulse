package coms309.backEnd.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class FriendShip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    //@JsonIgnore
    @JoinColumn
    private User user1;

    @ManyToOne
    //@JsonIgnore
    @JoinColumn
    private User user2;

    public FriendShip(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
    }

    public FriendShip() {
    }
}
