package com.pro.apigatewayservice.security;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import org.springframework.test.web.reactive.server.WebTestClient;

import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureWebTestClient
@Import({ GatewaySecurityConfigTest.TestController.class,  // <-- your nested controller
        GatewaySecurityConfig.class })



public class GatewaySecurityConfigTest {
    @Autowired
    private WebTestClient webTestClient;



    @Test
    void whenNoAuth_thenUnauthorized() {
        webTestClient.get()
                .uri("/test")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void whenMockUser_thenOk() {
        webTestClient.mutateWith(mockUser("alice"))
                .get().uri("/test")
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("it works");
    }

    // A tiny controller just for test –
    // all routes are secured by default in SecurityConfig
    @RestController

    static class TestController {
        @org.springframework.web.bind.annotation.GetMapping(path = "/test", produces = "text/plain")
        public Mono<String> test() {
            return Mono.just("it works");
        }
    }
}
