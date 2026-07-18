package com.protocol.lab.p2_websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String sender;
    private String content;
    private String room;
    private MessageType type;

    public enum MessageType {
        CHAT, JOIN, LEAVE
    }
}
