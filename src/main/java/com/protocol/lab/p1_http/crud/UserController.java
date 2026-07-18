package com.protocol.lab.p1_http.crud;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Controller minh họa các phương thức CRUD cơ bản trong REST/HTTP.
 * Tập trung vào Idempotency và Validation.
 */
@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {

    // Giả lập Database (Thread-safe)
    private final Map<String, User> userDatabase = new ConcurrentHashMap<>();

    /**
     * GET: Lấy danh sách (Có phân trang giả lập)
     */
    @GetMapping
    public ResponseEntity<List<User>> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        log.info("Fetching users page {} with limit {}", page, limit);
        return ResponseEntity.ok(new ArrayList<>(userDatabase.values()));
    }

    /**
     * GET: Lấy chi tiết theo ID (Path Variable)
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        log.info("Fetching user with id {}", id);
        User user = userDatabase.get(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(user);
    }

    /**
     * POST: Tạo mới Resource (Validation)
     * POST KHÔNG có tính chất Idempotent (gọi nhiều lần sẽ tạo nhiều resource).
     */
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        log.info("Creating new user: {}", user.getEmail());
        user.setId(UUID.randomUUID().toString());
        userDatabase.put(user.getId(), user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    /**
     * PUT: Cập nhật toàn bộ Resource (Idempotent)
     * PUT BẮT BUỘC phải là Idempotent: gọi 1 lần hay 100 lần kết quả vẫn vậy.
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> replaceUser(@PathVariable String id, @Valid @RequestBody User user) {
        log.info("Replacing user {}: {}", id, user.getEmail());
        if (!userDatabase.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        user.setId(id); // Giữ nguyên ID gốc
        userDatabase.put(id, user); // Đè toàn bộ object
        return ResponseEntity.ok(user);
    }

    /**
     * PATCH: Cập nhật 1 phần Resource (Không thay thế toàn bộ)
     */
    @PatchMapping("/{id}")
    public ResponseEntity<User> partialUpdateUser(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        log.info("Partial update user {} with fields {}", id, updates.keySet());
        User existingUser = userDatabase.get(id);
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        if (updates.containsKey("name")) {
            existingUser.setName((String) updates.get("name"));
        }
        if (updates.containsKey("email")) {
            existingUser.setEmail((String) updates.get("email"));
        }
        return ResponseEntity.ok(existingUser);
    }

    /**
     * DELETE: Xóa Resource (Idempotent)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        log.info("Deleting user {}", id);
        userDatabase.remove(id); // Xóa
        return ResponseEntity.noContent().build();
    }
}
