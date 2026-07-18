# Application Protocol Lab - Detailed Master Plan

Dự án này là phòng lab thực hành về giao tiếp Application-to-Application (A2A) và Frontend-to-Backend (FE-BE), tập trung vào các tiêu chuẩn của Backend/Data Engineer (System design, Timeout, Retry, Load Balancing, Resilience, Security).

Các module dưới đây được sắp xếp theo mức độ phổ biến giảm dần trong thực tế ngành phần mềm. Mặc dù chạy chung 1 service, nhưng Code của các package/giai đoạn phải độc lập, không import chéo nhau để dễ dàng copy-paste sang dự án khác.

## Tiêu chuẩn chung bắt buộc (Do AI thực thi)
- **Tài liệu API copy-paste được luôn**: Mọi API, Endpoint hoặc Kết nối được khai báo BẮT BUỘC phải đi kèm document (nằm trong `README.md` hoặc `api.md`). Format docs phải chi tiết, ngắn gọn, chuẩn mực (có bảng Headers, Params, Request Body, Response mẫu cho case thành công/thất bại).
- **Tối ưu cực hạn việc Test (Copy & Paste vào Tool)**: 
    - Với HTTP/REST: Phải có mã `cURL` chuẩn.
    - Với các giao thức không cURL được (MCP, GraphQL, WebSocket, gRPC, MQTT): **BẮT BUỘC cung cấp Raw Payload (như cục JSON-RPC cho MCP) hoặc mã Client (JS/grpcurl)** để user chỉ việc bôi đen -> Copy -> Paste thẳng vào Postman hoặc tool test chuyên dụng, tuyệt đối không bắt user gõ tay ghép JSON.
- **Tính Độc lập**: Code của các module độc lập 100%, không import chéo nhau.
- **Môi trường Test có sẵn**: Luôn cung cấp file `application.yml`, `docker-compose.yml` (nếu có hạ tầng phụ trợ) và Test classes để verify.
- **Giao diện UI (Thymeleaf)**: Bắt buộc mỗi Phase phải có giao diện test trên web. Giao diện này BẮT BUỘC phải gọi được **TẤT CẢ** các endpoints/protocal đã code trong Phase đó.
- **Kiến trúc tinh gọn (Mocking Backend)**: Code Backend tự mô phỏng hành vi (In-memory, Simple Broker...) mà KHÔNG phụ thuộc vào hạ tầng bên ngoài (như Redis, MySQL, RabbitMQ) để giữ cho dự án siêu nhẹ và dễ test. Tuyệt đối không tự ý tạo thêm file hạ tầng như `docker-compose.yml` mà không xin phép. Giao diện UI tự dựng bối cảnh (ví dụ: chia 2 cột Client A và Client B tự chat với nhau) bằng Thymeleaf để chứng minh tính năng thực tế một cách độc lập và trực quan.
- **Đơn giản hóa Testing**: Bỏ qua các ràng buộc bảo mật phức tạp (như bắt ép chuỗi prefix `Bearer `), giữ Code gọn và thực tế để User test nhanh nhất.
- **Lưu trữ In-Memory (RAM)**: Trong các bài lab upload file, chỉ lưu dữ liệu trên RAM (vd: `ConcurrentHashMap`), tuyệt đối không lưu ra ổ cứng vật lý.

---

## Phase 0: Khởi tạo kiến trúc nền tảng (Parent Workspace)
- [ ] 0.1 Khởi tạo thư mục gốc (`application-protocol-lab`) và `.gitignore` chuẩn cho Java/Spring.
- [ ] 0.2 Tạo Parent `pom.xml` với `packaging=jar` (Single Service).
    - [ ] Khai báo Java 21, Spring Boot 3.3.x.
    - [ ] Khai báo các plugins chuẩn: `maven-compiler-plugin`, `spring-boot-maven-plugin`.
- [ ] 0.3 Tạo `README.md` ở thư mục gốc giới thiệu tổng quan dự án và cách tra cứu.

---

## Phase 1: Khám phá HTTP/REST (Package `p1_http`) - Phổ biến nhất
- [ ] **1.1 Setup Module**: Khởi tạo package `p1_http`, thêm `spring-boot-starter-web`, `lombok`, `validation`.
- [ ] **1.2 Cấu trúc cơ bản (CRUD)**: `GET`, `POST`, `PUT`, `PATCH`, `DELETE`.
- [ ] **1.3 File Transfer**: `multipart-upload`, `download` (streaming chunks).
- [ ] **1.4 Giao tiếp Client to Server (The Backend Engineer way)**:
    - [ ] Sử dụng `RestClient` (Spring 6).
    - [ ] **Timeout Management**: Cấu hình Connection Timeout và Read Timeout.
    - [ ] **Connection Pooling**: Tái sử dụng TCP connection.
    - [ ] **Resilience**: Tích hợp Retry cơ bản.
- [ ] **1.5 Advanced/Streaming (FE-BE)**: `sse`, `long-polling`, Authentication (Header).
- [x] **1.6 Hoàn thiện Module**: Viết Test bằng `WireMock`, tạo `test.http` và viết `README.md` / `api.md` (kèm cURL mẫu).
- [x] **1.7 Giao diện UI**: Tích hợp Thymeleaf, thiết kế tab menu trái và màn hình test API trực quan cho HTTP.

---

