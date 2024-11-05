package coms309.backEnd.demo.repository;

import coms309.backEnd.demo.entity.FriendRequest;
import coms309.backEnd.demo.entity.RequestStatus;
import coms309.backEnd.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    List<FriendRequest> findAllByReceiverAndStatus(User receiver,RequestStatus status);
    List<FriendRequest> findAllBySenderAndStatus(User sender, RequestStatus status);
    Optional<FriendRequest> findFriendRequestBySenderAndReceiver(User sender, User receiver);
}
