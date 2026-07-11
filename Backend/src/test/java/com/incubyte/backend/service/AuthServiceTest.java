package com.incubyte.backend.service;

import com.incubyte.backend.dto.AuthResponse;
import com.incubyte.backend.dto.LoginRequest;
import com.incubyte.backend.entity.User;
import com.incubyte.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtService);
    }

    @Test
    @DisplayName("Successful login should return AuthResponse with JWT token")
    void testLogin_Success() {
        // Arrange
        String email = "user@example.com";
        String password = "Password123!";
        String hashedPassword = "hashedPassword";
        String token = "jwt.token.here";

        LoginRequest request = new LoginRequest(email, password);
        User user = User.builder()
                .id(1L)
                .email(email)
                .password(hashedPassword)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);
        when(jwtService.generateToken(email)).thenReturn(token);

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals(token, response.getToken());
        assertEquals(email, response.getEmail());

        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, hashedPassword);
        verify(jwtService).generateToken(email);
    }

    @Test
    @DisplayName("Login with non-existent email should throw BadCredentialsException")
    void testLogin_EmailNotFound() {
        // Arrange
        String email = "notfound@example.com";
        LoginRequest request = new LoginRequest(email, "Password123!");

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authService.login(request));

        verify(userRepository).findByEmail(email);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    @DisplayName("Login with incorrect password should throw BadCredentialsException")
    void testLogin_IncorrectPassword() {
        // Arrange
        String email = "user@example.com";
        String wrongPassword = "wrongPassword";
        String hashedPassword = "hashedPassword";

        LoginRequest request = new LoginRequest(email, wrongPassword);
        User user = User.builder()
                .id(1L)
                .email(email)
                .password(hashedPassword)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(wrongPassword, hashedPassword)).thenReturn(false);

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authService.login(request));

        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(wrongPassword, hashedPassword);
        verify(jwtService, never()).generateToken(anyString());
    }
}
