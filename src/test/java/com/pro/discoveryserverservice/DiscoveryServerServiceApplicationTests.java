package com.pro.discoveryserverservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test class for the main application {@link DiscoveryServerServiceApplication}.
 * Verifies that the Spring application context loads successfully.
 */
@SpringBootTest // This annotation is used to load the full Spring application context for testing.
class DiscoveryServerServiceApplicationTests {

    /**
     * Tests if the Spring application context loads without errors.
     * If the application context fails to load, this test will fail,
     * indicating a problem with the application's configuration or component scanning.
     * @param context The application context injected by Spring Boot test support.
     */
    @Test
    void contextLoads(ApplicationContext context) {
        // Check that the context is not null, meaning it loaded.
        assertNotNull(context, "The application context should have loaded.");

        // You can add further assertions here if needed, for example,
        // to check if specific beans are present in the context.
        // System.out.println("Application context loaded successfully for DiscoveryServerServiceApplication.");
    }
}