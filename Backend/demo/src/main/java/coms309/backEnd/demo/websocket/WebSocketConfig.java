package coms309.backEnd.demo.websocket;

import coms309.backEnd.demo.websocket.announcement.AnnouncementWebSocketHandler;
import coms309.backEnd.demo.websocket.chat.ChatWebSocketHandler;
import coms309.backEnd.demo.websocket.groupChat.GroupChatWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final AnnouncementWebSocketHandler announcementWebSocketHandler;
    private final ChatWebSocketHandler chatWebSocketHandler;
    private final GroupChatWebSocketHandler groupChatWebSocketHandler;

    public WebSocketConfig(AnnouncementWebSocketHandler announcementWebSocketHandler, ChatWebSocketHandler chatWebSocketHandler, GroupChatWebSocketHandler groupChatWebSocketHandler) {
        this.announcementWebSocketHandler = announcementWebSocketHandler;
        this.chatWebSocketHandler = chatWebSocketHandler;
        this.groupChatWebSocketHandler = groupChatWebSocketHandler;
    }



    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(announcementWebSocketHandler, "/ws/announcement")
                .setAllowedOrigins("*");
        registry.addHandler(chatWebSocketHandler, "/ws/chat")
                .setAllowedOrigins("*");// In production, specify allowed origins
        registry.addHandler(groupChatWebSocketHandler, "/ws/group-chat")
                .setAllowedOrigins("*");    }
}