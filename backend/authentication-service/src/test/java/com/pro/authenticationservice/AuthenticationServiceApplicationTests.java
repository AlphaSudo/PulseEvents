package com.pro.authenticationservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        // This property should disable the Spring Cloud Config client for these tests
        "spring.cloud.config.enabled=false",
})

class AuthenticationServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
