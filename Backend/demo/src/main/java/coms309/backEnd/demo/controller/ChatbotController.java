package coms309.backEnd.demo.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import coms309.backEnd.demo.entity.Chatbot;
import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.repository.ChatbotRepository;
import coms309.backEnd.demo.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringEscapeUtils.escapeJson;

@RestController
@RequestMapping("/chatbot")
@Tag(name = "Chatbot Controller", description = "Endpoints for chatbot interactions")
public class ChatbotController {

    // For logging
    private static final Logger logger = LoggerFactory.getLogger(ChatbotController.class);

    @Autowired
    private ChatbotRepository chatbotRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${openai.model}")
    private String openaiModel;

    @Value("${openai.api.url}")
    private String openaiApiUrl;

    @Value("${openai.api.key}")
    private String openaiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ChatbotController(ChatbotRepository chatbotRepository) {
        this.chatbotRepository = chatbotRepository;
    }

    /**
     * Endpoint to send a message to the chatbot.
     *
     * @param netId  NETID of the user sending the message.
     * @param message The user's message.
     * @return The chatbot's response.
     */
    @Operation(summary = "Send a message to the chatbot", description = "Allows a user to send a message and receive a chatbot response.")
    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(
            @RequestParam String netId,
            @RequestParam String message
    ) {
        try {
            // Validate user by netId
            Optional<User> userOpt = userRepository.findUserByNetId(netId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Error: User not found with NETID: " + netId);
            }
            User user = userOpt.get();

            // Save user's message
            Chatbot userMessage = new Chatbot();
            userMessage.setUser(user);
            userMessage.setSender("USER");
            userMessage.setMessage(message);
            userMessage.setTimestamp(LocalDateTime.now());
            chatbotRepository.save(userMessage);

            // Retrieve the last 4 messages to maintain a total of 5 (including the new message)
            List<Chatbot> recentMessagesDesc = chatbotRepository.findTop5ByUserOrderByTimestampDesc(user);
            // Reverse to chronological order (oldest to newest)
            Collections.reverse(recentMessagesDesc);

            // Prepare messages for OpenAI API
            // Convert Chatbot entities to OpenAI's message format
            // System prompt is included first
            String systemPrompt = "You are an intelligent and friendly assistant designed to help students at Iowa State University. Your primary goal is to provide accurate, clear, and helpful information while ensuring that the responses are well-validated and relevant to the user's needs. " +
                    "If a student asks about courses, thoroughly check their relevance to Iowa State University's offerings, and provide precise and meaningful recommendations. " +
                    "If the student seeks help with general queries, academic resources, or campus-related information, respond in a supportive and approachable tone. " +
                    "Avoid giving uncertain or speculative answers. If you are unsure about something, let the student know politely and guide them to reliable sources or university officials for further assistance. " +
                    "Always prioritize the student's clarity and satisfaction while ensuring that the information provided aligns with university policies and available resources. " +
                    "If the student is asking for a academic question, please mention academic dishonesty and give citations (link) to Iowa State Academic Dishonesty Code.";            // Initialize a StringBuilder for messages
            StringBuilder messagesBuilder = new StringBuilder();
            messagesBuilder.append("{\"role\": \"system\", \"content\": \"").append(escapeJson(systemPrompt)).append("\"},\n");

            for (Chatbot msg : recentMessagesDesc) {
                String role = msg.getSender().equalsIgnoreCase("USER") ? "user" : "assistant";
                messagesBuilder.append("{\"role\": \"").append(role).append("\", \"content\": \"")
                        .append(escapeJson(msg.getMessage())).append("\"},\n");
            }

            // Remove the trailing comma and newline
            if (messagesBuilder.length() > 0) {
                messagesBuilder.setLength(messagesBuilder.length() - 2);
            }

            // Define the request payload
            String payload = "{\n" +
                    "  \"model\": \"" + openaiModel + "\",\n" +
                    "  \"messages\": [\n" +
                    messagesBuilder.toString() + "\n" +
                    "  ]\n" +
                    "}";

            logger.info("Sending payload to OpenAI API: {}", payload);

            // Prepare HTTP headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openaiApiKey);

            HttpEntity<String> entity = new HttpEntity<>(payload, headers);

            // Send POST request to OpenAI API
            ResponseEntity<String> response = restTemplate.exchange(openaiApiUrl, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error: OpenAI API request failed with status: " + response.getStatusCode());
            }

            // Parse the response to extract the chatbot's message
            JsonNode root = objectMapper.readTree(response.getBody());
            String chatbotMessage = root.path("choices").get(0).path("message").path("content").asText();

            // Save chatbot's message
            Chatbot botMessage = new Chatbot();
            botMessage.setUser(user);
            botMessage.setSender("CHATBOT");
            botMessage.setMessage(chatbotMessage);
            botMessage.setTimestamp(LocalDateTime.now());
            chatbotRepository.save(botMessage);

            return ResponseEntity.ok(chatbotMessage);
        } catch (Exception e) {
            // Log the exception (optional)
            // logger.error("Error in sendMessage: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    /**
     * Endpoint to retrieve chat history for a user.
     *
     * @param netId ID of the user.
     * @return List of chat messages.
     */
    @Operation(summary = "Retrieve chat history", description = "Fetches all chat messages exchanged between a user and the chatbot.")
    @GetMapping("/history")
    public ResponseEntity<?> getChatHistory(
            @RequestParam String netId
    ) {
        try {
            logger.info("Retrieving chat history for NET ID: {}", netId);

            // Validate user by userId
            Optional<User> userOpt = userRepository.findUserByNetId(netId);
            if (userOpt.isEmpty()) {
                logger.warn("User not found with NET ID: {}", netId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Error: User not found with NET ID: " + netId);
            }
            User user = userOpt.get();

            // Fetch chat history
            List<Chatbot> history = chatbotRepository.findByUserOrderByTimestampAsc(user);
            logger.info("Retrieved {} messages for User NET ID: {}", history.size(), netId);

            return ResponseEntity.ok(history);
        } catch (Exception e) {
            logger.error("Error in getChatHistory: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}