# Phase 3: Socket.IO (The Frontend-Backend Realtime Tool)

Socket.IO là bộ khung hoàn chỉnh (framework) bọc ngoài WebSocket để giải quyết triệt để các vấn đề của Frontend khi làm Realtime. 

Tính năng "đắt giá" cốt lõi: **Auto-fallback** (Khởi đầu kết nối bằng cơ chế Long-Polling cực an toàn đi qua mọi Firewall, nếu đường truyền ổn định sẽ bí mật Upgrade lên WebSocket ở background). Ngoài ra còn hỗ trợ chia Namespace/Room rất mạnh mẽ.

### Kiến trúc trong bài lab:
- 1 server Spring Boot (Tomcat) chạy port `8080` để phục vụ UI.
- 1 server Socket.IO (Netty) chạy port `8085` để xử lý kết nối.

> 👉 **Hướng dẫn Test siêu tốc**: Mở file [socketio.md](socketio.md)
