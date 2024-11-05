package coms309.backEnd.demo.repository;

import coms309.backEnd.demo.entity.Enroll;
import coms309.backEnd.demo.entity.Schedule;
import coms309.backEnd.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EnrollRepository extends JpaRepository<Enroll, Long> {
//    public List<Enroll> findAllByStudentid(String sId);
//    public Enroll findByStudentidAndCourseid(@Param("studentid") String sId, @Param("courseid") int courseId);
//    public List<Enroll> findAllByStudent(User user);
//   public Schedule findByEnroll(Enroll enroll);
    @Query("SELECT e.student FROM Enroll e WHERE e.schedule.id = :scheduleId")
    public List<User> findStudentsBySchedule(long scheduleId);

    public List<Enroll> findBySchedule(Schedule schedule);
}
