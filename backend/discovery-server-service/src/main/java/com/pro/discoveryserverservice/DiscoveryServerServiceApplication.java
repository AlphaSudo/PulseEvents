package com.pro.discoveryserverservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Main application class for the Eureka Discovery Server.
 * This class serves as the entry point for the Spring Boot application.
 */
@SpringBootApplication
@EnableEurekaServer
// Suppress FinalClass for this specific class
public class DiscoveryServerServiceApplication {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private DiscoveryServerServiceApplication() {
        // Utility class
    }

    /**
     * Main entry point for the Spring Boot application.
     *
     * @param args Command line arguments passed to the application.
     */
    public static void main(final String[] args) {
        SpringApplication.run(DiscoveryServerServiceApplication.class, args);
    }

}
