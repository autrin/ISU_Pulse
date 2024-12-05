package coms309.backEnd.demo.repository;

import coms309.backEnd.demo.entity.FriendRequest;
import coms309.backEnd.demo.entity.RequestStatus;
import coms309.backEnd.demo.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    List<FriendRequest> findAllByReceiverAndStatus(User receiver,RequestStatus status);
    List<FriendRequest> findAllBySenderAndStatus(User sender, RequestStatus status);
    Optional<FriendRequest> findFriendRequestBySenderAndReceiver(User sender, User receiver);
    @Transactional
    @Modifying
    @Query("DELETE FROM FriendRequest f WHERE f.sender = :sender AND f.receiver = :receiver")
    void deleteBySenderAndReceiver(@Param("sender") User sender, @Param("receiver") User receiver);

}
