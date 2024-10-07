package coms309.backEnd.demo.repository;

import coms309.backEnd.demo.entity.Course;
import coms309.backEnd.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    public List<Course> findCourseByNetId(User user);
}
