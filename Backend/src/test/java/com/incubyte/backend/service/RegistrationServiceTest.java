package com.incubyte.backend.service;

import com.incubyte.backend.dto.RegistrationRequest;
import com.incubyte.backend.dto.RegistrationResponse;
import com.incubyte.backend.entity.Role;
import com.incubyte.backend.entity.User;
import com.incubyte.backend.exception.DuplicateEmailException;
import com.incubyte.backend.exception.InvalidEmailException;
import com.incubyte.backend.exception.WeakPasswordException;
import com.incubyte.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private RegistrationService registrationService;

    @BeforeEach
    void setUp() {
        registrationService = new RegistrationService(userRepository, passwordEncoder, new RegistrationValidator());
    }

    @Test
    @DisplayName("Successful registration should save user with default USER role and encode password")
    void testRegister_Successful() {
        String email = "success@example.com";
        String rawPassword = "StrongPass123!";
        String hashedPassword = "hashed_password_123";

        RegistrationRequest request = new RegistrationRequest(email, rawPassword);
        
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(rawPassword)).thenReturn(hashedPassword);
        
        User savedUser = User.builder()
                .id(1L)
                .email(email)
                .password(hashedPassword)
                .role(Role.USER)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        RegistrationResponse response = registrationService.register(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(email, response.getEmail());
        assertEquals("USER", response.getRole());

        verify(passwordEncoder, times(1)).encode(rawPassword);
        
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        
        User capturedUser = userCaptor.getValue();
        assertEquals(email, capturedUser.getEmail());
        assertEquals(hashedPassword, capturedUser.getPassword());
        assertEquals(Role.USER, capturedUser.getRole());
    }

    @Test
    @DisplayName("Registration with custom ADMIN role should save user as ADMIN")
    void testRegister_AdminSuccessful() {
        String email = "admin@example.com";
        String rawPassword = "StrongPass123!";
        String hashedPassword = "hashed_password_123";

        RegistrationRequest request = new RegistrationRequest(email, rawPassword, "ADMIN");

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(rawPassword)).thenReturn(hashedPassword);

        User savedUser = User.builder()
                .id(2L)
                .email(email)
                .password(hashedPassword)
                .role(Role.ADMIN)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        RegistrationResponse response = registrationService.register(request);

        assertNotNull(response);
        assertEquals(2L, response.getId());
        assertEquals(email, response.getEmail());
        assertEquals("ADMIN", response.getRole());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertEquals(Role.ADMIN, capturedUser.getRole());
    }

    @Test
    @DisplayName("Registration with duplicate email should throw DuplicateEmailException")
    void testRegister_DuplicateEmail() {
        String email = "duplicate@example.com";
        String rawPassword = "StrongPass123!";
        RegistrationRequest request = new RegistrationRequest(email, rawPassword);

        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> registrationService.register(request));
        
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "plainaddress",
        "#@%^%#$@#$@#.com",
        "@example.com",
        "Joe Smith <email@example.com>",
        "email.example.com",
        "email@example@example.com",
        "email@example.com (Joe Smith)"
    })
    @NullAndEmptySource
    @DisplayName("Registration with invalid email format should throw InvalidEmailException")
    void testRegister_InvalidEmail(String invalidEmail) {
        RegistrationRequest request = new RegistrationRequest(invalidEmail, "StrongPass123!");

        assertThrows(InvalidEmailException.class, () -> registrationService.register(request));

        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "short",                   
        "no_uppercase_123!",       
        "NO_LOWERCASE_123!",       
        "NoDigitsOrSpecial",       
        "NoSpecial123",
        "NoUppercaseAndSpecial12"  
    })
    @NullAndEmptySource
    @DisplayName("Registration with weak password should throw WeakPasswordException")
    void testRegister_WeakPassword(String weakPassword) {
        RegistrationRequest request = new RegistrationRequest("valid@example.com", weakPassword);

        assertThrows(WeakPasswordException.class, () -> registrationService.register(request));

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Verify that raw password is never stored or logged and is properly hashed")
    void testRegister_PasswordHashingExpectations() {
        String email = "hashcheck@example.com";
        String rawPassword = "SuperSecurePassword99!";
        String hashedPassword = "super_secure_hashed_99";
        
        RegistrationRequest request = new RegistrationRequest(email, rawPassword);
        
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(rawPassword)).thenReturn(hashedPassword);
        
        User savedUser = User.builder()
                .id(2L)
                .email(email)
                .password(hashedPassword)
                .role(Role.USER)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        registrationService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        
        User capturedUser = userCaptor.getValue();
        assertNotEquals(rawPassword, capturedUser.getPassword());
        assertEquals(hashedPassword, capturedUser.getPassword());
    }
}
