package coms309.backEnd.demo.repository;

import coms309.backEnd.demo.entity.Profile;
import coms309.backEnd.demo.entity.Schedule;
import coms309.backEnd.demo.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
