package com.protocol.lab.p2_websocket.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        // Xác thực tại frame CONNECT
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> authorization = accessor.getNativeHeader("Authorization");
            log.info("WS CONNECT Request with token: {}", authorization);

            if (authorization == null || authorization.isEmpty()) {
                log.error("WebSocket Auth Failed: Token is missing");
                throw new IllegalArgumentException("Unauthorized WS access!");
            }
            
            // Ở mô phỏng này, ta dùng luôn token làm Username
            String token = authorization.get(0);
            accessor.setUser(() -> token);
            log.info("WebSocket Authenticated User: {}", token);
        }
        return message;
    }
}
