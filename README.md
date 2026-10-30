# Application Protocol Lab

Hệ thống lab chuẩn hóa các mô hình giao tiếp Application-to-Application (A2A) và Frontend-to-Backend (FE-BE), tập trung vào các bài toán kỹ thuật phân tán: Timeout, Connection Pooling, Resilience/Retry, N+1 Query, và Low-level byte framing.

## Kiến trúc & Nguyên tắc
* **Nền tảng:** Java 21 | Spring Boot 3.3.x | Maven.
* **Tính độc lập:** Các module (`01-http` $\rightarrow$ `08-mcp`) cô lập 100%, không import chéo source code.
* **Hạ tầng tinh gọn:** Mock in-memory (`ConcurrentHashMap`), không phụ thuộc database/broker ngoài.
* **Giao diện:** Tích hợp Thymeleaf UI tại `http://localhost:8080/` cho từng giao thức.

## Ma trận Giao thức

| STT | Module | Giao thức | Cổng / Endpoint | Đặc trưng kỹ thuật cốt lõi |
| :---: | :--- | :--- | :--- | :--- |
| **01** | `01-http` | HTTP/REST | `:8080/api/v1` | Connection Pool, Connect/Read Timeout, SSE, Long-polling |
| **02** | `02-websocket` | WebSocket / STOMP | `ws://:8080/ws` | Simple In-Memory Broker, Heartbeat (Ping/Pong), Handshake Auth |
| **03** | `03-socketio` | Socket.IO | `:9092` | Auto-fallback (Polling $\leftrightarrow$ WS), Rooms & Namespaces |
| **04** | `04-graphql` | GraphQL | `:8080/graphql` | `@BatchMapping` (DataLoader giải quyết N+1), Max Query Depth |
| **05** | `05-grpc` | gRPC / Protobuf | `:9090` | 4 mô hình Streaming, Deadlines/Cancellation, Trace Interceptor |
| **06** | `06-mqtt` | MQTT | `:1883` | Pub/Sub, QoS 0/1/2, Last Will (LWT), Retained Messages |
| **07** | `07-socket` | TCP/UDP Socket | `:9191` (TCP) / `:9192` (UDP) | Java NIO, Xử lý Sticky Packets (Length-Field Framing) |
| **08** | `08-mcp` | Model Context Protocol | `:8080/mcp` | JSON-RPC 2.0, AI Tools & Resources Integration |

## Khởi chạy & Kiểm thử

```bash
# Khởi chạy toàn bộ hệ thống (Web UI tại http://localhost:8080)
./mvnw spring-boot:run
```

* Chi tiết các payload mẫu và lệnh kiểm thử (cURL, grpcurl, netcat `nc`, Postman Native, raw JSON-RPC) được đặt trực tiếp trong file doc kỹ thuật của từng module (vd: `socket.md`, `mqtt.md`, `grpc.md`).
* Giao diện tương tác trực quan (Web UI Dashboard) sẵn sàng tại `http://localhost:8080/` cho tất cả các giao thức đã hoàn thiện.

 ---
 
