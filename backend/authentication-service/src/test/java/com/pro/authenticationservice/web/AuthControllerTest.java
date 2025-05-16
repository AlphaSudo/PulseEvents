package com.pro.authenticationservice.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pro.authenticationservice.controller.AuthController;
import com.pro.authenticationservice.dto.LoginRequest;
import com.pro.authenticationservice.dto.RegisterRequest;
import com.pro.authenticationservice.exception.GlobalExceptionHandler;
import com.pro.authenticationservice.model.JwtResponse;
import com.pro.authenticationservice.security.JwtUtils;
import com.pro.authenticationservice.security.UserDetailsServiceImpl; // Import UserDetailsServiceImpl
import com.pro.authenticationservice.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(GlobalExceptionHandler.class)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService; // ADD THIS MOCK

    private final ObjectMapper om = new ObjectMapper();

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(AbstractHttpConfigurer::disable)
                    .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/auth/register", "/auth/login", "/auth/validate").permitAll()
                            .anyRequest().authenticated()
                    );
            // The actual JwtAuthenticationFilter will be used, now with its dependencies (JwtUtils, UserDetailsServiceImpl) mocked.
            return http.build();
        }
    }
    @Test
    void loginShouldReturnTokenWhenCredentialsValid() throws Exception {
        var req = new LoginRequest("user", "pass");
        var jwtResp = new JwtResponse("jwt-token", "Bearer", "user", List.of("ROLE_USER"));

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(jwtResp);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));

        verify(authService).login(any(LoginRequest.class));
    }


    @Test
    void loginShouldReturnUnauthorizedWhenCredentialsInvalid() throws Exception {
        var req = new LoginRequest("user", "wrong");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());

        verify(authService).login(any(LoginRequest.class));
    }


    @Test
    void registerShouldCreateUserWhenValid() throws Exception {
        var req = new RegisterRequest("newUser", "secretChase", "new@example.com");

        doNothing().when(authService).register(any(RegisterRequest.class));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered"));

        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    void registerShouldReturnBadRequestWhenUserExists() throws Exception {
        var req = new RegisterRequest("exists", "secretChase", "e@e.com");

        doThrow(new IllegalArgumentException("Username already taken"))
                .when(authService).register(any(RegisterRequest.class));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username already taken"));

        verify(authService).register(any(RegisterRequest.class));
    }
}