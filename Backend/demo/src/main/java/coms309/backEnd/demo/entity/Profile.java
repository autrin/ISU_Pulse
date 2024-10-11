package coms309.backEnd.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Profile {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn
    private User user;
}
