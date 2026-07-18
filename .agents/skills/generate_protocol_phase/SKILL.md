---
name: generate_protocol_phase
description: "Sử dụng skill này khi cần khởi tạo hoặc xây dựng một Phase (giao thức) mới trong dự án Application Protocol Lab."
---

# Hướng dẫn tạo Phase mới (Application Protocol Lab)

Skill này chứa toàn bộ các quy chuẩn, bài học kinh nghiệm và các "vết xe đổ" từ các Phase trước (HTTP, STOMP, Socket.IO) để áp dụng cho tất cả các Phase giao thức tiếp theo. Agent BẮT BUỘC tuân thủ luồng làm việc này để đảm bảo chất lượng, sự đồng nhất và tránh lỗi.

## 1. Cấu Trúc Mã Nguồn (Package Structure)
- **Thiết kế Modular**: Mỗi Phase là một thư mục package riêng nằm dưới `com.protocol.lab.pX_name` (ví dụ: `p1_http`, `p2_websocket`, `p3_socketio`, `p4_graphql`, v.v.).
- **Tài liệu ngắn gọn**: Trong mỗi package, bắt buộc phải có 2 file Markdown:
  - `README.md`: Giới thiệu tổng quan về giao thức, kiến trúc.
  - `<tên_giao_thức>.md` (VD: `socketio.md`): File chứa hướng dẫn Test (Client Script, Postman).
- **Tuyệt đối KHÔNG viết văn tự thừa thãi trong Docs**: File hướng dẫn test phải thẳng thắn, xúc tích. Đi thẳng vào "Bước 1: Làm gì, Bước 2: Điền gì". Xóa bỏ mọi lời bình luận như "Rất tuyệt vời...", "Đây là giải pháp hoàn hảo...".

## 2. Giao Diện Frontend (Thymeleaf UI) & Gỡ Lỗi 500
- Mỗi Phase phải có 1 file HTML riêng (VD: `phase4-graphql.html`) nằm trong `src/main/resources/templates/`.
- **Cấu trúc Layout (CỰC KỲ QUAN TRỌNG)**:
  - Phải tuân thủ cú pháp gọi layout: `<html lang="vi" xmlns:th="http://www.thymeleaf.org" th:replace="~{layout :: main(~{::#pageContent})}">`
  - Bọc toàn bộ nội dung (kể cả thẻ `<script>`) bên trong `<div id="pageContent">`. Nếu đặt `<script>` ra ngoài thẻ div này, Thymeleaf sẽ bỏ qua và frontend sẽ không chạy được JS.
- **WebController**:
  - Khi map Endpoint giao diện (VD: `/phase4`), bắt buộc phải truyền đủ biến vào Model: 
    ```java
    model.addAttribute("title", "Phase X: Tên Phase");
    model.addAttribute("activeTab", "phase4");
    ```
  - Thiếu `title` sẽ khiến Layout văng lỗi **HTTP 500 (TemplateProcessingException)**.
- **Menu Sidebar**: Cập nhật file `layout.html` để thêm link cho Phase mới, nhớ mở khóa (bỏ class `disabled`) và sử dụng logic `activeTab`.

## 3. Công Cụ Test & Ưu Tiên Native
- Khi hướng dẫn người dùng Test giao thức:
  1. Luôn ưu tiên dùng **Postman Native** nếu có (như Postman WebSocket Raw, Postman Socket.IO Request, Postman GraphQL). Hướng dẫn chi tiết từng field cần điền (URL, Params, Events, Body JSON).
  2. Dùng Extension của trình duyệt (Apic, STOMP WebSocket Client) nếu Postman không hỗ trợ chuẩn.
  3. Dùng mã nguồn Javascript tích hợp thẳng trên giao diện UI để dự phòng.

## 4. Quản lý Port & Tiến trình (Tránh BindException)
- Ứng dụng gốc chạy Tomcat trên port `8080`.
- Nếu Phase có tích hợp Server ngoài (VD: Netty chạy port `8085`), phải liên kết vòng đời của nó vào Spring Boot bằng `@Component` implements `CommandLineRunner` (để `start()`) và `@PreDestroy` (để `stop()`).
- **Khi dính lỗi Address Already in Use (Kẹt port)**: Chạy lệnh Maven `spring-boot:run` đôi lúc khi tắt bằng Task Manager của IDE (hoặc của Agent) sẽ để lại tiến trình Java bị cô nhi (orphan). Phải chủ động dùng `lsof -t -i:8080 | xargs -r kill -9` và tương tự cho các port phụ để dọn sạch trước khi start lại backend.

## 5. Quy Trình Quản Lý Task (Workflow)
Mọi Phase mới phải làm đủ các bước:
1. Tạo `plan.md` (nếu có yêu cầu từ người dùng) và cập nhật tiến độ liên tục.
2. Dùng `task.md` làm Check-list (`[ ]`, `[x]`).
3. Hoàn tất phải viết `walkthrough.md` tổng kết các file đã thay đổi, các kiến trúc đã làm.

## 6. Tuân Thủ Công Cụ (Tool Specificity)
- Không được dùng command `cat` kèm EOF trong bash shell để tạo file. Bắt buộc dùng công cụ `write_to_file`.
- Dùng `grep_search` thay vì bash `grep` nếu chỉ cần tìm kiếm nội dung trong mã nguồn.
