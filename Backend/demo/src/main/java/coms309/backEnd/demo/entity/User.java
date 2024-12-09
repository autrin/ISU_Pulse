package coms309.backEnd.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String netId;

    private String firstName;

    private String lastName;

    private String email;

    private String hashedPassword;
    private String profilePictureUrl = "https://as1.ftcdn.net/v2/jpg/01/78/33/12/1000_F_178331249_PIVD6lideletB8pUGKaRy1Z3L3N2YE9n.jpg";

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnore
    private List<PersonalTask> personalTaskList;

    @OneToMany(mappedBy = "student", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnore
    private List<Enroll> enrollList;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Profile profile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Faculty faculty;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<FriendRequest> sentRequests;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<FriendRequest> receivedRequests;

    @OneToMany(mappedBy = "user1", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<FriendShip> friendshipsAsUser1;

    @OneToMany(mappedBy = "user2", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<FriendShip> friendshipsAsUser2;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ChatMessage> sentMessages;

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ChatMessage> receivedMessages;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Join> joins;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<GroupMessages> groupMessagesSent;

    // This is the groups that this person created
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Group> groupsCreated;

    // Add fields for OTP-based authentication
    @JsonIgnore
    private String otp; // Temporary storage for OTP


    // 0 for false and 1 for true
    @JsonIgnore
    private boolean verified; // To track if the user is verified


    public User() {
    }

    public User(String netId, String firstName, String lastName, String email, String hashedPassword, UserType userType) {
        this.netId = netId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.userType = userType;
    }

    /**
     * This function is used to return a list of friendships that one user has
     * @return a list of friendships that one user has
     */
    @JsonIgnore
    public List<FriendShip> getFriendShips(){
        List<FriendShip> friendShips = new ArrayList<>();
        friendShips.addAll(friendshipsAsUser1);
        friendShips.addAll(friendshipsAsUser2);
        return friendShips;
    }
}