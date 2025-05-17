package com.pro.authenticationservice.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {
    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtUtils(
            @Value("${spring.jwt.secret}") String secret,
            @Value("${spring.jwt.expiration-ms}") long expirationMs
    ) {
        System.out.println("Initializing JwtUtils with secret length: " + (secret != null ? secret.length() : 0));
        System.out.println("Expiration time: " + expirationMs + " ms");
        try {
            this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
            System.out.println("Signing key created successfully");
        } catch (Exception e) {
            System.out.println("Error creating signing key: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        this.expirationMs = expirationMs;
    }

    public String generateToken(String username, Collection<String> roles) {
        System.out.println("Generating token for user: " + username);
        System.out.println("Roles: " + roles);
        try {
            Date now = new Date();
            Date exp = new Date(now.getTime() + expirationMs);
            System.out.println("Token expiration: " + exp);

            String token = Jwts.builder()
                    .subject(username)
                    .claim("roles", roles)
                    .issuedAt(now)
                    .expiration(exp)
                    .signWith(signingKey, Jwts.SIG.HS256)
                    .compact();
            System.out.println("Token generated successfully");
            return token;
        } catch (Exception e) {
            System.out.println("Error generating token: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public boolean validateToken(String token) {
        System.out.println("Validating token");
        if (token == null) {
            System.out.println("Token is null");
            return false;
        }
        try {
            parseClaims(token);
            System.out.println("Token is valid");
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            System.out.println("Token validation failed: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    public Jws<Claims> parseClaims(String token) {
        System.out.println("Parsing claims from token");
        try {
            Jws<Claims> claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token);
            System.out.println("Claims parsed successfully");
            System.out.println("Subject: " + claims.getPayload().getSubject());
            System.out.println("Roles: " + claims.getPayload().get("roles"));
            return claims;
        } catch (Exception e) {
            System.out.println("Error parsing claims: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public String getUsername(String token) {
        return parseClaims(token).getPayload().getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        return (List<String>) parseClaims(token).getPayload().get("roles");
    }
}
