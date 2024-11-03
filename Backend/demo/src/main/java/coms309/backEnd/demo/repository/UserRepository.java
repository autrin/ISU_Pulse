package coms309.backEnd.demo.repository;

import coms309.backEnd.demo.entity.Course;
import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.entity.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
   // public List<Course> findCourseByNetId(User user);
    @Query("SELECT u FROM User u WHERE u.netId = ?1")
    public Optional<User> findUserByNetId(String netId);
    public boolean existsByNetId(String netId);
    public Optional<List<User>> findAllUserByUserType(UserType userType);
}
