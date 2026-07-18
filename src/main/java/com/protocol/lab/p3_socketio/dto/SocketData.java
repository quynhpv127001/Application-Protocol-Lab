package com.protocol.lab.p3_socketio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocketData {
    private String room;
    private String content;
    private String sender;
}
