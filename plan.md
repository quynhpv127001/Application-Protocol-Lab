# Application Protocol Lab - Detailed Master Plan

Dự án này là phòng lab thực hành về giao tiếp Application-to-Application (A2A) và Frontend-to-Backend (FE-BE), tập trung vào các tiêu chuẩn của Backend/Data Engineer (System design, Timeout, Retry, Load Balancing, Resilience, Security).

Các module dưới đây được sắp xếp theo mức độ phổ biến giảm dần trong thực tế ngành phần mềm. Mỗi module đều độc lập 100% để dễ dàng copy-paste sang dự án khác.

## Tiêu chuẩn chung bắt buộc (Do AI thực thi)
- **Tài liệu API copy-paste được luôn**: Mọi API, Endpoint hoặc Kết nối được khai báo BẮT BUỘC phải đi kèm document (nằm trong `README.md` hoặc `api.md`). Format docs phải chi tiết, ngắn gọn, chuẩn mực (có bảng Headers, Params, Request Body, Response mẫu cho case thành công/thất bại).
- **Tối ưu cực hạn việc Test (Copy & Paste vào Tool)**: 
    - Với HTTP/REST: Phải có mã `cURL` chuẩn.
    - Với các giao thức không cURL được (MCP, GraphQL, WebSocket, gRPC, MQTT): **BẮT BUỘC cung cấp Raw Payload (như cục JSON-RPC cho MCP) hoặc mã Client (JS/grpcurl)** để user chỉ việc bôi đen -> Copy -> Paste thẳng vào Postman hoặc tool test chuyên dụng, tuyệt đối không bắt user gõ tay ghép JSON.
- **Tính Độc lập**: Không module nào được import source code của module khác.
- **Môi trường Test có sẵn**: Luôn cung cấp file `application.yml`, `docker-compose.yml` (nếu có hạ tầng phụ trợ) và Test classes để verify.

---

## Giai đoạn 1: Khởi tạo kiến trúc nền tảng (Parent Workspace)
- [ ] 1.1 Khởi tạo thư mục gốc (`application-protocol-lab`) và `.gitignore` chuẩn cho Java/Spring.
- [ ] 1.2 Tạo Parent `pom.xml` với `packaging=pom`.
    - [ ] Khai báo Java 21, Spring Boot 3.3.x trong `<dependencyManagement>`.
    - [ ] Khai báo các plugins chuẩn: `maven-compiler-plugin`, `spring-boot-maven-plugin`.
- [ ] 1.3 Tạo `README.md` ở thư mục gốc giới thiệu tổng quan dự án và cách tra cứu.

---

## Giai đoạn 2: Khám phá HTTP/REST (Module `http`) - Phổ biến nhất
- [ ] **2.1 Setup Module**: Khởi tạo module `http`, thêm `spring-boot-starter-web`, `lombok`, `validation`.
- [ ] **2.2 Cấu trúc cơ bản (CRUD)**: `GET`, `POST`, `PUT`, `PATCH`, `DELETE`.
- [ ] **2.3 File Transfer**: `multipart-upload`, `download` (streaming chunks).
- [ ] **2.4 Giao tiếp Client to Server (The Backend Engineer way)**:
    - [ ] Sử dụng `RestClient` (Spring 6).
    - [ ] **Timeout Management**: Cấu hình Connection Timeout và Read Timeout.
    - [ ] **Connection Pooling**: Tái sử dụng TCP connection.
    - [ ] **Resilience**: Tích hợp Retry cơ bản.
- [ ] **2.5 Advanced/Streaming (FE-BE)**: `sse`, `long-polling`, Authentication (Header).
- [ ] **2.6 Hoàn thiện Module**: Viết Test bằng `WireMock`, tạo `test.http` và viết `README.md` / `api.md` (kèm cURL mẫu).

---

## Giai đoạn 3: Khám phá WebSocket & Realtime (Module `websocket`)
- [ ] **3.1 Setup Module**: Khởi tạo `websocket`, thêm `spring-boot-starter-websocket`.
- [ ] **3.2 WebSocket thuần & STOMP**: `chat` (room), `broadcast` (notification).
- [ ] **3.3 The Backend Engineer way**:
    - [ ] **Heartbeat (Ping/Pong)**: Cơ chế Keep-alive phát hiện mất mạng.
    - [ ] **Handshake Auth**: Xác thực JWT lúc thiết lập `ws://`.
    - [ ] **Scaling**: Tích hợp Redis Pub/Sub đồng bộ message giữa nhiều instances.
