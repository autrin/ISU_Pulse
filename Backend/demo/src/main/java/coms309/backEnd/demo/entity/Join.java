package coms309.backEnd.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "group_user_join")
public class Join {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Group group;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    public Join() {
    }

    public Join(Group group, User user) {
        this.group = group;
        this.user = user;
    }
}
