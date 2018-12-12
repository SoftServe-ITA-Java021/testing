package com.kh021j.travelwithpleasurehub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

/**
 * Created by ${JDEEK} on ${11.11.2018}.
 */
@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages){
        messages.simpMessageDestMatchers("/topic/chat.login", "/topic/chat.logout", "/topic/chat.message").denyAll()
        .anyMessage().authenticated();
    }

    @Override
    protected boolean sameOriginDisabled(){
        return true;
    }
}
