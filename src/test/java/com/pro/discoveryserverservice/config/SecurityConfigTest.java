package com.pro.discoveryserverservice.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private HttpSecurity httpSecurity;

    @Mock
    private CsrfConfigurer<HttpSecurity> csrfConfigurer;

    @Mock
    private AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authRegistry;

    // Mock for the object returned by anyRequest()
    @Mock
    private AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizedUrl authorizedUrl;

    @Mock
    private SecurityFilterChain securityFilterChain; // Mock for the result of httpSecurity.build()


    @InjectMocks // Creates an instance of SecurityConfig and injects the mocks into it
    private SecurityConfig securityConfig;

    // ArgumentCaptor allows us to capture the lambda/customizer passed to methods
    @Captor
    private ArgumentCaptor<Customizer<CsrfConfigurer<HttpSecurity>>> csrfCustomizerCaptor;

    @Captor
    private ArgumentCaptor<Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry>> authorizeRequestsCustomizerCaptor;

    @Test
    void filterChain_shouldConfigureCsrfAndPermitAllRequests() throws Exception {
        // --- Arrange ---

        // Mock the behavior of HttpSecurity's fluent API:
        // 1. When httpSecurity.csrf(customizer) is called, capture the customizer and return httpSecurity.
        when(httpSecurity.csrf(csrfCustomizerCaptor.capture())).thenReturn(httpSecurity);

        // 2. When httpSecurity.authorizeHttpRequests(customizer) is called, capture the customizer and return httpSecurity.
        when(httpSecurity.authorizeHttpRequests(authorizeRequestsCustomizerCaptor.capture())).thenReturn(httpSecurity);

        // 3. When httpSecurity.build() is called, return a mock SecurityFilterChain.
        doReturn(securityFilterChain).when(httpSecurity).build();


        // --- Act ---
        // Call the method we are testing
        securityConfig.filterChain(httpSecurity);


        // --- Assert ---

        // 1. Verify CSRF configuration:
        // Get the captured CSRF customizer (the lambda: csrf -> csrf.disable())
        Customizer<CsrfConfigurer<HttpSecurity>> csrfCustomizer = csrfCustomizerCaptor.getValue();
        // Execute this customizer with our mock CsrfConfigurer
        csrfCustomizer.customize(csrfConfigurer);
        // Verify that disable() was called on the CsrfConfigurer mock
        verify(csrfConfigurer).disable();

        // 2. Verify authorizeHttpRequests configuration:
        // Get the captured authorizeHttpRequests customizer (the lambda: authz -> authz.anyRequest().permitAll())
        Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> authCustomizer = authorizeRequestsCustomizerCaptor.getValue();

        // Before executing the customizer, set up the mocks for the calls made *inside* the customizer:
        // When authRegistry.anyRequest() is called (inside the lambda), return our mock `authorizedUrl`
        when(authRegistry.anyRequest()).thenReturn(authorizedUrl);

        // Now, execute the customizer with our mock AuthorizationManagerRequestMatcherRegistry
        authCustomizer.customize(authRegistry);

        // Verify that authRegistry.anyRequest() was called
        verify(authRegistry).anyRequest();
        // Verify that permitAll() was called on the object returned by anyRequest() (our authorizedUrl mock)
        verify(authorizedUrl).permitAll();

        // 3. Verify that build() was called on HttpSecurity
        verify(httpSecurity).build();
    }
}
