package com.incubyte.backend.service;

import com.incubyte.backend.dto.RegistrationRequest;
import com.incubyte.backend.dto.RegistrationResponse;
import com.incubyte.backend.entity.User;
import com.incubyte.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    public RegistrationResponse register(RegistrationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        validateEmail(request.getEmail());
        validatePassword(request.getPassword());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new com.incubyte.backend.exception.DuplicateEmailException("Email already exists: " + request.getEmail());
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .email(request.getEmail())
                .password(hashedPassword)
                .build();

        User savedUser = userRepository.save(user);

        return new RegistrationResponse(savedUser.getId(), savedUser.getEmail());
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty() || !email.matches(EMAIL_REGEX)) {
            throw new com.incubyte.backend.exception.InvalidEmailException("Invalid email format");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new com.incubyte.backend.exception.WeakPasswordException("Password must be at least 8 characters long");
        }

        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowercase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecial = true;
            }
        }

        if (!hasUppercase || !hasLowercase || !hasDigit || !hasSpecial) {
            throw new com.incubyte.backend.exception.WeakPasswordException(
                    "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
            );
        }
    }
}
