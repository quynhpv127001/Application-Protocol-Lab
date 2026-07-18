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
- [ ] **1.6 Hoàn thiện Module**: Viết Test bằng `WireMock`, tạo `test.http` và viết `README.md` / `api.md` (kèm cURL mẫu).

---

## Phase 2: Khám phá WebSocket & Realtime (Package `p2_websocket`)
- [ ] **2.1 Setup Module**: Khởi tạo `p2_websocket`, thêm `spring-boot-starter-websocket`.
- [ ] **2.2 WebSocket thuần & STOMP**: `chat` (room), `broadcast` (notification).
- [ ] **2.3 The Backend Engineer way**:
    - [ ] **Heartbeat (Ping/Pong)**: Cơ chế Keep-alive phát hiện mất mạng.
    - [ ] **Handshake Auth**: Xác thực JWT lúc thiết lập `ws://`.
    - [ ] **Scaling**: Tích hợp Redis Pub/Sub đồng bộ message giữa nhiều instances.
- [ ] **2.4 Hoàn thiện Module**: Test & `README.md` (kèm JS Client code mẫu / raw frame data để copy-paste chạy).

---

## Phase 3: Khám phá Socket.IO - Ứng dụng FE-BE chuyên sâu (Package `p3_socketio`)
- [ ] **3.1 Setup Module**: Khởi tạo `p3_socketio`, tích hợp thư viện `netty-socketio`.
- [ ] **3.2 The Frontend-Backend Connection**:
    - [ ] Tính năng Auto-fallback (Long-Polling <-> WebSocket).
    - [ ] Quản lý Room/Namespaces tự động.
- [ ] **3.3 Hoàn thiện Module**: Test & `README.md` (kèm code socket.io-client mẫu để copy).

---

## Phase 4: Khám phá GraphQL (Package `p4_graphql`)
- [ ] **4.1 Setup Module**: Khởi tạo `p4_graphql`, thêm `spring-boot-starter-graphql`.
- [ ] **4.2 Core**: `schema.graphqls`, Queries, Mutations.
- [ ] **4.3 The Data Engineer way**:
    - [ ] Giải quyết N+1 query (`BatchMapping` / `DataLoader`).
    - [ ] Security: Max Query Depth/Complexity.
- [ ] **4.4 Hoàn thiện Module**: Test & `README.md` (kèm raw GraphQL query/variables để dán thẳng vào Postman).

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

---

## Phase 6: Khám phá IoT & Message Broker với MQTT (Package `p6_mqtt`)
- [ ] **6.1 Setup Module**: Khởi tạo `p6_mqtt`, tạo Mosquitto Broker qua `docker-compose.yml`.
- [ ] **6.2 Thực thi Pub/Sub**: Publisher, Subscriber.
- [ ] **6.3 The System Engineer way**: QoS 0/1/2, LWT (Last Will), Retained Messages.
- [ ] **6.4 Hoàn thiện Module**: Test & `README.md` (kèm lệnh `mosquitto_pub` / raw payload để test).

---

## Phase 7: Giao tiếp Low-level Socket (Package `p7_socket`)
- [ ] **7.1 Setup Module**: Khởi tạo `p7_socket`.
- [ ] **7.2 TCP & UDP**: TCP Client/Server (NIO), UDP.
- [ ] **7.3 The Backend way**: Xử lý Sticky packets, End of Stream.
- [ ] **7.4 Hoàn thiện Module**: Test & `README.md` (kèm lệnh `netcat` (nc) mẫu).

---

## Phase 8: Model Context Protocol (Package `p8_mcp`) - Giao thức AI mới nhất
- [ ] **8.1 Setup Module**: Khởi tạo `p8_mcp`.
- [ ] **8.2 Core**: MCP Server/Client.
- [ ] **8.3 Hoàn thiện Module**: Test & `README.md` (KÈM RAW JSON-RPC PAYLOAD như ví dụ user cung cấp để paste thẳng vào MCP Inspector/Postman).
