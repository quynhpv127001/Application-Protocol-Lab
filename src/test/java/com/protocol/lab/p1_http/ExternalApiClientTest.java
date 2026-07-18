package com.protocol.lab.p1_http;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.protocol.lab.p1_http.client.ExternalApiClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@WireMockTest(httpPort = 8089)
class ExternalApiClientTest {

    @Autowired
    private ExternalApiClient apiClient;

    @Test
    void testRetryLogic_SuccessOnThirdTry() {
        // Lần 1, 2 trả về 500. Lần 3 trả về 200.
        stubFor(get(urlEqualTo("/test-retry"))
                .inScenario("Retry Scenario")
                .whenScenarioStateIs(com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED)
                .willReturn(serverError())
                .willSetStateTo("Attempt 2"));

        stubFor(get(urlEqualTo("/test-retry"))
                .inScenario("Retry Scenario")
                .whenScenarioStateIs("Attempt 2")
                .willReturn(serverError())
                .willSetStateTo("Attempt 3"));

        stubFor(get(urlEqualTo("/test-retry"))
                .inScenario("Retry Scenario")
                .whenScenarioStateIs("Attempt 3")
                .willReturn(ok("Success!")));

        String result = apiClient.fetchExternalData("http://localhost:8089/test-retry");
        assertEquals("Success!", result);
        
        verify(3, getRequestedFor(urlEqualTo("/test-retry")));
    }
}
