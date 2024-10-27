package coms309.backEnd.demo.entity;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn
    private User sender;

    @ManyToOne
    @JoinColumn
    private User receiver;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    public FriendRequest() {}

    public FriendRequest(User sender, User receiver, RequestStatus status) {
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
    }






}
