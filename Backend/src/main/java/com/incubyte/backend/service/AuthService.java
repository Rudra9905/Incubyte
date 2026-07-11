package com.incubyte.backend.service;

import com.incubyte.backend.dto.AuthResponse;
import com.incubyte.backend.dto.LoginRequest;
import com.incubyte.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse login(LoginRequest request) {
        throw new UnsupportedOperationException("Login logic is not implemented yet.");
    }
}
