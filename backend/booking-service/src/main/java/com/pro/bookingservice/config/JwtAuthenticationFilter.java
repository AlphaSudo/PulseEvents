package com.pro.bookingservice.config;

import com.pro.bookingservice.exception.ErrorResponse;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${auth.service.url:https://authentication-service}")
    private String authServiceUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public JwtAuthenticationFilter(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {


        String authHeader = request.getHeader("Authorization");

        // Skip filter if no Authorization header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        try {
            // Create a request body with proper typing
            Map<String, String> requestBody = Collections.singletonMap("token", jwt);

            // Call authentication service to validate token with proper response typing
            TokenValidationResponse validationResponse = restTemplate.postForObject(
                    authServiceUrl + "/auth/validate",
                    requestBody,
                    TokenValidationResponse.class
            );


            if (validationResponse != null && validationResponse.isValid()) {
                // Get user information from a token (this will depend on your JWT implementation)
                // For this example, we'll extract userId from the token claim or use another endpoint
                String userId = extractUserIdFromToken(jwt);
                List<String> roles = validationResponse.getRoles();

                // Create an authentication token with authorities
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userId, // principal
                        null,   // credentials
                        roles.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList())
                );

                // Add request details to an auth token
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set authentication in Security Context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }


            filterChain.doFilter(request, response);

        } catch (Exception e) {
            // Handle authentication errors
            handleAuthenticationError(response, e.getMessage());
        }
    }
    /**
     * Extract user ID from the JWT token
     * This method would typically decode the JWT and extract a claim
     *
     * @param token the JWT token
     * @return the user ID
     */
    private String extractUserIdFromToken(String token) {
        // Using ParameterizedTypeReference to avoid unchecked assignment warnings
        try {
            Map<String, String> requestBody = Collections.singletonMap("token", token);

            // Use exchange with ParameterizedTypeReference for type-safe response
            ParameterizedTypeReference<Map<String, Object>> responseType =
                    new ParameterizedTypeReference<>() {};

            Map<String, Object> userInfo = restTemplate.exchange(
                    authServiceUrl + "/auth/user-info",
                    HttpMethod.POST,
                    new HttpEntity<>(requestBody),
                    responseType
            ).getBody();

            if (userInfo != null && userInfo.containsKey("userId")) {
                return userInfo.get("userId").toString();
            }

            // Fallback if userId not available
            return "anonymous";
        } catch (Exception e) {
            // Log error and return fallback value
            logger.error("Error extracting user ID from token", e);
            return "anonymous";
        }
    }



    private void handleAuthenticationError(HttpServletResponse response, String errorMessage) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse errorResponse = new ErrorResponse("Authentication failed: " + errorMessage);
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
    /**
     * Response class for token validation to avoid unchecked casts
     */
    private static class TokenValidationResponse {
        private boolean valid;
        private String username;
        private List<String> roles;

        // Default constructor for Jackson
        public TokenValidationResponse() {}

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public List<String> getRoles() {
            return roles != null ? roles : Collections.emptyList();
        }

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }
    }
}
