package com.pro.authenticationservice.security;



import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;


import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;


    @BeforeEach
    void setUp() {
        // Clear context before each test
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilter_internal_withValidToken_shouldAuthenticateAndContinueChain() throws ServletException, IOException {
        // given
        String token = "valid-token";
        String header = "Bearer " + token;
        String username = "john.doe";

        when(request.getHeader("Authorization")).thenReturn(header);
        when(jwtUtils.validateToken(token)).thenReturn(true);
        when(jwtUtils.getUsername(token)).thenReturn(username);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then the chain always continues
        verify(filterChain).doFilter(request, response);

        // verify authentication was set
        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isInstanceOf(UsernamePasswordAuthenticationToken.class);

        UsernamePasswordAuthenticationToken auth =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        assertThat(auth.getPrincipal()).isEqualTo(userDetails);
        assertThat(auth.getCredentials()).isNull();
        assertThat(auth.getAuthorities()).isEmpty();
        assertThat(auth.getDetails()).isInstanceOf(WebAuthenticationDetails.class);

    }

    @Test
    void doFilter_internal_withInvalidToken_shouldNotAuthenticateAndContinueChain() throws ServletException, IOException {
        // given
        String token = "invalid-token";
        String header = "Bearer " + token;

        when(request.getHeader("Authorization")).thenReturn(header);
        when(jwtUtils.validateToken(token)).thenReturn(false);

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtUtils, never()).getUsername(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    void doFilter_internal_withoutHeader_shouldNotAuthenticateAndContinueChain() throws ServletException, IOException {
        // given
        when(request.getHeader("Authorization")).thenReturn(null);

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtUtils, never()).validateToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }
}
