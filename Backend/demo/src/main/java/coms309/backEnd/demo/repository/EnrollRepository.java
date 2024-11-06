package coms309.backEnd.demo.repository;

import coms309.backEnd.demo.entity.Enroll;
import coms309.backEnd.demo.entity.Schedule;
import coms309.backEnd.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EnrollRepository extends JpaRepository<Enroll, Long> {
    @Query("SELECT e.student FROM Enroll e WHERE e.schedule.id = :scheduleId")
    public List<User> findStudentsBySchedule(long scheduleId);
    public List<Enroll> findBySchedule(Schedule schedule);
    public List<Enroll> findByStudent(User user);
}
