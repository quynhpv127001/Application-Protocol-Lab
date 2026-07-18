package com.protocol.lab.p1_http.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/v1/files")
@Slf4j
public class FileController {

    private final Path uploadDir = Paths.get(System.getProperty("java.io.tmpdir"), "uploads");

    public FileController() throws IOException {
        Files.createDirectories(uploadDir);
    }

    /**
     * Multipart Upload: Upload file.
     * Cần giới hạn max-file-size trong application.yml để tránh OOM.
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }
        try {
            Path target = uploadDir.resolve(file.getOriginalFilename());
            file.transferTo(target);
            log.info("Uploaded file to {}", target);
            return ResponseEntity.ok("File uploaded successfully to: " + target.toString());
        } catch (IOException e) {
            log.error("Failed to upload file", e);
            return ResponseEntity.internalServerError().body("Upload failed");
        }
    }

    /**
     * Download file thông thường (Load vào RAM rồi gửi đi)
     * Rất nguy hiểm nếu file lớn, dễ gây OutOfMemoryError (OOM).
     */
    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            Path file = uploadDir.resolve(fileName);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Streaming Download: Dùng cho file lớn.
     * Đọc file từng chunk nhỏ rồi đẩy thẳng qua network, không nạp toàn bộ vào RAM.
     */
    @GetMapping("/download-stream/{fileName}")
    public ResponseEntity<StreamingResponseBody> streamFile(@PathVariable String fileName) {
        Path file = uploadDir.resolve(fileName);
        if (!Files.exists(file)) {
            return ResponseEntity.notFound().build();
        }

        StreamingResponseBody responseBody = outputStream -> {
            try (InputStream inputStream = Files.newInputStream(file)) {
                byte[] buffer = new byte[8192]; // 8KB chunk
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(responseBody);
    }
}
