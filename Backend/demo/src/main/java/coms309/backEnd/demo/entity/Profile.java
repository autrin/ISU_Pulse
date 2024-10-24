package coms309.backEnd.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn
    @JsonIgnore
    private User user;

    private String linkedinUrl = "";
    private String externalUrl = "";
    private String description = "";

    public Profile() {
    }

    public Profile(Long id, User user, String linkedinUrl, String externalUrl, String description) {
        this.id = id;
        this.user = user;
        this.linkedinUrl = linkedinUrl;
        this.externalUrl = externalUrl;
        this.description = description;
    }
}
