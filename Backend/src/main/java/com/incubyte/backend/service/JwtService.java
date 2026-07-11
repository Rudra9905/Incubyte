package com.incubyte.backend.service;

import org.springframework.stereotype.Service;

@Service
public class JwtService {

    public String generateToken(String email) {
        throw new UnsupportedOperationException("JWT generation logic is not implemented yet.");
    }

    public String extractEmail(String token) {
        throw new UnsupportedOperationException("JWT token extraction is not implemented yet.");
    }

    public boolean isTokenValid(String token, String email) {
        throw new UnsupportedOperationException("JWT token validation is not implemented yet.");
    }
}
