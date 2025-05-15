package com.pro.configserverservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConfigServerActuatorTests {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void healthEndpointUp() {
        @SuppressWarnings("unchecked")
        Map<String, Object> health = restTemplate
                .getForObject("http://localhost:" + port + "/actuator/health", Map.class);

        assertThat(health).containsEntry("status", "UP");
    }
}