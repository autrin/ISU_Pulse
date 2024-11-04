package coms309.backEnd.demo.websocket;

import coms309.backEnd.demo.entity.Announcement;
import coms309.backEnd.demo.entity.Faculty;
import coms309.backEnd.demo.entity.Schedule;
import coms309.backEnd.demo.entity.Teach;
import coms309.backEnd.demo.repository.AnnouncementRepository;
import coms309.backEnd.demo.repository.FacultyRepository;
import coms309.backEnd.demo.repository.ScheduleRepository;
import coms309.backEnd.demo.repository.TeachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class AnnouncementWebSocketController {
    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private TeachRepository teachRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/announcement.post")
    public void postAnnouncement(AnnouncementMessage message, Principal principal) {
        // Get faculty from principal
        Faculty faculty = getFacultyFromPrincipal(principal);

        // Validate schedule
        Schedule schedule = scheduleRepository.findById(message.getScheduleId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid schedule ID"));

        // Verify faculty teaches the schedule
        verifyFacultyTeachesSchedule(faculty, schedule);

        // Create and save announcement
        Announcement announcement = new Announcement(message.getContent(), schedule, faculty);
        announcementRepository.save(announcement);

        // Broadcast to students
        messagingTemplate.convertAndSend("/topic/announcements/" + schedule.getId(), announcement);
    }

    private Faculty getFacultyFromPrincipal(Principal principal) {
        // Implement logic to retrieve Faculty from authenticated user
        return facultyRepository.findByUserNetId(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("Faculty not found"));
    }

    private void verifyFacultyTeachesSchedule(Faculty faculty, Schedule schedule) {
        Teach teach = teachRepository.findByScheduleIdAndFacultyId(schedule.getId(), faculty.getId())
                .orElseThrow(() -> new IllegalArgumentException("Faculty does not teach this schedule"));
    }
}
