# Socket.IO Documentation

## 1. Test trực tiếp trên Postman

### Bước 1: Khởi tạo kết nối
- Trên Postman, bấm **New** -> Chọn **Socket.IO Request**.
- **URL**: Nhập `http://localhost:8085`
- Mở tab **Params**, thêm Key: `token` - Value: `Alex` *(Server sẽ lấy Query Param này để định danh user)*.
- Bấm **Connect**. (Sẽ có log báo `Connected`).

### Bước 2: Lắng nghe sự kiện (Subscribe)
- Sang tab **Events**.
- Điền `chat_message` vào ô *Add event* và bật công tắc **Listen** sang On. (Lúc này Postman sẽ lắng nghe các tin nhắn có tên sự kiện là `chat_message`).

### Bước 3: Xin vào Room (Join Room)
- Sang tab **Message**.
- Đổi ô *Event name* (bên trái nút Send) từ mặc định thành: `join_room`
- Đổi kiểu dữ liệu sang **Text** (hoặc chuỗi).
- Nội dung Message: `IT-ROOM`
- Bấm **Send**.

### Bước 4: Gửi tin nhắn (Chat)
- Vẫn ở tab **Message**.
- Đổi ô *Event name* thành: `chat_message`
- Đổi kiểu dữ liệu sang **JSON**.
- Paste nội dung sau vào ô Message:
  ```json
  {
      "room": "IT-ROOM",
      "content": "Hello anh em, tôi test bằng Postman!",
      "sender": "Alex"
  }
  ```
- Bấm **Send**. Bạn sẽ thấy tin nhắn nảy lên ở ô Response phía dưới ngay lập tức!

---

## 2. Code Client (Dành cho Frontend)

```javascript
// 1. Tải thư viện từ CDN hoặc npm install socket.io-client
// <script src="https://cdn.socket.io/4.7.5/socket.io.min.js"></script>

// 2. Kết nối tới Server Netty port 8085
// Tham số transports giúp test Auto-Fallback (bắt đầu bằng polling)
const socket = io("http://localhost:8085", {
    query: { token: "Alex" },
    transports: ["polling", "websocket"] 
});

socket.on("connect", () => {
    console.log("Connected với ID:", socket.id);
    
    // 3. Xin vào Room
    socket.emit("join_room", "IT-ROOM");
});

// 4. Lắng nghe tin nhắn broadcast
socket.on("chat_message", (data) => {
    console.log(`[${data.sender}]: ${data.content}`);
});

// 5. Hàm gửi tin nhắn
function sendChat() {
    socket.emit("chat_message", {
        room: "IT-ROOM",
        content: "Hello from Socket.IO Client!",
        sender: "Alex"
    });
}
```
