package coms309.backEnd.demo.repository;

import coms309.backEnd.demo.entity.Course;
import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.entity.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
public interface UserRepository extends JpaRepository<User, Long> {
   // public List<Course> findCourseByNetId(User user);
    @Query("SELECT u FROM User u WHERE u.netId = ?1")
    public Optional<User> findUserByNetId(String netId);
    public boolean existsByNetId(String netId);
    public Optional<List<User>> findAllUserByUserType(UserType userType);
    @Query("SELECT u FROM User u WHERE u.id <> :userId AND u.id NOT IN " +
            "(SELECT f.user2.id FROM FriendShip f WHERE f.user1.id = :userId " +
            " UNION " +
            "SELECT f.user1.id FROM FriendShip f WHERE f.user2.id = :userId)")
    public List<User> findUsersNotFriendsWith(Long userId);

  @Query("SELECT u FROM User u WHERE LOWER(u.firstName) = LOWER(:name) OR LOWER(u.lastName) = LOWER(:name)")
  public List<User> findByFirstNameOrLastNameIgnoreCase(@Param("name") String name);

    Optional<User> findByEmail(String email);

}
