package com.protocol.lab.p1_http.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/files")
@Slf4j
public class FileController {

    // Lưu file hoàn toàn trên RAM (Không lưu ra ổ đĩa)
    private final ConcurrentHashMap<String, byte[]> fileStorage = new ConcurrentHashMap<>();

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }
        try {
            String fileName = file.getOriginalFilename();
            if (fileName == null) fileName = "unknown_file_" + System.currentTimeMillis();
            
            // Đẩy bytes của file vào HashMap (RAM)
            fileStorage.put(fileName, file.getBytes());
            log.info("Uploaded file to RAM: {}", fileName);
            return ResponseEntity.ok(fileName);
        } catch (IOException e) {
            log.error("Failed to upload file", e);
            return ResponseEntity.internalServerError().body("Upload failed");
        }
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        byte[] data = fileStorage.get(fileName);
        if (data == null) {
            return ResponseEntity.notFound().build();
        }
        
        ByteArrayResource resource = new ByteArrayResource(data) {
            @Override
            public String getFilename() {
                return fileName;
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @GetMapping("/download-stream/{fileName}")
    public ResponseEntity<StreamingResponseBody> streamFile(@PathVariable String fileName) {
        byte[] data = fileStorage.get(fileName);
        if (data == null) {
            return ResponseEntity.notFound().build();
        }

        StreamingResponseBody responseBody = outputStream -> {
            try (InputStream inputStream = new ByteArrayInputStream(data)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    outputStream.flush(); // Đẩy chunk qua mạng ngay lập tức
                }
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(responseBody);
    }
}
