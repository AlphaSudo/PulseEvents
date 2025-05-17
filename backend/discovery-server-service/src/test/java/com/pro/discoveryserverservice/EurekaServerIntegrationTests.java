package com.pro.discoveryserverservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "management.info.env.enabled=true",
        "spring.cloud.config.enabled=false",
        "management.endpoints.web.exposure.include=health,info",
        "logging.level.org.springframework.boot.actuate.info=DEBUG",
        "info.app.name=discovery-server-service",
        "info.app.description=Eureka Discovery Server (Local Development Setup)"
})
public class EurekaServerIntegrationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Eureka Server context loads and dashboard is accessible")
    void eurekaDashboardIsAccessible() {
        ResponseEntity<String> response = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        // Check for a more stable and prominent element on the Eureka dashboard
        assertThat(response.getBody()).contains("<h1>System Status</h1>");
    }


    @Test
    @DisplayName("Eureka applications endpoint is accessible and returns JSON")
    void eurekaApplicationsEndpointIsAccessible() {
        ResponseEntity<String> response = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/eureka/apps", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        // Check for the "applications" key, indicating a JSON response structure
        assertThat(response.getBody()).contains("\"applications\"");
    }


    @Test
    @DisplayName("Health actuator endpoint is accessible and shows UP status")
    @SuppressWarnings("unchecked")
    void healthActuatorEndpointIsUp() {
        ResponseEntity<Map<String, Object>> response = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/actuator/health", (Class<Map<String, Object>>)(Class<?>)Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo("UP");
    }

    @Test
    @DisplayName("Info actuator endpoint is accessible and contains application name")
    @SuppressWarnings("unchecked")
    void infoActuatorEndpointContainsAppName() {
        ResponseEntity<Map<String, Object>> response = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/actuator/info", (Class<Map<String, Object>>)(Class<?>)Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Print the actual response body to help diagnose
        System.out.println("Actual /actuator/info response: " + response.getBody());

        Map<String, Object> appInfo = (Map<String, Object>) response.getBody().get("app");

        // This assertion is failing because the "app" key is not in the response.
        // This indicates that the management.info.app properties from application.yml
        // are not being correctly reflected in the /actuator/info output during the test.
        // The test logic itself is correct for what it's trying to verify.
        assertThat(appInfo).isNotNull(); // This will likely still fail until the root cause is found

        // These lines will only be reached if appInfo is not null
        assertThat(appInfo.get("name")).isEqualTo("discovery-server-service");
        assertThat(appInfo.get("description")).isEqualTo("Eureka Discovery Server (Local Development Setup)");
    }


    // You could add more tests here, for example,
    // - Test specific Eureka server behaviors if you had more complex configurations.
    // - Test client registration and deregistration if you were to set up a dummy client within the test.
}
