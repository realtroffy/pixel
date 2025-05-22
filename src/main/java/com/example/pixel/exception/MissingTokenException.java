package com.example.pixel.exception;

import org.springframework.security.core.AuthenticationException;

public class MissingTokenException extends AuthenticationException {
    public MissingTokenException(String message, Throwable cause) {
        super(message, cause);
    }
    public MissingTokenException(String message) {
        super(message);
    }
}
