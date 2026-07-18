# Application Protocol Lab

Chào mừng bạn đến với **Application Protocol Lab**! Đây là một bách khoa toàn thư thực hành về các giao thức giao tiếp Application-to-Application (A2A) và Frontend-to-Backend (FE-BE).

Dự án được thiết kế đặc biệt dành cho **Backend & Data Engineer**, tập trung sâu vào giải quyết các bài toán hóc búa của hệ thống phân tán:
- Timeout Management (Connect & Read time).
- Connection Pooling.
- Load Balancing (L4/L7).
- Resilience & Retry logic.
- Tránh dính gói (Sticky packets), N+1 Query.
- Scaling (Redis Pub/Sub).

## Kiến trúc dự án
Dự án được xây dựng trên nền tảng:
- **Java 21**
- **Spring Boot 3.3.x**
- **Maven** (Multi-module)

Các giao thức được tổ chức thành các module hoàn toàn độc lập với nhau (Không import chéo source code), đảm bảo tính cô lập, dễ dàng để bạn copy-paste sang dự án thực tế.

## Danh sách các Module
1. `http`: Khám phá HTTP/REST, RestClient, SSE, Long-polling.
2. `websocket`: Khám phá Realtime thuần túy, Heartbeat, STOMP.
3. `socketio`: Cơ chế kết nối FE-BE với auto-fallback.
4. `graphql`: Truy vấn dữ liệu linh hoạt, DataLoader.
5. `grpc`: Tiêu chuẩn giao tiếp Microservices tốc độ cao.
6. `mqtt`: Giao thức nhắn tin vạn vật (IoT), LWT, Retained Message.
7. `socket`: Low-level TCP/UDP với NIO.
8. `mcp`: Model Context Protocol (AI Tools Integration).

## Cách sử dụng
1. Clone repository này về máy.
2. Mở bằng IntelliJ IDEA hoặc VS Code.
3. Chuyển đến thư mục của giao thức bạn muốn tìm hiểu (VD: `cd http`).
4. Đọc file `README.md` bên trong mỗi module để hiểu nguyên lý, kiến trúc, và lấy các API Testing Payload (cURL, JSON-RPC, v.v) đã được chuẩn bị sẵn để copy-paste chạy ngay.

*Lưu ý: Bạn có thể theo dõi toàn bộ kế hoạch chi tiết ở file [plan.md](./plan.md).*
