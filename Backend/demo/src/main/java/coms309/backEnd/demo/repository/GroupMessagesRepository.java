package coms309.backEnd.demo.repository;

import coms309.backEnd.demo.entity.GroupMessages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GroupMessagesRepository extends JpaRepository<GroupMessages, Long> {
    List<GroupMessages> findByGroupId(Long groupId);
    @Query("SELECT gm FROM GroupMessages gm WHERE gm.group.id = :groupId ORDER BY gm.timestamp DESC")
    Optional<GroupMessages> findLatestMessageByGroupId(Long groupId);
}
