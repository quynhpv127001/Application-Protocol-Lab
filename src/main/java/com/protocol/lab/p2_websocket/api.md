# WebSocket / STOMP Documentation

## 1. Kết nối & Xác thực (Handshake)

**Endpoint:** `ws://localhost:8080/ws`
**Sub-protocol:** STOMP

**Authentication:** 
Khi STOMP Client gửi frame `CONNECT`, bắt buộc phải truyền Header `Authorization`. Trong bài Lab này, Server dùng luôn giá trị truyền vào làm Username để mô phỏng.

**Ví dụ Code Client (JavaScript):**
```javascript
// Yêu cầu thư viện: sockjs-client & stompjs
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

stompClient.connect(
    { 'Authorization': 'Alex' }, // Header xác thực
    function (frame) {
        console.log('Connected: ' + frame);
    },
    function (error) {
        console.error('Lỗi kết nối:', error);
    }
);
```

## 2. Subscribe (Lắng nghe tin nhắn từ Room)

**Topic:** `/topic/room/{roomName}` (Ví dụ: `/topic/room/IT-ROOM`)

**Ví dụ Code Client:**
```javascript
stompClient.subscribe('/topic/room/IT-ROOM', function (payload) {
    const message = JSON.parse(payload.body);
    console.log(`[${message.sender}] gửi: ${message.content}`);
});
```

## 3. Publish (Gửi tin nhắn vào Room)

**Destination:** `/app/chat`
**Payload (JSON):** Tương ứng với class `ChatMessage.java`.

**Raw JSON Body để test:**
```json
{
  "content": "Hello anh em trong phòng!",
  "room": "IT-ROOM",
  "type": "CHAT"
}
```

**Ví dụ Code Client gửi tin:**
```javascript
const chatMessage = {
    content: "Hello anh em trong phòng!",
    room: "IT-ROOM",
    type: "CHAT"
};

// Tham số 2 là Headers, Tham số 3 là Body dạng chuỗi JSON
stompClient.send("/app/chat", {}, JSON.stringify(chatMessage));
```
