package com.pro.bookingservice.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")

class ApplicationConfigTest {

    @Autowired
    private Environment environment;

    @Test
    @DisplayName("Configuration properties should be loaded correctly")
    void configurationPropertiesShouldBeLoadedCorrectly() {
        assertThat(environment.getProperty("spring.application.name"))
                .isEqualTo("booking-service");

        assertThat(environment.getProperty("server.port"))
                .isEqualTo("8082");

        assertThat(environment.getProperty("server.servlet.context-path"))
                .isEqualTo("/booking-api");

        assertThat(environment.getProperty("eureka.client.fetch-registry"))
                .isEqualTo("true");
    }
}