package coms309.backEnd.demo.repository;

import coms309.backEnd.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    public User findByNetId(String netId);
}
