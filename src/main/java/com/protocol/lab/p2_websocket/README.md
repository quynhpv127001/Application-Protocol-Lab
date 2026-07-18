# Module: WebSocket & STOMP

## Giới thiệu
WebSocket là giao thức giao tiếp hai chiều (bidirectional), toàn thời gian (full-duplex) trên một kết nối TCP duy nhất. Khác với HTTP/REST (Client phải chủ động Request), WebSocket cho phép Server chủ động đẩy (Push) dữ liệu xuống Client bất kỳ lúc nào mà không độ trễ. 
STOMP (Simple Text Oriented Messaging Protocol) là một sub-protocol chạy trên nền WebSocket, quy định format tin nhắn rõ ràng theo mô hình Pub/Sub (Topic/Queue).

## Kiến trúc (The Backend Engineer way)
- **Handshake Authentication**: Thay vì gửi Token qua Header của mỗi request HTTP, WebSocket thực hiện xác thực ngay từ bước Handshake ban đầu (khi upgrade từ HTTP lên WS). Ta chặn ở tầng `ChannelInterceptor` để bắt gói tin `CONNECT` và lấy token, giảm tải cho các message sau đó.
- **Heartbeat (Ping/Pong)**: Trong kết nối TCP giữ lâu, cấu hình Heartbeat giúp cả Server và Client biết đối phương còn sống hay đã mất mạng (Network drop) để dọn dẹp tài nguyên (Giải phóng RAM, Close Socket).
- **Simple Broker (In-Memory)**: 
    - Để phục vụ mục đích Lab siêu nhẹ, Server sử dụng Broker tích hợp sẵn trong bộ nhớ RAM (`SimpleBrokerMessageHandler`).
    - *Lưu ý thực tế*: Khi Scale-out ra nhiều Server Node, Simple Broker không đồng bộ được message. Lúc đó, hệ thống thực tế sẽ phải tích hợp External Broker như RabbitMQ hoặc Redis Pub/Sub.

## Cách chạy
1. Run class chính của Spring Boot (`Application.java`).
2. Mở trình duyệt truy cập `http://localhost:8080/phase2` để test giao diện chat realtime.
3. Mở file [api.md](api.md) để lấy code mẫu kết nối (dành cho phía Client) copy và paste.

## Cấu trúc
- `config/WebSocketConfig.java`: Cấu hình Endpoint `/ws`, STOMP Broker và Heartbeat.
- `config/WebSocketAuthInterceptor.java`: Chặn gói tin `CONNECT` để xác thực người dùng.
- `controller/ChatController.java`: Controller nhận tin nhắn từ Client và Broadcast vào Room.
- `dto/ChatMessage.java`: Cấu trúc JSON của tin nhắn.
