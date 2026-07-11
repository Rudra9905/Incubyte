package com.incubyte.backend.service;

import com.incubyte.backend.dto.RegistrationRequest;
import com.incubyte.backend.dto.RegistrationResponse;
import com.incubyte.backend.entity.User;
import com.incubyte.backend.exception.DuplicateEmailException;
import com.incubyte.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RegistrationValidator registrationValidator;

    public RegistrationService(UserRepository userRepository, 
                               PasswordEncoder passwordEncoder, 
                               RegistrationValidator registrationValidator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.registrationValidator = registrationValidator;
    }

    public RegistrationResponse register(RegistrationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        registrationValidator.validateEmail(request.getEmail());
        registrationValidator.validatePassword(request.getPassword());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("Email already exists: " + request.getEmail());
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .email(request.getEmail())
                .password(hashedPassword)
                .build();

        User savedUser = userRepository.save(user);

        return new RegistrationResponse(savedUser.getId(), savedUser.getEmail());
    }
}
