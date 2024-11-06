package coms309.backEnd.demo.websocket.announcement;

import com.fasterxml.jackson.databind.ObjectMapper;
import coms309.backEnd.demo.DTO.AnnouncementDTO;
import coms309.backEnd.demo.entity.*;
import coms309.backEnd.demo.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AnnouncementWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(AnnouncementWebSocketHandler.class);

    // Maps to keep track of student and faculty sessions
    private final Map<Long, Set<WebSocketSession>> studentSessions = new ConcurrentHashMap<>();
    private final Map<Long, Set<WebSocketSession>> facultySessions = new ConcurrentHashMap<>();

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

    @Autowired
    private ObjectMapper objectMapper; // Inject the auto-configured ObjectMapper

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        URI uri = session.getUri();
        if (uri == null) {
            session.close(CloseStatus.BAD_DATA.withReason("No URI in session"));
            return;
        }

        String query = uri.getQuery();
        if (query == null) {
            session.close(CloseStatus.BAD_DATA.withReason("No query parameters in URI"));
            return;
        }

        Map<String, String> params = getQueryParams(query);
        String netId = params.get("netId");
        String userType = params.get("userType");

        if (netId == null || userType == null) {
            session.close(CloseStatus.BAD_DATA.withReason("Missing netId or userType"));
            return;
        }

        User user = userRepository.findUserByNetId(netId).orElse(null);
        if (user == null) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("User not found"));
            return;
        }

        // Verify userType matches using Enum
        UserType enumUserType;
        try {
            enumUserType = UserType.valueOf(userType.toUpperCase());
            if (!user.getUserType().equals(enumUserType)) {
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("UserType mismatch"));
                return;
            }
        } catch (IllegalArgumentException e) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Invalid userType value"));
            return;
        }

        // Store session based on userType
        if (enumUserType == UserType.FACULTY) {
            facultySessions.computeIfAbsent(user.getId(), k -> ConcurrentHashMap.newKeySet()).add(session);
            logger.info("Faculty user connected: {}", user.getNetId());
        } else if (enumUserType == UserType.STUDENT) {
            studentSessions.computeIfAbsent(user.getId(), k -> ConcurrentHashMap.newKeySet()).add(session);
            logger.info("Student user connected: {}", user.getNetId());

            // Send announcement history to the student upon connection
            sendAnnouncementHistoryToStudent(session, user);
        } else {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Invalid userType"));
            return;
        }

        // Store user in session attributes for quick access
        session.getAttributes().put("user", user);

        // Optionally, send a welcome message
        sendMessage(session, "Welcome " + user.getNetId() + "!");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Retrieve user from session attributes
        User user = (User) session.getAttributes().get("user");
        if (user == null) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("User not found in session"));
            return;
        }

        String userType = user.getUserType().toString();

        String payload = message.getPayload();
        Map<String, Object> msgMap;
        try {
            msgMap = objectMapper.readValue(payload, Map.class);
        } catch (Exception e) {
            sendMessage(session, "Invalid message format.");
            return;
        }

        String action = (String) msgMap.get("action");

        if ("post".equalsIgnoreCase(action) && user.getUserType() == UserType.FACULTY) {
            handlePostAnnouncement(session, user, msgMap);
        } else if ("update".equalsIgnoreCase(action) && user.getUserType() == UserType.FACULTY) {
            handleUpdateAnnouncement(session, user, msgMap);
        } else if ("delete".equalsIgnoreCase(action) && user.getUserType() == UserType.FACULTY) {
            handleDeleteAnnouncement(session, user, msgMap);
        } else {
            sendMessage(session, "Invalid action or insufficient permissions.");
        }
    }

    private void handlePostAnnouncement(WebSocketSession session, User user, Map<String, Object> msgMap) throws Exception {
        Long scheduleId;
        String content;

        try {
            scheduleId = Long.valueOf(msgMap.get("scheduleId").toString());
            content = (String) msgMap.get("content");
        } catch (Exception e) {
            sendMessage(session, "Invalid message format.");
            return;
        }

        Faculty faculty = facultyRepository.findByUserNetId(user.getNetId()).orElse(null);
        Schedule schedule = scheduleRepository.findById(scheduleId).orElse(null);

        if (faculty == null || schedule == null) {
            sendMessage(session, "Invalid faculty or schedule.");
            return;
        }

        // Check if faculty teaches the schedule
        Teach teach = teachRepository.findByScheduleIdAndFacultyId(scheduleId, faculty.getId()).orElse(null);
        if (teach == null) {
            sendMessage(session, "You do not teach this schedule.");
            return;
        }

        // Create and save announcement
        Announcement announcement = new Announcement(content, schedule, faculty);
        announcementRepository.save(announcement);

        // Broadcast to students enrolled in this schedule
        broadcastAnnouncement(announcement);

        // Send confirmation to the faculty
        sendMessage(session, "Announcement posted successfully.");
    }

    private void handleUpdateAnnouncement(WebSocketSession session, User user, Map<String, Object> msgMap) throws Exception {
        Long announcementId;
        String newContent;

        try {
            announcementId = Long.valueOf(msgMap.get("announcementId").toString());
            newContent = (String) msgMap.get("content");
        } catch (Exception e) {
            sendMessage(session, "Invalid message format.");
            return;
        }

        Announcement announcement = announcementRepository.findById(announcementId).orElse(null);
        if (announcement == null) {
            sendMessage(session, "Announcement not found.");
            return;
        }

        // Check if the faculty owns the announcement
        Faculty faculty = facultyRepository.findByUserNetId(user.getNetId()).orElse(null);
        if (faculty == null || announcement.getFaculty().getId() != faculty.getId()) {
            sendMessage(session, "You do not have permission to update this announcement.");
            return;
        }

        // Update and save the announcement
        announcement.setContent(newContent);
        announcement.setTimestamp(LocalDateTime.now()); // Reset the timestamp to current time
        announcementRepository.save(announcement);

        // Optionally, broadcast the updated announcement to students
        broadcastAnnouncement(announcement);

        // Send confirmation to the faculty
        sendMessage(session, "Announcement updated successfully.");
    }

    private void handleDeleteAnnouncement(WebSocketSession session, User user, Map<String, Object> msgMap) throws Exception {
        Long announcementId;

        try {
            announcementId = Long.valueOf(msgMap.get("announcementId").toString());
        } catch (Exception e) {
            sendMessage(session, "Invalid message format.");
            return;
        }

        Announcement announcement = announcementRepository.findById(announcementId).orElse(null);
        if (announcement == null) {
            sendMessage(session, "Announcement not found.");
            return;
        }

        // Check if the faculty owns the announcement
        Faculty faculty = facultyRepository.findByUserNetId(user.getNetId()).orElse(null);
        if (faculty == null || announcement.getFaculty().getId() != faculty.getId()) {
            sendMessage(session, "You do not have permission to delete this announcement.");
            return;
        }

        // Get the schedule before deletion
        Schedule schedule = announcement.getSchedule();

        // Delete the announcement
        announcementRepository.delete(announcement);

        // Notify students about the deletion
        broadcastAnnouncementDeletion(announcement);

        // Send confirmation to the faculty
        sendMessage(session, "Announcement deleted successfully.");
    }

    private void broadcastAnnouncement(Announcement announcement) {
        List<Enroll> enrollments = enrollRepository.findBySchedule(announcement.getSchedule());

        AnnouncementDTO announcementDTO = new AnnouncementDTO();
        announcementDTO.setId(announcement.getId());
        announcementDTO.setContent(announcement.getContent());
        announcementDTO.setTimestamp(announcement.getTimestamp());
        announcementDTO.setScheduleId(announcement.getSchedule().getId());
        announcementDTO.setFacultyNetId(announcement.getFaculty().getUser().getNetId());

        // Create a parent object with the action
        Map<String, Object> newAnnouncementMessage = new HashMap<>();
        newAnnouncementMessage.put("action", "new");
        newAnnouncementMessage.put("announcement", announcementDTO);

        String announcementJson;
        try {
            announcementJson = objectMapper.writeValueAsString(newAnnouncementMessage);
        } catch (Exception e) {
            logger.error("Failed to serialize announcement: {}", e.getMessage());
            return; // Exit the method if serialization fails
        }

        for (Enroll enroll : enrollments) {
            User student = enroll.getStudent();
            Set<WebSocketSession> sessions = studentSessions.get(student.getId());
            if (sessions != null) {
                for (WebSocketSession studentSession : sessions) {
                    if (studentSession.isOpen()) {
                        try {
                            sendMessage(studentSession, announcementJson);
                            logger.info("Announcement sent to student {}.", student.getNetId());
                        } catch (Exception e) {
                            logger.error("Failed to send announcement to student {}: {}", student.getNetId(), e.getMessage());
                            // Optionally, handle the failed session (e.g., remove it from the set)
                        }
                    }
                }
            }
        }
    }

    private void broadcastAnnouncementDeletion(Announcement announcement) {
        List<Enroll> enrollments = enrollRepository.findBySchedule(announcement.getSchedule());

        for (Enroll enroll : enrollments) {
            User student = enroll.getStudent();
            Set<WebSocketSession> sessions = studentSessions.get(student.getId());
            if (sessions != null) {
                for (WebSocketSession studentSession : sessions) {
                    if (studentSession.isOpen()) {
                        try {
                            // Send updated announcement history to the student
                            sendAnnouncementHistoryToStudent(studentSession, student);
                            logger.info("Updated announcement history sent to student {}.", student.getNetId());
                        } catch (Exception e) {
                            logger.error("Failed to send updated history to student {}: {}", student.getNetId(), e.getMessage());
                        }
                    }
                }
            }
        }
    }

    private void sendMessage(WebSocketSession session, String message) throws Exception {
        if (session.isOpen()) {
            session.sendMessage(new TextMessage(message));
            logger.debug("Message sent to session {}: {}", session.getId(), message);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Remove session from tracking maps
        URI uri = session.getUri();
        if (uri != null) {
            String query = uri.getQuery();
            if (query != null) {
                Map<String, String> params = getQueryParams(query);
                String netId = params.get("netId");
                String userType = params.get("userType");

                if (netId != null && userType != null) {
                    User user = userRepository.findUserByNetId(netId).orElse(null);
                    if (user != null) {
                        if ("FACULTY".equalsIgnoreCase(userType)) {
                            Set<WebSocketSession> sessions = facultySessions.get(user.getId());
                            if (sessions != null) {
                                sessions.remove(session);
                                if (sessions.isEmpty()) {
                                    facultySessions.remove(user.getId());
                                }
                            }
                            logger.info("Faculty user disconnected: {}", user.getNetId());
                        } else if ("STUDENT".equalsIgnoreCase(userType)) {
                            Set<WebSocketSession> sessions = studentSessions.get(user.getId());
                            if (sessions != null) {
                                sessions.remove(session);
                                if (sessions.isEmpty()) {
                                    studentSessions.remove(user.getId());
                                }
                            }
                            logger.info("Student user disconnected: {}", user.getNetId());
                        }
                    }
                }
            }
        }
    }

    // Utility method to parse query parameters
    private Map<String, String> getQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0 && pair.length() > idx + 1) {
                String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                params.put(key, value);
            }
        }
        return params;
    }

    private void sendAnnouncementHistoryToStudent(WebSocketSession session, User user) throws Exception {
        // Retrieve all schedules the student is enrolled in
        List<Enroll> enrollments = enrollRepository.findByStudent(user);

        Set<Long> scheduleIds = new HashSet<>();
        for (Enroll enroll : enrollments) {
            scheduleIds.add(enroll.getSchedule().getId());
        }

        if (scheduleIds.isEmpty()) {
            // No schedules enrolled, no announcements to send
            return;
        }

        // Retrieve all announcements for these schedules, sorted by timestamp descending
        List<Announcement> announcements = announcementRepository.findByScheduleIdInOrderByTimestampDesc(new ArrayList<>(scheduleIds));

        // Collect all AnnouncementDTOs into a list
        List<AnnouncementDTO> announcementDTOList = new ArrayList<>();
        for (Announcement announcement : announcements) {
            AnnouncementDTO announcementDTO = new AnnouncementDTO();
            announcementDTO.setId(announcement.getId());
            announcementDTO.setContent(announcement.getContent());
            announcementDTO.setTimestamp(announcement.getTimestamp());
            announcementDTO.setScheduleId(announcement.getSchedule().getId());
            announcementDTO.setFacultyNetId(announcement.getFaculty().getUser().getNetId());

            announcementDTOList.add(announcementDTO);
        }

        // Create a wrapper object to indicate the type of message
        Map<String, Object> historyMessage = new HashMap<>();
        historyMessage.put("action", "history");
        historyMessage.put("announcements", announcementDTOList);

        // Serialize the history message to JSON
        String historyJson;
        try {
            historyJson = objectMapper.writeValueAsString(historyMessage);
        } catch (Exception e) {
            logger.error("Failed to serialize history message: {}", e.getMessage());
            return; // Exit the method if serialization fails
        }

        // Send the single history message to the student
        sendMessage(session, historyJson);
        logger.info("Historical announcements sent to student {}.", user.getNetId());
    }
}