package com.pro.authenticationservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        System.out.println("JwtAuthenticationFilter: Processing request to " + request.getRequestURI());
        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization header: " + (authHeader != null ? "present" : "not present"));

        String token = null;
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            System.out.println("Token extracted from header");
        } else {
            System.out.println("No valid token found in header");
        }

        if (token != null && jwtUtils.validateToken(token)) {
            System.out.println("Token is valid");
            try {
                String username = jwtUtils.getUsername(token);
                System.out.println("Username from token: " + username);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                System.out.println("User details loaded: " + userDetails.getUsername());
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("Authentication set in SecurityContextHolder");
            } catch (Exception e) {
                System.out.println("Error during authentication: " + e.getMessage());
                e.printStackTrace();
            }
        } else if (token != null) {
            System.out.println("Token is not valid");
        }
        filterChain.doFilter(request, response);
    }
}
