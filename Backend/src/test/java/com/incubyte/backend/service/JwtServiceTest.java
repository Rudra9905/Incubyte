package com.incubyte.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
    }

    @Test
    @DisplayName("generateToken should create a valid non-empty JWT token containing user email")
    void testGenerateAndValidateToken() {
        String email = "user@example.com";

        String token = jwtService.generateToken(email);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(email, jwtService.extractEmail(token));
        assertTrue(jwtService.isTokenValid(token, email));
    }

    @Test
    @DisplayName("generateToken with role should embed role claim and extract it")
    void testTokenRoleClaim() {
        String email = "admin@example.com";
        String role = "ADMIN";

        String token = jwtService.generateToken(email, role);

        assertNotNull(token);
        assertEquals(role, jwtService.extractRole(token));
    }

    @Test
    @DisplayName("isTokenValid should return false if email does not match token subject")
    void testTokenInvalidForDifferentEmail() {
        String email = "user@example.com";
        String token = jwtService.generateToken(email);

        assertFalse(jwtService.isTokenValid(token, "other@example.com"));
    }
}
