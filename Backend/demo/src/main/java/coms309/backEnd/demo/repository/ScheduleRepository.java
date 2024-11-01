package coms309.backEnd.demo.repository;

import coms309.backEnd.demo.entity.Profile;
import coms309.backEnd.demo.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
