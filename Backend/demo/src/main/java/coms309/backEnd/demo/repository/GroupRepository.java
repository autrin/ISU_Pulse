package coms309.backEnd.demo.repository;

import coms309.backEnd.demo.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GroupRepository  extends JpaRepository<Group, Long> {
    @Query(value = "SELECT * FROM user_group g WHERE g.creator_id = :userId ORDER BY g.timestamp DESC LIMIT 1", nativeQuery = true)
    Optional<Group> findLatestGroupByUser(@Param("userId") Long userId);

}
