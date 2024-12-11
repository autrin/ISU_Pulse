package coms309.backEnd.demo.websocket.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import coms309.backEnd.demo.DTO.ChatMessageDTO;
import coms309.backEnd.demo.entity.ChatMessage;
import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.repository.ChatMessageRepository;
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
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketHandler.class);

    private final Map<String, WebSocketSession> activeSessions = new ConcurrentHashMap<>();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String senderNetId = getNetIdFromSession(session);
        String recipientNetId = getRecipientNetIdFromSession(session);

        if (senderNetId == null || recipientNetId == null || !userRepository.existsByNetId(senderNetId)) {
            session.close(CloseStatus.BAD_DATA.withReason("Invalid senderNetId or recipientNetId"));
            return;
        }

        activeSessions.put(senderNetId, session);
        logger.info("User {} connected to chat.", senderNetId);

        // Send chat history to the user upon connection
        sendChatHistoryToUser(session, senderNetId, recipientNetId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        ChatMessageDTO chatMessageDTO;

        try {
            chatMessageDTO = objectMapper.readValue(payload, ChatMessageDTO.class);
        } catch (JsonProcessingException e) {
            sendMessage(session, "Invalid message format.");
            return;
        }

        User sender = userRepository.findUserByNetId(chatMessageDTO.getSenderNetId()).orElse(null);
        User recipient = userRepository.findUserByNetId(chatMessageDTO.getRecipientNetId()).orElse(null);

        if (sender == null || recipient == null) {
            sendMessage(session, "Error: Invalid sender or recipient.");
            return;
        }

        // Set the timestamp for the message
        chatMessageDTO.setTimestamp(LocalDateTime.now());

        // Save the chat message to the database
        ChatMessage chatMessage = new ChatMessage(sender, recipient, chatMessageDTO.getContent());
        chatMessage.setTimestamp(chatMessageDTO.getTimestamp());  // Set timestamp before saving
        chatMessageRepository.save(chatMessage);


        // Forward the message to the recipient if they’re connected
        WebSocketSession recipientSession = activeSessions.get(chatMessageDTO.getRecipientNetId());
        if (recipientSession != null && recipientSession.isOpen()) {
            String messageJson = objectMapper.writeValueAsString(chatMessageDTO);
            sendMessage(recipientSession, messageJson);
        }
        // The server sends the message to the sender
        sendMessage(session, objectMapper.writeValueAsString(chatMessageDTO));
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String netId = getNetIdFromSession(session);
        if (netId != null) {
            activeSessions.remove(netId);
            logger.info("User {} disconnected from chat.", netId);
        }
    }

    private void sendChatHistoryToUser(WebSocketSession session, String senderNetId, String recipientNetId) {
        User sender = userRepository.findUserByNetId(senderNetId).orElse(null);
        User recipient = userRepository.findUserByNetId(recipientNetId).orElse(null);

        if (sender == null || recipient == null) {
            sendMessage(session, "Error: Invalid sender or recipient.");
            return;
        }

        // Retrieve chat history for messages exchanged between the two users
        List<ChatMessage> chatMessages = chatMessageRepository.findMessagesBetweenUsers(senderNetId, recipientNetId);

        // Convert ChatMessage entities to ChatMessageDTOs
        List<ChatMessageDTO> chatHistory = new ArrayList<>();
        for (ChatMessage message : chatMessages) {
            ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
            chatMessageDTO.setSenderNetId(message.getSender().getNetId());
            chatMessageDTO.setRecipientNetId(message.getRecipient().getNetId());
            chatMessageDTO.setContent(message.getContent());
            chatMessageDTO.setTimestamp(message.getTimestamp());
            chatHistory.add(chatMessageDTO);
        }

        // Send the chat history as a single JSON array message
        String chatHistoryJson;
        try {
            chatHistoryJson = objectMapper.writeValueAsString(chatHistory);
            sendMessage(session, chatHistoryJson);
        } catch (JsonProcessingException e) {
            sendMessage(session, "Error: Failed to retrieve chat history.");
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

    private String getNetIdFromSession(WebSocketSession session) {
        return getQueryParam(session, "netId");
    }

    private String getRecipientNetIdFromSession(WebSocketSession session) {
        return getQueryParam(session, "recipientNetId");
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
}
