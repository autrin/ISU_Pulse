package coms309.demo_nhat.student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Responsible for Data Access
public interface StudentRepository
        extends JpaRepository<Student, Long> {

    // SELECT * FROM student WHERE email = ? OR
    @Query("SELECT s FROM Student s WHERE s.email = ?1") // This is Jbql, not Sql
    Optional<Student> findStudentByEmail(String email);
}
