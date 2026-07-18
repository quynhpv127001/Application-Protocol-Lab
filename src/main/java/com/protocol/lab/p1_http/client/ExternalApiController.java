package com.protocol.lab.p1_http.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/external-api")
public class ExternalApiController {
    
    private final ExternalApiClient apiClient;
    
    public ExternalApiController(ExternalApiClient apiClient) {
        this.apiClient = apiClient;
    }
    
    @GetMapping("/fetch")
    public ResponseEntity<String> fetchExternal(@RequestParam String url) {
        try {
            String result = apiClient.fetchExternalData(url);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching from external API: " + e.getMessage());
        }
    }
}
