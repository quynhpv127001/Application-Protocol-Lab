# HTTP / REST Module API Documentation

## 1. CRUD API

**Mục đích:** Xử lý người dùng với các chuẩn REST (Idempotent, Validation).

### GET - Lấy danh sách

```bash
curl --location 'http://localhost:8081/api/v1/users?page=1&limit=10'
```

### POST - Tạo người dùng

```bash
curl --location 'http://localhost:8081/api/v1/users' \
--header 'Content-Type: application/json' \
--data '{
    "name": "Nguyen Van A",
    "email": "nguyenvana@gmail.com"
}'
```

**Ví dụ Response 201 Created:**
```json
{
    "id": "e4b52b21-4f11-49fa-b7d1-dc7e954fa02d",
    "name": "Nguyen Van A",
    "email": "nguyenvana@gmail.com"
}
```

### PUT - Cập nhật toàn bộ (Idempotent)

```bash
curl --location --request PUT 'http://localhost:8081/api/v1/users/e4b52b21-4f11-49fa-b7d1-dc7e954fa02d' \
--header 'Content-Type: application/json' \
--data '{
    "name": "Nguyen Van A Updated",
    "email": "nguyenvana2@gmail.com"
}'
```

### PATCH - Cập nhật một phần

```bash
curl --location --request PATCH 'http://localhost:8081/api/v1/users/e4b52b21-4f11-49fa-b7d1-dc7e954fa02d' \
--header 'Content-Type: application/json' \
--data '{
    "name": "Nguyen Van B"
}'
```

### DELETE - Xóa người dùng

```bash
curl --location --request DELETE 'http://localhost:8081/api/v1/users/e4b52b21-4f11-49fa-b7d1-dc7e954fa02d'
```


## 2. File Transfer API

### Multipart Upload

```bash
curl --location 'http://localhost:8081/api/v1/files/upload' \
--form 'file=@"/absolute/path/to/your/file.txt"'
```

### Chunked Download (Streaming)

```bash
curl --location 'http://localhost:8081/api/v1/files/download-stream/file.txt' -O
```


## 3. Streaming & Advanced API

### Server-Sent Events (SSE)

Cứ mỗi giây server sẽ trả về một luồng dữ liệu mà không đóng connection.

```bash
curl --location 'http://localhost:8081/api/v1/streaming/sse'
```

### Long Polling

Mở tab 1, request này sẽ treo trong tối đa 30s:
```bash
curl --location 'http://localhost:8081/api/v1/streaming/long-polling/12345'
```

Mở tab 2, trigger kết quả trả về cho tab 1 ngay lập tức:
```bash
curl --location 'http://localhost:8081/api/v1/streaming/long-polling/12345/trigger' \
--header 'Content-Type: text/plain' \
--data 'Done computing'
```

### Authentication Header (Bearer)

```bash
curl --location 'http://localhost:8081/api/v1/streaming/auth' \
--header 'Authorization: Bearer valid-token-123'
```
