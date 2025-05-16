package com.pro.authenticationservice.service;



import com.pro.authenticationservice.dto.LoginRequest;
import com.pro.authenticationservice.dto.RegisterRequest;
import com.pro.authenticationservice.exception.UsernameAlreadyExistsException;
import com.pro.authenticationservice.model.JwtResponse;
import com.pro.authenticationservice.model.Role;
import com.pro.authenticationservice.model.TokenValidationResponse;
import com.pro.authenticationservice.model.User;
import com.pro.authenticationservice.repository.UserRepository;
import com.pro.authenticationservice.security.JwtUtils;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {


    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepo,
                       PasswordEncoder encoder,
                       AuthenticationManager authManager,
                       JwtUtils jwtUtils) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.authManager = authManager;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    public void register(RegisterRequest req) {
        if (userRepo.existsByUsername(req.getUsername())) {
            throw new UsernameAlreadyExistsException(req.getUsername());
        }
        var user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setRoles(Set.of(Role.ROLE_USER));
        userRepo.save(user);
    }


    public JwtResponse login(LoginRequest req) {
        var authToken = new UsernamePasswordAuthenticationToken(
                req.getUsername(), req.getPassword());
        var auth = authManager.authenticate(authToken);

        var userDetails = (org.springframework.security.core.userdetails.User)
                auth.getPrincipal();

        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        String token = jwtUtils.generateToken(userDetails.getUsername(), roles);

        // JwtResponse(String token, String type, String username, List<String> roles)
        return new JwtResponse(token, "Bearer", userDetails.getUsername(), roles);
    }


    public TokenValidationResponse validateToken(String token) {
        boolean valid = jwtUtils.validateToken(token);

        List<String> roles = valid
                ? jwtUtils.getRoles(token)
                : Collections.emptyList();

        return new TokenValidationResponse(valid, roles);
    }


}