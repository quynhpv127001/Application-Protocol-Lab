# Phase 5: gRPC & Protocol Buffers (Microservices)

## 1. Giới thiệu
Package `p5_grpc` triển khai chuẩn giao tiếp RPC hiệu năng cao giữa các dịch vụ Backend-to-Backend:
* **Protocol Buffers (.proto):** Hợp đồng giao tiếp nhị phân, định kiểu chặt chẽ.
* **HTTP/2 Transport:** Hỗ trợ Multiplexing, nén Header HPACK và truyền dẫn nhị phân.
* **4 Mô hình Streaming:** Unary RPC, Server Streaming, Client Streaming, Bidirectional Streaming.
* **The Backend Engineer Way:**
  * Xử lý lỗi toàn cục với `@GrpcAdvice` (`GrpcExceptionAdvice`).
  * Kiểm soát thời gian xử lý và giải phóng tài nguyên với **gRPC Deadlines** (`Context.current().isCancelled()`).
  * Trích xuất và truyền nhận định danh vết phân tán với **ServerInterceptor** (`LogAndTraceServerInterceptor` / Header `x-trace-id`).

---

## 2. Hướng dẫn kiểm thử
Chi tiết các lệnh `grpcurl` và mã mẫu kiểm thử trên Postman có sẵn tại:
👉 [Xem tài liệu kiểm thử chi tiết (grpc.md)](./grpc.md)
