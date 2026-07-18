# WebSocket / STOMP Documentation

Sử dụng Extension **STOMP WebSocket Client** để test các chức năng dưới đây.

## 1. Kết nối (Handshake)

- **URL**: `http://localhost:8080/ws`
- **Username**: `Alex` (Server dùng trường này làm Username)
- **Password**: `123456` (Bắt buộc phải nhập đúng `123456` để qua bước xác thực)

## 2. Lắng nghe tin nhắn (Subscribe)

- **Topic**: `/topic/room/IT-ROOM`

## 3. Gửi tin nhắn (Publish)

- **Destination**: `/app/chat`
- **Message to send**:

```json
{
  "content": "Hello anh em, tôi test bằng Extension!",
  "room": "IT-ROOM",
  "type": "CHAT"
}
```

---

## 4. Tham khảo Code Client (Cho Frontend)

```javascript
// Yêu cầu thư viện: sockjs-client & stompjs
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

// 4.1 Kết nối
stompClient.connect({ 'login': 'Alex', 'passcode': '123456' }, function (frame) {
    console.log('Connected: ' + frame);

    // 4.2 Subscribe
    stompClient.subscribe('/topic/room/IT-ROOM', function (payload) {
        const message = JSON.parse(payload.body);
        console.log(`[${message.sender}]: ${message.content}`);
    });
});

// 4.3 Publish
function sendChat() {
    const chatMessage = {
        content: "Hello FE Test!",
        room: "IT-ROOM",
        type: "CHAT"
    };
    stompClient.send("/app/chat", {}, JSON.stringify(chatMessage));
}
```
