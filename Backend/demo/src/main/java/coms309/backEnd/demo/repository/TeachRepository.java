package coms309.backEnd.demo.repository;

import coms309.backEnd.demo.entity.Schedule;
import coms309.backEnd.demo.entity.Teach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeachRepository extends JpaRepository<Teach, Long> {
    @Query("SELECT t.schedule FROM Teach t WHERE t.faculty.id = :facultyId")
    public List<Schedule> findSchedulesByFacultyId(long facultyId);

    @Query("SELECT t FROM Teach t WHERE t.schedule.id = :scheduleId AND t.faculty.id = :facultyId")
    public Optional<Teach> findByScheduleIdAndFacultyId(@Param("scheduleId") long scheduleId, @Param("facultyId") long facultyId);}
