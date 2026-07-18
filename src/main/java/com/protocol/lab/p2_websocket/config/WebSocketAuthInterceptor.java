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
            String login = accessor.getLogin();
            String passcode = accessor.getPasscode();
            
            // Nếu STOMP Client truyền qua Headers tuỳ chỉnh (dự phòng)
            if (login == null && accessor.getNativeHeader("login") != null) {
                login = accessor.getNativeHeader("login").get(0);
            }
            if (passcode == null && accessor.getNativeHeader("passcode") != null) {
                passcode = accessor.getNativeHeader("passcode").get(0);
            }

            log.info("WS CONNECT Request - User: {}, Pass: {}", login, passcode);

            if (login == null || login.trim().isEmpty() || passcode == null || passcode.trim().isEmpty()) {
                log.error("WebSocket Auth Failed: Missing login or passcode");
                throw new IllegalArgumentException("Unauthorized WS access: Missing credentials!");
            }
            
            // Giả lập check DB: Password luôn phải là 123456
            if (!"123456".equals(passcode)) {
                log.error("WebSocket Auth Failed: Wrong password");
                throw new IllegalArgumentException("Unauthorized WS access: Wrong password!");
            }
            
            // Gán Username vào Principal
            final String finalUsername = login;
            accessor.setUser(() -> finalUsername);
            log.info("WebSocket Authenticated User: {}", finalUsername);
        }
        return message;
    }
}
