package com.protocol.lab.p3_socketio.controller;

import com.corundumstudio.socketio.SocketIOServer;
import com.protocol.lab.p3_socketio.dto.SocketData;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SocketMessageController {

    private final SocketIOServer server;

    @PostConstruct
    public void init() {
        // Lắng nghe sự kiện Client kết nối
        server.addConnectListener(client -> {
            String token = client.getHandshakeData().getSingleUrlParam("token");
            log.info("Client connected: {}, Token: {}", client.getSessionId(), token);
        });

        // Lắng nghe sự kiện Client ngắt kết nối
        server.addDisconnectListener(client -> {
            log.info("Client disconnected: {}", client.getSessionId());
        });

        // Lắng nghe sự kiện Join Room
        server.addEventListener("join_room", String.class, (client, room, ackRequest) -> {
            client.joinRoom(room);
            log.info("Client {} joined room: {}", client.getSessionId(), room);
        });

        // Lắng nghe sự kiện Chat Message
        server.addEventListener("chat_message", SocketData.class, (client, data, ackRequest) -> {
            log.info("Received message to room {}: {}", data.getRoom(), data.getContent());
            // Broadcast tin nhắn tới tất cả client trong room
            server.getRoomOperations(data.getRoom()).sendEvent("chat_message", data);
        });
    }
}
