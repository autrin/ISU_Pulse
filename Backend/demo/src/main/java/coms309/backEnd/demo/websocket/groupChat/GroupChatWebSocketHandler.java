package coms309.backEnd.demo.websocket.groupChat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import coms309.backEnd.demo.DTO.GroupMessagesDTO;
import coms309.backEnd.demo.entity.GroupMessages;
import coms309.backEnd.demo.entity.Group;
import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.repository.GroupMessagesRepository;
import coms309.backEnd.demo.repository.GroupRepository;
import coms309.backEnd.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GroupChatWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(GroupChatWebSocketHandler.class);

    private final Map<Long, List<WebSocketSession>> groupSessions = new ConcurrentHashMap<>();
    @Autowired
    private final GroupRepository groupRepository;
    @Autowired
    private final GroupMessagesRepository groupMessagesRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final ObjectMapper objectMapper;

    public GroupChatWebSocketHandler(GroupRepository groupRepository, GroupMessagesRepository groupMessagesRepository,
                                     UserRepository userRepository, ObjectMapper objectMapper) {
        this.groupRepository = groupRepository;
        this.groupMessagesRepository = groupMessagesRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long groupId = getGroupIdFromSession(session);
        String senderNetId = getNetIdFromSession(session);

        if (groupId == null || senderNetId == null || !userRepository.existsByNetId(senderNetId)) {
            session.close(CloseStatus.BAD_DATA.withReason("Invalid groupId or senderNetId"));
            return;
        }

        // Add the session to the group
        groupSessions.computeIfAbsent(groupId, k -> new ArrayList<>()).add(session);
        logger.info("User {} connected to group {}", senderNetId, groupId);

        // Send chat history to the user upon connection
        sendChatHistoryToUser(session, groupId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        GroupMessagesDTO groupMessagesDTO;

        try {
            // Deserialize the incoming message payload
            groupMessagesDTO = objectMapper.readValue(payload, GroupMessagesDTO.class);
        } catch (JsonProcessingException e) {
            sendMessage(session, "Invalid message format.");
            return;
        }

        // Validate sender and group
        User sender = userRepository.findUserByNetId(groupMessagesDTO.getSenderNetId()).orElse(null);
        Group group = groupRepository.findById(groupMessagesDTO.getGroupId()).orElse(null);

        if (sender == null || group == null) {
            sendMessage(session, "Error: Invalid sender or group.");
            return;
        }

        // Set the timestamp for the message
        groupMessagesDTO.setTimestamp(LocalDateTime.now());

        // Save the group message to the database
        GroupMessages groupMessage = new GroupMessages(sender, group, groupMessagesDTO.getContent());
        groupMessage.setTimestamp(groupMessagesDTO.getTimestamp());  // Set timestamp before saving
        groupMessagesRepository.save(groupMessage);

        // Broadcast the message to all group members
        groupMessagesDTO.setId(groupMessage.getId());
        String messageJson = objectMapper.writeValueAsString(groupMessagesDTO);
        broadcastToGroup(groupMessagesDTO.getGroupId(), messageJson);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long groupId = getGroupIdFromSession(session);

        if (groupId != null) {
            groupSessions.getOrDefault(groupId, Collections.emptyList()).remove(session);
            logger.info("User disconnected from group {}", groupId);
        }
    }

    private void sendChatHistoryToUser(WebSocketSession session, Long groupId) {
        Group group = groupRepository.findById(groupId).orElse(null);

        if (group == null) {
            sendMessage(session, "Error: Invalid group.");
            return;
        }

        // Retrieve chat history for the group
        List<GroupMessages> groupMessages = groupMessagesRepository.findByGroupId(groupId);

        // Convert GroupMessages entities to GroupMessagesDTOs
        List<GroupMessagesDTO> chatHistory = new ArrayList<>();
        for (GroupMessages message : groupMessages) {
            GroupMessagesDTO groupMessagesDTO = new GroupMessagesDTO();
            groupMessagesDTO.setId(message.getId());
            //groupMessagesDTO.setSenderNetId(message.getSender().getNetId());
            // The value of sender cna be null therefor, it will need the ternary conditional operator to handle this situation
            groupMessagesDTO.setSenderNetId(message.getSender() != null ? message.getSender().getNetId() : null);
            groupMessagesDTO.setGroupId(message.getGroup().getId());
            groupMessagesDTO.setContent(message.getContent());
            groupMessagesDTO.setTimestamp(message.getTimestamp());
            chatHistory.add(groupMessagesDTO);
        }

        // Send the chat history as a single JSON array message
        try {
            String chatHistoryJson = objectMapper.writeValueAsString(chatHistory);
            sendMessage(session, chatHistoryJson);
        } catch (JsonProcessingException e) {
            sendMessage(session, "Error: Failed to retrieve chat history.");
        }
    }

    private void broadcastToGroup(Long groupId, String message) {
        List<WebSocketSession> sessions = groupSessions.getOrDefault(groupId, Collections.emptyList());
        for (WebSocketSession session : sessions) {
            sendMessage(session, message);
        }
    }

    private void sendMessage(WebSocketSession session, String message) {
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            }
        } catch (Exception e) {
            logger.error("Failed to send message: {}", e.getMessage());
        }
    }

    private Long getGroupIdFromSession(WebSocketSession session) {
        return getQueryParamAsLong(session, "groupId");
    }

    private String getNetIdFromSession(WebSocketSession session) {
        return getQueryParam(session, "netId");
    }

    private String getQueryParam(WebSocketSession session, String paramName) {
        URI uri = session.getUri();
        if (uri != null && uri.getQuery() != null) {
            String[] pairs = uri.getQuery().split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2 && keyValue[0].equals(paramName)) {
                    return keyValue[1];
                }
            }
        }
        return null;
    }

    private Long getQueryParamAsLong(WebSocketSession session, String paramName) {
        String paramValue = getQueryParam(session, paramName);
        if (paramValue != null) {
            try {
                return Long.parseLong(paramValue);
            } catch (NumberFormatException e) {
                logger.error("Invalid parameter value for {}: {}", paramName, paramValue);
            }
        }
        return null;
    }
}
