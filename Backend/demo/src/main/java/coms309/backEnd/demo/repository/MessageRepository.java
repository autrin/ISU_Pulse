package coms309.backEnd.demo.repository;

import coms309.backEnd.demo.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message,Long> {
}
