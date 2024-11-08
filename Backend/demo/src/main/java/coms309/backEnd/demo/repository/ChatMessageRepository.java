package coms309.backEnd.demo.repository;

import coms309.backEnd.demo.entity.ChatMessage;
import coms309.backEnd.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query("SELECT m FROM ChatMessage m WHERE " +
            "(m.sender.netId = :user1NetId AND m.recipient.netId = :user2NetId) " +
            "OR (m.sender.netId = :user2NetId AND m.recipient.netId = :user1NetId) " +
            "ORDER BY m.timestamp")
    List<ChatMessage> findMessagesBetweenUsers(
            @Param("user1NetId") String user1NetId,
            @Param("user2NetId") String user2NetId);

    @Query("SELECT DISTINCT m.sender FROM ChatMessage m WHERE m.recipient.netId = :netId")
    List<User> findDistinctSenders(@Param("netId") String netId);

    @Query("SELECT DISTINCT m.recipient FROM ChatMessage m WHERE m.sender.netId = :netId")
    List<User> findDistinctRecipients(@Param("netId") String netId);




}
