package com.incubyte.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.incubyte.backend.dto.AuthResponse;
import com.incubyte.backend.dto.LoginRequest;
import com.incubyte.backend.dto.RegistrationRequest;
import com.incubyte.backend.dto.RegistrationResponse;
import com.incubyte.backend.exception.DuplicateEmailException;
import com.incubyte.backend.exception.InvalidEmailException;
import com.incubyte.backend.exception.WeakPasswordException;
import com.incubyte.backend.service.AuthService;
import com.incubyte.backend.service.RegistrationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegistrationService registrationService;

    @MockBean
    private AuthService authService;

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/register - Success should return 201 Created")
    void testRegister_Success() throws Exception {
        RegistrationRequest request = new RegistrationRequest("test@example.com", "Password123!");
        RegistrationResponse response = new RegistrationResponse(1L, "test@example.com");

        when(registrationService.register(any(RegistrationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/register - Duplicate Email should return 400 Bad Request")
    void testRegister_DuplicateEmail() throws Exception {
        RegistrationRequest request = new RegistrationRequest("duplicate@example.com", "Password123!");

        when(registrationService.register(any(RegistrationRequest.class)))
                .thenThrow(new DuplicateEmailException("Email already exists"));

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/register - Invalid Email should return 400 Bad Request")
    void testRegister_InvalidEmail() throws Exception {
        RegistrationRequest request = new RegistrationRequest("invalid", "Password123!");

        when(registrationService.register(any(RegistrationRequest.class)))
                .thenThrow(new InvalidEmailException("Invalid email format"));

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid email format"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/register - Weak Password should return 400 Bad Request")
    void testRegister_WeakPassword() throws Exception {
        RegistrationRequest request = new RegistrationRequest("test@example.com", "weak");

        when(registrationService.register(any(RegistrationRequest.class)))
                .thenThrow(new WeakPasswordException("Password must be at least 8 characters"));

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Password must be at least 8 characters"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/login - Success should return 200 OK with JWT token")
    void testLogin_Success() throws Exception {
        LoginRequest request = new LoginRequest("test@example.com", "Password123!");
        AuthResponse response = new AuthResponse("mock-jwt-token", "test@example.com");

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/login - Bad Credentials should return 401 Unauthorized")
    void testLogin_BadCredentials() throws Exception {
        LoginRequest request = new LoginRequest("test@example.com", "wrong-password");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Invalid email or password"));

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }
}
