package coms309.backEnd.demo.repository;

import coms309.backEnd.demo.entity.Course;
import coms309.backEnd.demo.entity.Schedule;
import coms309.backEnd.demo.entity.Task;
import coms309.backEnd.demo.entity.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task,Long> {
//    public List<Task> findAllByCourse(Course course);
//    public List<Task> findAllByTaskType(TaskType taskType);
    public List<Task> findAllBySchedule(Schedule schedule);
}
