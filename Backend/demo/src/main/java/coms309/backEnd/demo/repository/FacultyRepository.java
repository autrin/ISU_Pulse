package coms309.backEnd.demo.repository;

import coms309.backEnd.demo.entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {

}