- [ ] **3.4 Hoàn thiện Module**: Test & `README.md` (kèm JS Client code mẫu / raw frame data để copy-paste chạy).

---

## Giai đoạn 4: Khám phá Socket.IO - Ứng dụng FE-BE chuyên sâu (Module `socketio`)
- [ ] **4.1 Setup Module**: Khởi tạo `socketio`, tích hợp thư viện `netty-socketio`.
- [ ] **4.2 The Frontend-Backend Connection**:
    - [ ] Tính năng Auto-fallback (Long-Polling <-> WebSocket).
    - [ ] Quản lý Room/Namespaces tự động.
- [ ] **4.3 Hoàn thiện Module**: Test & `README.md` (kèm code socket.io-client mẫu để copy).

---

## Giai đoạn 5: Khám phá GraphQL (Module `graphql`)
- [ ] **5.1 Setup Module**: Khởi tạo `graphql`, thêm `spring-boot-starter-graphql`.
- [ ] **5.2 Core**: `schema.graphqls`, Queries, Mutations.
- [ ] **5.3 The Data Engineer way**:
    - [ ] Giải quyết N+1 query (`BatchMapping` / `DataLoader`).
    - [ ] Security: Max Query Depth/Complexity.
- [ ] **5.4 Hoàn thiện Module**: Test & `README.md` (kèm raw GraphQL query/variables để dán thẳng vào Postman).

---

## Giai đoạn 6: Khám phá gRPC & Protobuf (Module `grpc`) - Phổ biến trong BE-BE Microservices
- [ ] **6.1 Setup Module**: Khởi tạo `grpc`, tích hợp `grpc-spring-boot-starter` và Protobuf Maven Plugin.
- [ ] **6.2 Định nghĩa IDL**: Tạo `schema.proto`.
- [ ] **6.3 Thực thi 4 mô hình gRPC**: `unary`, `server-streaming`, `client-streaming`, `bidirectional-streaming`.
- [ ] **6.4 The Backend Engineer way**:
    - [ ] **Error Handling**: Mapping StatusRuntimeException.
    - [ ] **Deadlines/Timeouts**: Cấu hình gRPC Deadlines.
    - [ ] **Interceptors**: Tạo gRPC Interceptor (Auth/Logging).
- [ ] **6.5 Hoàn thiện Module**: Test & `README.md` (kèm lệnh `grpcurl` mẫu và raw JSON body để gọi hàm).

---

## Giai đoạn 7: Khám phá IoT & Message Broker với MQTT (Module `mqtt`)
- [ ] **7.1 Setup Module**: Khởi tạo `mqtt`, tạo Mosquitto Broker qua `docker-compose.yml`.
- [ ] **7.2 Thực thi Pub/Sub**: Publisher, Subscriber.
- [ ] **7.3 The System Engineer way**: QoS 0/1/2, LWT (Last Will), Retained Messages.
- [ ] **7.4 Hoàn thiện Module**: Test & `README.md` (kèm lệnh `mosquitto_pub` / raw payload để test).

---

## Giai đoạn 8: Giao tiếp Low-level Socket (Module `socket`)
- [ ] **8.1 Setup Module**: Khởi tạo `socket`.
- [ ] **8.2 TCP & UDP**: TCP Client/Server (NIO), UDP.
- [ ] **8.3 The Backend way**: Xử lý Sticky packets, End of Stream.
- [ ] **8.4 Hoàn thiện Module**: Test & `README.md` (kèm lệnh `netcat` (nc) mẫu).

---

## Giai đoạn 9: Model Context Protocol (Module `mcp`) - Giao thức AI mới nhất
- [ ] **9.1 Setup Module**: Khởi tạo `mcp`.
- [ ] **9.2 Core**: MCP Server/Client.
- [ ] **9.3 Hoàn thiện Module**: Test & `README.md` (KÈM RAW JSON-RPC PAYLOAD như ví dụ user cung cấp để paste thẳng vào MCP Inspector/Postman).