## Phase 2: Khám phá WebSocket & Realtime (Package `p2_websocket`)
- [x] **2.1 Setup Module**: Khởi tạo `p2_websocket`, thêm `spring-boot-starter-websocket`.
- [x] **2.2 WebSocket thuần & STOMP**: `chat` (room), `broadcast` (notification).
- [x] **2.3 The Backend Engineer way**:
    - [x] **Heartbeat (Ping/Pong)**: Cơ chế Keep-alive phát hiện mất mạng.
    - [x] **Handshake Auth**: Cấu hình ChannelInterceptor lấy Username lúc thiết lập `ws://`.
    - [x] **Architecture**: Sử dụng Simple Broker (In-Memory), không dùng Redis cho Lab.
- [x] **2.4 Hoàn thiện Module**: Đã có `WebSocketConfig.java`, `WebSocketAuthInterceptor.java` và `ChatController.java`.
- [x] **2.5 Giao diện UI**: Tích hợp tab WebSocket, màn hình 2 cột Chat giả lập Client A và Client B kết nối vào cùng 1 room.

---

## Phase 3: Khám phá Socket.IO - Ứng dụng FE-BE chuyên sâu (Package `p3_socketio`)
- [ ] **3.1 Setup Module**: Khởi tạo `p3_socketio`, tích hợp thư viện `netty-socketio`.
- [ ] **3.2 The Frontend-Backend Connection**:
    - [ ] Tính năng Auto-fallback (Long-Polling <-> WebSocket).
    - [ ] Quản lý Room/Namespaces tự động.
- [ ] **3.3 Hoàn thiện Module**: Test & `README.md` (kèm code socket.io-client mẫu để copy).
- [ ] **3.4 Giao diện UI**: Tích hợp tab Socket.IO, kiểm thử Auto-fallback.

---

## Phase 4: Khám phá GraphQL (Package `p4_graphql`)
- [ ] **4.1 Setup Module**: Khởi tạo `p4_graphql`, thêm `spring-boot-starter-graphql`.
- [ ] **4.2 Core**: `schema.graphqls`, Queries, Mutations.
- [ ] **4.3 The Data Engineer way**:
    - [ ] Giải quyết N+1 query (`BatchMapping` / `DataLoader`).
    - [ ] Security: Max Query Depth/Complexity.
- [ ] **4.4 Hoàn thiện Module**: Test & `README.md` (kèm raw GraphQL query/variables để dán thẳng vào Postman).
- [ ] **4.5 Giao diện UI**: Tích hợp tab GraphQL, form gọi Query/Mutation.

---

## Phase 5: Khám phá gRPC & Protobuf (Package `p5_grpc`) - Phổ biến trong BE-BE Microservices
- [ ] **5.1 Setup Module**: Khởi tạo `p5_grpc`, tích hợp `grpc-spring-boot-starter` và Protobuf Maven Plugin.
- [ ] **5.2 Định nghĩa IDL**: Tạo `schema.proto`.
- [ ] **5.3 Thực thi 4 mô hình gRPC**: `unary`, `server-streaming`, `client-streaming`, `bidirectional-streaming`.
- [ ] **5.4 The Backend Engineer way**:
    - [ ] **Error Handling**: Mapping StatusRuntimeException.
    - [ ] **Deadlines/Timeouts**: Cấu hình gRPC Deadlines.
    - [ ] **Interceptors**: Tạo gRPC Interceptor (Auth/Logging).
- [ ] **5.5 Hoàn thiện Module**: Test & `README.md` (kèm lệnh `grpcurl` mẫu và raw JSON body để gọi hàm).
- [ ] **5.6 Giao diện UI**: Tích hợp tab gRPC, mô phỏng gọi API.

---

## Phase 6: Khám phá IoT & Message Broker với MQTT (Package `p6_mqtt`)
- [ ] **6.1 Setup Module**: Khởi tạo `p6_mqtt`, tạo Mosquitto Broker qua `docker-compose.yml`.
- [ ] **6.2 Thực thi Pub/Sub**: Publisher, Subscriber.
- [ ] **6.3 The System Engineer way**: QoS 0/1/2, LWT (Last Will), Retained Messages.
- [ ] **6.4 Hoàn thiện Module**: Test & `README.md` (kèm lệnh `mosquitto_pub` / raw payload để test).
- [ ] **6.5 Giao diện UI**: Tích hợp tab MQTT, mô phỏng thiết bị IoT gửi nhận dữ liệu.

---

## Phase 7: Giao tiếp Low-level Socket (Package `p7_socket`)
- [ ] **7.1 Setup Module**: Khởi tạo `p7_socket`.
- [ ] **7.2 TCP & UDP**: TCP Client/Server (NIO), UDP.
- [ ] **7.3 The Backend way**: Xử lý Sticky packets, End of Stream.
- [ ] **7.4 Hoàn thiện Module**: Test & `README.md` (kèm lệnh `netcat` (nc) mẫu).
- [ ] **7.5 Giao diện UI**: Tích hợp tab Socket TCP/UDP.

---

## Phase 8: Model Context Protocol (Package `p8_mcp`) - Giao thức AI mới nhất
- [ ] **8.1 Setup Module**: Khởi tạo `p8_mcp`.
- [ ] **8.2 Core**: MCP Server/Client.
- [ ] **8.3 Hoàn thiện Module**: Test & `README.md` (KÈM RAW JSON-RPC PAYLOAD như ví dụ user cung cấp để paste thẳng vào MCP Inspector/Postman).
- [ ] **8.4 Giao diện UI**: Tích hợp tab MCP, giả lập gọi tools AI.
