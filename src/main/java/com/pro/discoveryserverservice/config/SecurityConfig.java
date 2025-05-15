package com.pro.discoveryserverservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the Eureka Server.
 * For local development, security is simplified to allow all requests.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures the security filter chain.
     * Disables CSRF as Eureka clients might not handle it easily.
     * Allows all requests for a simplified local setup.
     *
     * @param http HttpSecurity object to configure.
     * @return The configured SecurityFilterChain.
     * @throws Exception If configuration fails.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF protection - Eureka clients typically don't handle CSRF tokens
                .csrf(csrf -> csrf.disable())
                // Configure authorization rules
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll() // Allow all requests for local development
                );
        // HTTP Basic and session management are not strictly necessary when all requests are permitted.
        return http.build();
    }
}