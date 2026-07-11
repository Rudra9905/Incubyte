package com.incubyte.backend.service;

import org.springframework.stereotype.Service;

@Service
public class JwtService {

    public String generateToken(String email) {
        throw new UnsupportedOperationException("JWT Token generation is not implemented yet.");
    }

    public String extractUsername(String token) {
        throw new UnsupportedOperationException("JWT Token username extraction is not implemented yet.");
    }

    public boolean isTokenValid(String token, String username) {
        throw new UnsupportedOperationException("JWT Token validation is not implemented yet.");
    }
}
