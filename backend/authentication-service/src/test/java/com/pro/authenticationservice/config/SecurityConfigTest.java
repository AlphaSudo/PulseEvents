package com.pro.authenticationservice.config;



import com.pro.authenticationservice.security.JwtAuthenticationFilter;
import com.pro.authenticationservice.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.userdetails.DaoAuthenticationConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {
    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtAuthenticationFilter jwtFilter;

    private  SecurityConfig config ;

    @Mock
    private HttpSecurity httpSecurity;

    @Mock
    private CsrfConfigurer<HttpSecurity> csrfConfigurer;

    @Mock
    private SessionManagementConfigurer<HttpSecurity> sessionManagementConfigurer;

    @Mock
    private AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authRegistry;

    @Mock
    private AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizedUrl authorizedUrlForMatchers;

    @Mock
    private AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizedUrl authorizedUrlForAny;



    @Mock
    private SecurityFilterChain securityFilterChain;

    @Captor
    private ArgumentCaptor<Customizer<CsrfConfigurer<HttpSecurity>>> csrfCustomizerCaptor;

    @Captor
    private ArgumentCaptor<Customizer<SessionManagementConfigurer<HttpSecurity>>> sessionMgmtCustomizerCaptor;

    @Captor
    private ArgumentCaptor<Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry>>
            authorizeRequestsCustomizerCaptor;

    @InjectMocks
    private SecurityConfig securityConfig;
    @Mock
    private AuthenticationManagerBuilder authBuilder;
    @Mock
    private DaoAuthenticationConfigurer<AuthenticationManagerBuilder, UserDetailsService> daoConfigurer;

    @Mock
    private AuthenticationManager authenticationManager;

    @Captor
    private ArgumentCaptor<PasswordEncoder> encoderCaptor;
    @BeforeEach
    void setUp() {
        // pass both required constructor args
        config = new SecurityConfig(userDetailsService, jwtFilter);
    }

    @Test
    void authManager_shouldWireUserDetailsServiceAndPasswordEncoder() throws Exception {
        // Arrange: stub httpSecurity.getSharedObject(...) to return our mock builder
        when(httpSecurity.getSharedObject(AuthenticationManagerBuilder.class))
                .thenReturn(authBuilder);

        // Stub the fluent calls on the builder
        when(authBuilder.userDetailsService(userDetailsService))
                .thenReturn(daoConfigurer);
        when(daoConfigurer.passwordEncoder(any(PasswordEncoder.class)))
                .thenReturn(daoConfigurer);

        // Stub build()
        when(authBuilder.build())
                .thenReturn(authenticationManager);

        // Act
        AuthenticationManager result = securityConfig.authManager(httpSecurity);

        // Assert
        assertSame(authenticationManager, result);

        // Verify userDetailsService(...) was called on the builder
        verify(authBuilder).userDetailsService(userDetailsService);

        // Capture and assert that a BCryptPasswordEncoder was provided
        verify(daoConfigurer).passwordEncoder(encoderCaptor.capture());
        PasswordEncoder used = encoderCaptor.getValue();
        assertInstanceOf(BCryptPasswordEncoder.class, used);

        // And finally build() must have been invoked
        verify(authBuilder).build();
    }


    @Test
    void filterChain_configuresHttpSecurityCorrectly() throws Exception {
        // --- Arrange ---
        when(httpSecurity.csrf(csrfCustomizerCaptor.capture())).thenReturn(httpSecurity);
        when(httpSecurity.sessionManagement(sessionMgmtCustomizerCaptor.capture())).thenReturn(httpSecurity);
        when(httpSecurity.authorizeHttpRequests(authorizeRequestsCustomizerCaptor.capture()))
                .thenReturn(httpSecurity);
        when(httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class))
                .thenReturn(httpSecurity);
        doReturn(securityFilterChain).when(httpSecurity).build();

        // --- Act ---
        SecurityFilterChain result = securityConfig.filterChain(httpSecurity);

        // --- Assert ---
        assertSame(securityFilterChain, result);

        // 1) CSRF disabled
        Customizer<CsrfConfigurer<HttpSecurity>> csrfCustomizer = csrfCustomizerCaptor.getValue();
        csrfCustomizer.customize(csrfConfigurer);
        verify(csrfConfigurer).disable();

        // 2) Session management stateless
        Customizer<SessionManagementConfigurer<HttpSecurity>> sessionCustomizer =
                sessionMgmtCustomizerCaptor.getValue();
        sessionCustomizer.customize(sessionManagementConfigurer);
        verify(sessionManagementConfigurer)
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // 3) Authorization rules
        // Stub the chained calls inside the lambda
        when(authRegistry.requestMatchers(
                "/auth/register", "/auth/login", "/auth/validate"))
                .thenReturn(authorizedUrlForMatchers);
        when(authorizedUrlForMatchers.permitAll()).thenReturn(authRegistry);
        when(authRegistry.anyRequest()).thenReturn(authorizedUrlForAny);
        when(authorizedUrlForAny.authenticated()).thenReturn(authRegistry);

        Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry>
                authCustomizer = authorizeRequestsCustomizerCaptor.getValue();
        authCustomizer.customize(authRegistry);

        verify(authRegistry)
                .requestMatchers("/auth/register", "/auth/login", "/auth/validate");
        verify(authorizedUrlForMatchers).permitAll();
        verify(authRegistry).anyRequest();
        verify(authorizedUrlForAny).authenticated();

        // 4) JWT filter added and build() invoked
        verify(httpSecurity).addFilterBefore(
                jwtFilter, UsernamePasswordAuthenticationFilter.class);
        verify(httpSecurity).build();
    }
    @Test
    void passwordEncoder_shouldBeBCryptPasswordEncoder() {
        PasswordEncoder encoder = config.passwordEncoder();
        assertThat(encoder).isInstanceOf(BCryptPasswordEncoder.class);
    }

    @Test
    void passwordEncoder_encodesAndMatchesRawPassword() {
        PasswordEncoder encoder = config.passwordEncoder();
        String raw = "mySecret123";
        String encoded = encoder.encode(raw);

        // It should not store the raw string directly
        assertThat(encoded).isNotEqualTo(raw);
        // It should successfully match the raw value against its hash
        assertThat(encoder.matches(raw, encoded)).isTrue();
    }

}
