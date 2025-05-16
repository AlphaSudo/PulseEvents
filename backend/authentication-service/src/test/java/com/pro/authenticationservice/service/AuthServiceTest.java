package com.pro.authenticationservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;


import com.pro.authenticationservice.dto.LoginRequest;
import com.pro.authenticationservice.dto.RegisterRequest;
import com.pro.authenticationservice.exception.UsernameAlreadyExistsException;
import com.pro.authenticationservice.model.JwtResponse;
import com.pro.authenticationservice.model.Role;
import com.pro.authenticationservice.model.TokenValidationResponse;
import com.pro.authenticationservice.model.User;
import com.pro.authenticationservice.repository.UserRepository;
import com.pro.authenticationservice.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    private final String rawPassword = "secret";
    private final String encodedPassword = "encodedSecret";
    private final String username = "johndoe";
    private final List<String> roles = List.of("ROLE_USER");
    private final String token = "jwt-token";

    @BeforeEach
    void setUp() {
        // common stubbing can go here if needed
    }

    @Test
    void register_success() {
        // given
        RegisterRequest req = new RegisterRequest();
        req.setUsername(username);
        req.setPassword(rawPassword);

        when(userRepo.existsByUsername(username)).thenReturn(false);
        when(encoder.encode(rawPassword)).thenReturn(encodedPassword);

        // when
        authService.register(req);

        // then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepo).save(userCaptor.capture());
        User saved = userCaptor.getValue();

        assertEquals(username, saved.getUsername());
        assertEquals(encodedPassword, saved.getPassword());
        assertTrue(saved.getRoles().contains(Role.ROLE_USER));
    }

    @Test
    void register_usernameTaken_throws() {
        // given
        RegisterRequest req = new RegisterRequest();
        req.setUsername(username);
        req.setPassword(rawPassword);

        when(userRepo.existsByUsername(username)).thenReturn(true);

        // when / then
        UsernameAlreadyExistsException ex =
                assertThrows(UsernameAlreadyExistsException.class,
                        () -> authService.register(req));
        assertEquals("Username '" + username + "' is already taken",
                ex.getMessage());

        verify(userRepo, never()).save(any());

    }

    @Test
    void login_success() {
        // given
        LoginRequest req = new LoginRequest();
        req.setUsername(username);
        req.setPassword(rawPassword);

        // create a UserDetails principal
        GrantedAuthority auth = new SimpleGrantedAuthority("ROLE_USER");
        UserDetails principal = new org.springframework.security.core.userdetails.User(
                username, encodedPassword, List.of(auth)
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal, rawPassword, List.of(auth)
        );

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtils.generateToken(username, roles)).thenReturn(token);

        // when
        JwtResponse resp = authService.login(req);

        // then
        assertNotNull(resp);
        assertEquals(token, resp.getToken());
        assertEquals("Bearer", resp.getType());
        assertEquals(username, resp.getUsername());
        assertEquals(roles, resp.getRoles());
    }

    @Test
    void login_badCredentials_throws() {
        // given
        LoginRequest req = new LoginRequest();
        req.setUsername(username);
        req.setPassword(rawPassword);

        when(authManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad creds"));

        // when / then
        assertThrows(BadCredentialsException.class, () -> authService.login(req));
    }

    @Test
    void validateToken_valid() {
        // given
        when(jwtUtils.validateToken(token)).thenReturn(true);
        when(jwtUtils.getRoles(token)).thenReturn(roles);

        // when
        TokenValidationResponse resp = authService.validateToken(token);

        // then
        assertTrue(resp.isValid());
        assertEquals(roles, resp.getRoles());
    }

    @Test
    void validateToken_invalid() {
        // given
        when(jwtUtils.validateToken(token)).thenReturn(false);

        // when
        TokenValidationResponse resp = authService.validateToken(token);

        // then
        assertFalse(resp.isValid());
        assertTrue(resp.getRoles().isEmpty());
    }
}
