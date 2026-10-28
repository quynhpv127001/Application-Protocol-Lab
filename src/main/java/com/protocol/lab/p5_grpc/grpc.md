# gRPC API Documentation & Testing Guide

## 1. Thông số kết nối gRPC Server
* **Host:** `localhost`
* **gRPC Port:** `9090` (Cấu hình qua `grpc.server.port=9090`)
* **IDL Proto File:** `src/main/proto/order_service.proto`
* **Package Service:** `com.protocol.lab.p5_grpc.generated.OrderGrpcService`

---

## 2. Kiểm thử bằng Postman Native gRPC (Giao diện trực quan)

Postman hỗ trợ trực tiếp giao thức gRPC native từ phiên bản v9+:

1. Mở Postman, bấm nút **New** $\rightarrow$ Chọn **gRPC Request**.
2. **Server URL:** Nhập `localhost:9090`.
3. **Service Definition:** Chọn **Import a .proto file** $\rightarrow$ Trỏ tới tệp `src/main/proto/order_service.proto`.
4. Chọn Service: `OrderGrpcService`.
5. Chọn Method muốn test từ dropdown và dán payload bên dưới.

---

## 3. Lệnh grpcurl & Raw Payload (Copy-Paste Ready)

### 3.1. Unary RPC: `CreateOrder` (1 Request $\rightarrow$ 1 Response)
```bash
grpcurl -plaintext -d '{
  "customerId": "CUST-8888",
  "itemSku": "SERVER-BLADE-GEN11",
  "quantity": 4,
  "price": 2499.50
}' localhost:9090 com.protocol.lab.p5_grpc.generated.OrderGrpcService/CreateOrder
```

**Raw Payload (Dán vào Postman):**
```json
{
  "customerId": "CUST-8888",
  "itemSku": "SERVER-BLADE-GEN11",
  "quantity": 4,
  "price": 2499.50
}
```

---

### 3.2. Server Streaming RPC: `StreamOrdersByCustomer` (1 Request $\rightarrow$ Stream Responses)
Server sẽ liên tục stream trả về danh sách các Order của khách hàng.
```bash
grpcurl -plaintext -d '{
  "customerId": "CUST-8888"
}' localhost:9090 com.protocol.lab.p5_grpc.generated.OrderGrpcService/StreamOrdersByCustomer
```

**Raw Payload:**
```json
{
  "customerId": "CUST-8888"
}
```

---

### 3.3. Client Streaming RPC: `RecordTelemetry` (Stream Requests $\rightarrow$ 1 Response)
Client gửi liên tục các tọa độ GPS / tốc độ xe giao hàng, Server tổng hợp và trả về báo cáo cuối:
```bash
grpcurl -plaintext -d '{
  "orderId": "ORD-1001",
  "latitude": 21.0285,
  "longitude": 105.8542,
  "speedKmh": 45.2
}' localhost:9090 com.protocol.lab.p5_grpc.generated.OrderGrpcService/RecordTelemetry
```

---

### 3.4. Bidirectional Streaming RPC: `LiveDeliveryChannel` (Full-Duplex)
Hai chiều gửi nhận dữ liệu thời gian thực độc lập giữa tài xế và hệ thống điều phối:
```bash
grpcurl -plaintext -d '{
  "orderId": "ORD-1001",
  "sender": "COURIER_DEVICE_01",
  "payload": "Courier arrived at pickup location"
}' localhost:9090 com.protocol.lab.p5_grpc.generated.OrderGrpcService/LiveDeliveryChannel
```

---

### 3.5. Heavy RPC & gRPC Deadlines / Cancellation: `ProcessHeavyOrder`
Kiểm tra cơ chế Server chủ động hủy tác vụ (`Context.current().isCancelled()`) khi Client vượt ngưỡng timeout:
```bash
# Thiết lập deadline tối đa 0.2s (Server xử lý ~0.75s) -> Kích hoạt Status DEADLINE_EXCEEDED (Code 4)
grpcurl -plaintext -max-time 0.2 -d '{
  "orderId": "ORD-HEAVY-001"
}' localhost:9090 com.protocol.lab.p5_grpc.generated.OrderGrpcService/ProcessHeavyOrder
```

---

## 4. Metadata & Distributed Tracing (`x-trace-id`)
Mỗi request gửi tới gRPC Server có thể đính kèm Header Metadata `x-trace-id`:
```bash
grpcurl -plaintext -H "x-trace-id: trace-dev-9999" -d '{"orderId":"ORD-1001"}' localhost:9090 com.protocol.lab.p5_grpc.generated.OrderGrpcService/ProcessHeavyOrder
```
Server Interceptor (`LogAndTraceServerInterceptor`) sẽ tự động trích xuất hoặc sinh mới `x-trace-id` và trả ngược về Client trong Header phản hồi.
