package coms309.backEnd.demo.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import coms309.backEnd.demo.entity.*;
import coms309.backEnd.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AnnouncementWebSocketHandler extends TextWebSocketHandler {

    private Map<Long, WebSocketSession> studentSessions = new ConcurrentHashMap<>();
    private Map<Long, WebSocketSession> facultySessions = new ConcurrentHashMap<>();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private EnrollRepository enrollRepository;

    @Autowired
    private TeachRepository teachRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private AnnouncementRepository announcementRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Get the authenticated user
        Authentication authentication = (Authentication) session.getPrincipal();
        String username = authentication.getName();

        User user = userRepository.findUserByNetId(username).orElse(null);
        if (user == null) {
            session.close();
            return;
        }

        // Store session based on user role
        if ("FACULTY".equalsIgnoreCase(user.getUserType().toString())) {
            facultySessions.put(user.getId(), session);
        } else if ("STUDENT".equalsIgnoreCase(user.getUserType().toString())) {
            studentSessions.put(user.getId(), session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Get the authenticated user
        Authentication authentication = (Authentication) session.getPrincipal();
        String username = authentication.getName();

        User user = userRepository.findUserByNetId(username).orElse(null);
        if (user == null) {
            session.close();
            return;
        }

        String payload = message.getPayload();
        Map<String, Object> msgMap = objectMapper.readValue(payload, Map.class);
        String action = (String) msgMap.get("action");

        if ("post".equalsIgnoreCase(action) && "FACULTY".equalsIgnoreCase(user.getUserType().toString())) {
            handlePostAnnouncement(session, user, msgMap);
        }
    }

    private void handlePostAnnouncement(WebSocketSession session, User user, Map<String, Object> msgMap) throws Exception {
        Long scheduleId = Long.valueOf(msgMap.get("scheduleId").toString());
        String content = (String) msgMap.get("content");

        Faculty faculty = facultyRepository.findByUserNetId(user.getNetId()).orElse(null);
        Schedule schedule = scheduleRepository.findById(scheduleId).orElse(null);

        if (faculty == null || schedule == null) {
            sendMessage(session, "Invalid faculty or schedule");
            return;
        }

        // Check if faculty teaches the schedule
        Teach teach = teachRepository.findByScheduleIdAndFacultyId(scheduleId, faculty.getId()).orElse(null);
        if (teach == null) {
            sendMessage(session, "You do not teach this schedule");
            return;
        }

        // Create and save announcement
        Announcement announcement = new Announcement(content, schedule, faculty);
        announcementRepository.save(announcement);

        // Broadcast to students enrolled in this schedule
        broadcastAnnouncement(announcement);
    }

    private void broadcastAnnouncement(Announcement announcement) throws Exception {
        List<Enroll> enrollments = enrollRepository.findBySchedule(announcement.getSchedule());

        for (Enroll enroll : enrollments) {
            User student = enroll.getStudent();
            WebSocketSession studentSession = studentSessions.get(student.getId());
            if (studentSession != null && studentSession.isOpen()) {
                String announcementJson = objectMapper.writeValueAsString(announcement);
                sendMessage(studentSession, announcementJson);
            }
        }
    }

    private void sendMessage(WebSocketSession session, String message) throws Exception {
        session.sendMessage(new TextMessage(message));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Remove session from tracking maps
        Authentication authentication = (Authentication) session.getPrincipal();
        String username = authentication.getName();
        User user = userRepository.findUserByNetId(username).orElse(null);
        if (user != null) {
            if ("FACULTY".equalsIgnoreCase(user.getUserType().toString())) {
                facultySessions.remove(user.getId());
            } else if ("STUDENT".equalsIgnoreCase(user.getUserType().toString())) {
                studentSessions.remove(user.getId());
            }
        }
    }
}