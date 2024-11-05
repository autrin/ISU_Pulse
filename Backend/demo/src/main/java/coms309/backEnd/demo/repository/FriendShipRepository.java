package coms309.backEnd.demo.repository;

import coms309.backEnd.demo.entity.FriendShip;
import coms309.backEnd.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FriendShipRepository extends JpaRepository<FriendShip,Long> {
    @Query("SELECT f FROM FriendShip f WHERE (f.user1 = :user1 AND f.user2 = :user2) OR (f.user1 = :user2 AND f.user2 = :user1)")
    public Optional<FriendShip> findFriendShipBetweenUsers(User user1, User user2);
}
