package coms309.backEnd.demo.repository;

import coms309.backEnd.demo.entity.GroupMessages;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupMessagesRepository extends JpaRepository<GroupMessages, Long> {
    List<GroupMessages> findByGroupId(Long groupId);
}
