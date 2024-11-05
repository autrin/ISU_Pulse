package coms309.backEnd.demo.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.messaging.MessageSecurityConfigurer;
import org.springframework.security.config.annotation.messaging.EnableMessageSecurity;

@Configuration
@EnableMessageSecurity
public class WebSocketSecurityConfig extends MessageSecurityConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                .simpDestMatchers("/ws/announcement/**").authenticated()
                .anyMessage().permitAll();
    }
}