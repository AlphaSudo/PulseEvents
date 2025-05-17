package com.pro.apigatewayservice.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "cors.allowed-origins=https://localhost:3000,https://localhost:3000" // Changed a property name and added HTTPS
})
@Import(GatewayCorsConfig.class) // Import the configuration class
class GatewayCorsConfigTest {

    @LocalServerPort
    private int port; // Made field private



    private WebTestClient client;

    @BeforeEach
    void setUp() {
        // Create WebTestClient bound to the running server
        client = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port) // or https if configured
                .build();

        // Alternative if you need to bind to application context:
        // client = WebTestClient.bindToApplicationContext(context).build();
    }

    @Test
    void whenOptionsFromAllowedOrigin_thenReturnsCorsHeaders() {
        client.options().uri("/any-path")
                .header("Origin", "https://localhost:3000") // Using HTTPS
                .header("Access-Control-Request-Method", "GET")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Access-Control-Allow-Origin", "https://localhost:3000")
                .expectHeader().exists("Access-Control-Allow-Methods")
                .expectHeader().valueEquals("Access-Control-Allow-Credentials", "true");
    }

    @Test
    void whenOptionsFromNotAllowedOrigin_thenForbidden() {
        client.options().uri("/any-path")
                .header("Origin", "https://evil.com")
                .header("Access-Control-Request-Method", "GET")
                .exchange()
                .expectStatus().isForbidden();
    }
}