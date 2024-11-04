package coms309.backEnd.demo.controller;

import coms309.backEnd.demo.entity.Announcement;
import coms309.backEnd.demo.repository.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/announcements")
public class AnnouncementController {
    @Autowired
    private AnnouncementRepository announcementRepository;

    public AnnouncementController(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    @GetMapping("/schedule/{scheduleId}")
    public ResponseEntity<List<Announcement>> getAnnouncements(@PathVariable long scheduleId) {
        List<Announcement> announcements = announcementRepository.findByScheduleIdOrderByTimestampDesc(scheduleId);
        return ResponseEntity.ok(announcements);
    }
}
