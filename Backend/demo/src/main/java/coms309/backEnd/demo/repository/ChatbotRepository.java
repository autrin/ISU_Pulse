package coms309.backEnd.demo.repository;

import coms309.backEnd.demo.entity.Chatbot;
import coms309.backEnd.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatbotRepository extends JpaRepository<Chatbot, Long> {
    /**
     * Retrieves the top N chat messages associated with a specific user, ordered by timestamp descending.
     *
     * @param user     The user whose chat messages are to be retrieved.
     * @return List of Chatbot messages.
     */
    List<Chatbot> findTop5ByUserOrderByTimestampDesc(User user);

    /**
     * Retrieves all chat messages associated with a specific user, ordered by timestamp ascending.
     *
     * @param user The user whose chat history is to be retrieved.
     * @return List of Chatbot messages.
     */
    List<Chatbot> findByUserOrderByTimestampAsc(User user);
}