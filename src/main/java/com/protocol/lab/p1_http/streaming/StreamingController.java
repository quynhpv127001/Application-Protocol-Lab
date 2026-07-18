package com.protocol.lab.p1_http.streaming;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.context.request.async.DeferredResult;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/v1/streaming")
@Slf4j
public class StreamingController {

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final ConcurrentHashMap<String, DeferredResult<String>> longPollingRequests = new ConcurrentHashMap<>();

    /**
     * Server-Sent Events (SSE): Push dữ liệu 1 chiều từ Server -> Client.
     * Liên tục giữ kết nối mở. Rất hữu ích cho Realtime dashboard.
     */
    @GetMapping(path = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamEvents() {
        // Timeout 60s
        SseEmitter emitter = new SseEmitter(60000L);
        
        executor.execute(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    emitter.send(SseEmitter.event()
                            .id(String.valueOf(i))
                            .name("message")
                            .data("Event number " + i + " at " + System.currentTimeMillis()));
                    Thread.sleep(1000); // Giả lập dữ liệu sinh ra mỗi giây
                }
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }

    /**
     * Long-Polling: Giữ Request (Treo request) cho đến khi có dữ liệu mới trả về.
     * Tránh tốn connection liên tục, nhưng nếu timeout client phải gọi lại.
     */
    @GetMapping("/long-polling/{requestId}")
    public DeferredResult<String> longPolling(@PathVariable String requestId) {
        log.info("Received long-polling request {}", requestId);
        
        // Treo tối đa 30s
        DeferredResult<String> output = new DeferredResult<>(30000L);
        
        output.onTimeout(() -> {
            log.info("Request {} timed out", requestId);
            output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("No new data available."));
            longPollingRequests.remove(requestId);
        });

        longPollingRequests.put(requestId, output);
        return output;
    }

    /**
     * API này giả lập có một event nào đó xảy ra, kích hoạt trả kết quả cho request đang bị treo.
     */
    @PostMapping("/long-polling/{requestId}/trigger")
    public ResponseEntity<String> triggerLongPolling(@PathVariable String requestId, @RequestBody String data) {
        DeferredResult<String> result = longPollingRequests.remove(requestId);
        if (result != null) {
            result.setResult("Data ready: " + data);
            return ResponseEntity.ok("Triggered successfully");
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Ví dụ truyền Auth Header để xác thực.
     */
    @GetMapping("/auth")
    public ResponseEntity<String> secureEndpoint(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Bearer token");
        }
        String token = authHeader.substring(7);
        // Trong thực tế, bạn sẽ parse JWT ở đây.
        if (!"valid-token-123".equals(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
        return ResponseEntity.ok("Welcome! You are authenticated.");
    }
}
