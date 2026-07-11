package com.incubyte.backend.service;

import com.incubyte.backend.exception.InvalidEmailException;
import com.incubyte.backend.exception.WeakPasswordException;
import org.springframework.stereotype.Component;

@Component
public class RegistrationValidator {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final int MIN_PASSWORD_LENGTH = 8;

    public void validateEmail(String email) {
        if (email == null || email.trim().isEmpty() || !email.matches(EMAIL_REGEX)) {
            throw new InvalidEmailException("Invalid email format");
        }
    }

    public void validatePassword(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw new WeakPasswordException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long");
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
            throw new WeakPasswordException(
                    "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
            );
        }
    }
}
