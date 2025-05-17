package com.pro.authenticationservice.controller;

import com.pro.authenticationservice.dto.LoginRequest;
import com.pro.authenticationservice.dto.RegisterRequest;
import com.pro.authenticationservice.model.JwtResponse;
import com.pro.authenticationservice.model.TokenValidationResponse;
import com.pro.authenticationservice.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authSvc;

    public AuthController(AuthService authSvc) {
        this.authSvc = authSvc;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @NotNull @Valid @RequestBody RegisterRequest req) {

            authSvc.register(req);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("User registered");


    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @NotNull @Valid @RequestBody LoginRequest req) {
            System.out.println("AuthController: Login request received for user: " + req.getUsername());
            try {
                JwtResponse resp = authSvc.login(req);
                System.out.println("AuthController: Login successful for user: " + resp.getUsername());
                System.out.println("AuthController: Token generated: " + (resp.getToken() != null ? "yes" : "no"));
                System.out.println("AuthController: User roles: " + resp.getRoles());
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(resp);
            } catch (Exception e) {
                System.out.println("AuthController: Login failed: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
    }

    @GetMapping("/validate")
    public ResponseEntity<TokenValidationResponse> validate(
            @RequestParam String token) {

        TokenValidationResponse resp = authSvc.validateToken(token);
        if (!resp.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
        }
        return ResponseEntity.ok(resp);
    }
}
