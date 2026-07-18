package com.protocol.lab.p1_http.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Configuration
@Slf4j
class RestClientConfig {
    @Bean
    public RestClient restClient() {
        // 1. Connection Pool
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(100); // Tối đa 100 connection tổng
        connectionManager.setDefaultMaxPerRoute(20); // Tối đa 20 connection đến 1 host

        // 2. Timeouts
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofSeconds(3)) // Thời gian chờ mượn connection từ pool
                .setResponseTimeout(Timeout.ofSeconds(5)) // Read timeout: Thời gian chờ nhận dữ liệu
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setConnectTimeout(3000); // Connect timeout: Thời gian chờ bắt tay TCP

        return RestClient.builder()
                .requestFactory(factory)
                .build();
    }
}

@Service
@Slf4j
public class ExternalApiClient {
    private final RestClient restClient;

    public ExternalApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Call API bên ngoài.
     * Tích hợp Retry khi gặp sự cố mạng hoặc 5xx Server Error.
     * Tối đa 3 lần, delay 1s, nhân 2 mỗi lần retry.
     */
    @Retryable(
            retryFor = {RestClientException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    public String fetchExternalData(String url) {
        log.info("Attempting to fetch data from: {}", url);
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(String.class);
    }
}
