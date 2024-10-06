package coms309.backEnd.demo.repository;

import coms309.backEnd.demo.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    //public Course findBy()
}
