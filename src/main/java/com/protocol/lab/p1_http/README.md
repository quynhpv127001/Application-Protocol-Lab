# Module: HTTP / REST

## Giới thiệu
HTTP/REST là giao thức và kiểu kiến trúc phổ biến nhất trong giao tiếp ứng dụng, dựa trên nền tảng của HTTP (thường là HTTP/1.1 hoặc HTTP/2). REST quy định cách sử dụng các HTTP Methods (`GET`, `POST`, `PUT`, `DELETE`) gắn với ngữ nghĩa (Semantics).

## Kiến trúc (The Backend Engineer way)
- **Timeout Management**: Cấu hình `Connection Timeout` (thời gian bắt tay TCP) và `Read Timeout` (thời gian chờ gói tin data) thông qua Apache HttpClient. Nếu không có Timeout, một request lỗi có thể treo (hang) toàn bộ server Thread.
- **Connection Pooling**: Mở TCP handshake mất nhiều thời gian (chưa kể TLS/SSL). Connection Pooling giúp giữ TCP connection sống để tái sử dụng giữa các request liên tiếp đến cùng 1 host.
- **Idempotency**: `PUT`, `DELETE` và `GET` phải đảm bảo gọi 1 lần hay 100 lần kết quả (side-effect trên server) là như nhau. Nếu xảy ra timeout mạng lúc gọi `PUT`, Client có thể an tâm Retry. Ngược lại với `POST`.
- **Chunked Transfer**: Thay vì load file to vào RAM rồi gửi, dùng `StreamingResponseBody` đẩy dữ liệu stream trực tiếp xuống OS socket (chống OOM).

## Các kỹ thuật FE-BE (Polling)
- **SSE (Server-Sent Events)**: Giao thức 1 chiều, dùng `text/event-stream`. Dùng cho real-time notifications, biểu đồ chứng khoán. Trình duyệt hỗ trợ natively thông qua `EventSource`.
- **Long-Polling**: Client gọi server, server giữ request 30s. Nếu có data thì trả về luôn. Khắc phục điểm yếu tốn tài nguyên của Short-polling, nhưng vẫn thua WebSocket về độ trễ hai chiều.

## Cách chạy
1. Run class `HttpApplication.java`.
2. Mở file [api.md](api.md) để lấy toàn bộ các câu lệnh cURL mẫu để test trực tiếp trên Terminal hoặc Postman.

## Cấu trúc
- `crud`: Các method cơ bản.
- `file`: Xử lý streaming data chống tràn RAM.
- `client`: `RestClient` config Pooling & Retry.
- `streaming`: SSE, Long-polling.
