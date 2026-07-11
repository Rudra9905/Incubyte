package com.incubyte.backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${app.jwt.secret:dummy_secret_key_for_testing_purposes_only_do_not_use_in_production_environments}")
    private String secretKey = "dummy_secret_key_for_testing_purposes_only_do_not_use_in_production_environments";

    @Value("${app.jwt.expiration-ms:86400000}")
    private long jwtExpirationMs = 86400000L;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateToken(String email, String role) {
        return generateToken(email);
    }

    public String extractRole(String token) {
        return null;
    }

    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, String email) {
        try {
            Claims claims = getClaims(token);
            String tokenSubject = claims.getSubject();
            boolean isExpired = claims.getExpiration().before(new Date());
            return (tokenSubject.equals(email) && !isExpired);
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
