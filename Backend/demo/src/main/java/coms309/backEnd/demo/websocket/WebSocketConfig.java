package coms309.backEnd.demo.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final AnnouncementWebSocketHandler announcementWebSocketHandler;

    public WebSocketConfig(AnnouncementWebSocketHandler announcementWebSocketHandler) {
        this.announcementWebSocketHandler = announcementWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(announcementWebSocketHandler, "/ws/announcement")
                .setAllowedOrigins("*"); // In production, specify allowed origins
    }
}