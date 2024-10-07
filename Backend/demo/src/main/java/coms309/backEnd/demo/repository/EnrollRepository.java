package coms309.backEnd.demo.repository;

import coms309.backEnd.demo.entity.Course;
import coms309.backEnd.demo.entity.Enroll;
import coms309.backEnd.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;

public interface EnrollRepository extends JpaRepository<Enroll, String> {
    public List<Enroll> findAllBysId(String sId);
}
