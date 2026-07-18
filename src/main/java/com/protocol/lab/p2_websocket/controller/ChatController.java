package com.protocol.lab.p2_websocket.controller;

import com.protocol.lab.p2_websocket.dto.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@Slf4j
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat")
    public void sendMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        Principal user = headerAccessor.getUser();
        if (user != null) {
            chatMessage.setSender(user.getName());
        } else {
            chatMessage.setSender("Anonymous");
        }
        
        log.info("Received message from {} to room {}: {}", chatMessage.getSender(), chatMessage.getRoom(), chatMessage.getContent());
        
        // Broadcast trực tiếp xuống các client đang lắng nghe room này (Sử dụng Simple Broker in-memory)
        messagingTemplate.convertAndSend("/topic/room/" + chatMessage.getRoom(), chatMessage);
    }
}
