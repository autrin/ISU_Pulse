package coms309.backEnd.demo.repository;

import coms309.backEnd.demo.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByScheduleIdInOrderByTimestampDesc(List<Long> scheduleIds);
    List<Announcement> findByScheduleIdOrderByTimestampDesc(long scheduleId);
}