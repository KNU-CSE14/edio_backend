package com.edio.common.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends BaseException {
    public <T> AuthenticationException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }

    public <T> AuthenticationException(String message, Throwable cause) {
        super(HttpStatus.UNAUTHORIZED, cause.getMessage());
    }
}
