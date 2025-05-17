package com.pro.authenticationservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        // This property should disable the Spring Cloud Config client for these tests
        "spring.cloud.config.enabled=false",
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=LEGACY",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false",
        "jwt.secret=z4+W0v3h4ZqQS/sYQmFIX9aZZlI3Z0pQOxERa/NSnKI=",
        "jwt.expiration-ms=3600000",
        "app.cors.allowed-origins=https://localhost:3000"

})

class AuthenticationServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
