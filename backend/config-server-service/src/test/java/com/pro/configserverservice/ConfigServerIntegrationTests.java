package com.pro.configserverservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.profiles.active=native",
        "spring.cloud.config.server.native.searchLocations=classpath:/config"
})
class ConfigServerIntegrationTests {

    @LocalServerPort
    private int port;

    private final TestRestTemplate rest = new TestRestTemplate();

    private String baseUrl(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    @DisplayName("GET /{app}/{profile} returns propertySources with our test property")
    void configEndpointShouldReturnProperties() {
        ResponseEntity<Map<String, Object>> resp = rest.exchange(
                baseUrl("/testapp/default"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> body = resp.getBody();
        assertThat(body).isNotNull().containsKey("propertySources");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> sources = (List<Map<String, Object>>) body.get("propertySources");
        assertThat(sources).isNotEmpty();

        boolean found = sources.stream()
                .map(src -> src.get("source"))
                .filter(Map.class::isInstance)
                .map(Map.class::cast)
                .anyMatch(m -> m.containsKey("example.property"));
        assertThat(found).isTrue();
    }

    @Test
    @DisplayName("GET unknown application returns empty propertySources")
    void missingAppReturnsEmptySources() {
        ResponseEntity<Map<String, Object>> resp = rest.exchange(
                baseUrl("/does-not-exist/default"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // Spring Cloud Config native mode returns 200 with an empty environment
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map<String, Object> body = resp.getBody();
        assertThat(body).isNotNull().containsKey("propertySources");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> sources = (List<Map<String, Object>>) body.get("propertySources");
        assertThat(sources).isEmpty();
    }
}