package com.protocol.lab.p3_socketio.config;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServerRunner implements CommandLineRunner {

    private final SocketIOServer server;

    @Override
    public void run(String... args) {
        log.info("Starting Socket.IO server on port 8085...");
        server.start();
    }

    @PreDestroy
    public void stopServer() {
        log.info("Stopping Socket.IO server...");
        server.stop();
    }
}
