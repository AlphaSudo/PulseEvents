package com.pro.apigatewayservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.server.SecurityWebFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebFluxSecurity

public class GatewaySecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges ->
                        exchanges
                                .pathMatchers("/auth/**").permitAll()
                                .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                // <— new method name is jwtDecoder(), not decoder()
                                jwt.jwtDecoder(jwtDecoder())
                        )
                );

        ;
        return http.build();
    }


    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        // e.g. if you use an HMAC secret
        return NimbusReactiveJwtDecoder.withSecretKey(
                new SecretKeySpec("your-256-bit-secret".getBytes(), "HMACSHA256")
        ).build();
    }
}